package com.feedhenry.securenativeandroidtemplate.features.authentication.providers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.Constants;
import com.feedhenry.securenativeandroidtemplate.domain.callbacks.Callback;
import com.feedhenry.securenativeandroidtemplate.domain.configurations.AppConfiguration;
import com.feedhenry.securenativeandroidtemplate.domain.configurations.AuthenticationConfiguration;
import com.feedhenry.securenativeandroidtemplate.domain.models.Identity;
import com.feedhenry.securenativeandroidtemplate.domain.services.AuthStateService;
import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenResponse;
import net.openid.appauth.browser.BrowserBlacklist;
import net.openid.appauth.browser.VersionedBrowserMatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by tjackman on 9/8/17.
 */

@Singleton
public class KeycloakAuthenticateProviderImpl implements OpenIDAuthenticationProvider {

    private static final Uri REDIRECT_URI = Constants.OPEN_ID_CONNECT_CONFIG.REDIRECT_URI;
    private static final String OPEN_ID_SCOPE = Constants.OPEN_ID_CONNECT_CONFIG.OPEN_ID_SCOPE;

    private AuthenticationConfiguration authenticationConfiguration;

    private AuthState authState;
    private AuthorizationService authService;
    private AuthorizationRequest authRequest;
    private AuthorizationServiceConfiguration serviceConfig;
    private Callback authCallback;
    private Callback logoutCallback;
    private AuthStateService authStateService;

    @Inject
    Context context;

    @Inject
    public KeycloakAuthenticateProviderImpl(@NonNull Context context, AppConfiguration appConfiguration, AuthStateService authStateService) {
        this.context = context;
        this.authenticationConfiguration = appConfiguration.getAuthConfiguration();
        this.authStateService = authStateService;
    }

    // tag::performAuthRequest[]
    /**
     * Create the config for the initial Keycloak auth request to get a temporary token and create an intent to handle the response
     */
    @Override
    public void performAuthRequest(Activity fromActivity, Callback authCallback) {
        this.authCallback = authCallback;
        if (!this.authenticationConfiguration.isValid()) {
            this.authCallback.onError(this.authenticationConfiguration.getConfigurationError());
            return;
        }
        // Setup the config for the AuthorizationService
        serviceConfig =
                new AuthorizationServiceConfiguration(
                        this.authenticationConfiguration.getAuthenticationEndpoint(), // the clients keycloak authorization endpoint
                        this.authenticationConfiguration.getTokenEndpoint()); // the clients keycloak token endpoint

        // Persist the AuthorizationServiceConfiguration
        authState = new AuthState(serviceConfig);

        // Prevent the app opening the keycloak view in chrome custom tabs, instead open it in the native browser
        AppAuthConfiguration appAuthConfig = new AppAuthConfiguration.Builder()
                .setBrowserMatcher(new BrowserBlacklist(
                        VersionedBrowserMatcher.CHROME_CUSTOM_TAB))
                .build();

        // create a new auth service with the auth config
        authService = new AuthorizationService(context, appAuthConfig);

        AuthorizationRequest.Builder authRequestBuilder =
                new AuthorizationRequest.Builder(
                        serviceConfig, // the authorization service configuration
                        this.authenticationConfiguration.getClientId(), // the client ID, typically pre-registered and static
                        ResponseTypeValues.CODE, // the response_type value: we want a code
                        REDIRECT_URI) // the redirect URI to which the auth response is sent
                        .setScopes(OPEN_ID_SCOPE);

        authRequest = authRequestBuilder.build();
        // perform the auth request
        Intent authIntent = authService.getAuthorizationRequestIntent(authRequest);
        fromActivity.startActivityForResult(authIntent, Constants.REQUEST_CODES.AUTH_CODE);
    }
    // end::performAuthRequest[]

    // tag::onAuthResult[]
    /**
     * Checks if the incoming intent matches the Keycloak Auth response intent
     *
     * @param intent - the intent to check
     */
    public void onAuthResult(@Nullable Intent intent) {
        if (intent != null) {
            handleAuthorizationResponse(intent);
        }
    }
    // end::onAuthResult[]

    // tag::handleAuthorizationResponse[]
    /**
     * Handles the initial auth response and create a new request to the token endpoint to get the actual tokens
     *
     * @param intent - the auth response intent
     */
    public void handleAuthorizationResponse(Intent intent) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);

        // update the auth state
        authState.update(response, error);

        if (response != null) {
            exchangeTokens(response);
        } else {
            authFailed(error);
        }
    }
    // end::handleAuthorizationResponse[]

    // tag::exchangeTokens[]
    /**
     * Token exchange against the token endpoint
     *
     * @param response - the auth response from the intent/server
     */
    private void exchangeTokens(AuthorizationResponse response) {
        authService.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
            @Override
            public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                if (tokenResponse != null) {
                    authState.update(tokenResponse, exception);
                    authStateService.writeAuthState(authState);

                    JSONObject decodedIdentityData = authStateService.getIdentityInformation();
                    Identity newIdentity = new Identity("", "", "", new ArrayList<String>());

                    try {
                        newIdentity = Identity.fromJson(decodedIdentityData);
                    } catch (JSONException e) {
                        Log.i("","Decoding Access Token Failed", e);
                    }

                    authSuccess(newIdentity);
                } else {
                    authFailed(exception);
                }
            }
        });
    }
    // end::exchangeTokens[]

    // tag::logout[]
    /**
     * Perform a logout request against the openid connect server
     * @param logoutCallback - the logout callback
     */
    public void logout(Callback logoutCallback) {
        this.logoutCallback = logoutCallback;
        String identityToken = authStateService.getIdentityToken();

        // Construct the Keycloak logout URL
        String logoutRequestUri = this.authenticationConfiguration.getLogoutUrl(identityToken, REDIRECT_URI.toString());

        boolean sendAccessToken = false;

        authStateService.createRequest(logoutRequestUri, sendAccessToken, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logoutFailed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // nullify the auth state
                authStateService.writeAuthState(null);
                logoutSuccess();
            }
        });
    }
    // end::logout[]

    private void logoutSuccess() {
        if (this.logoutCallback != null) {
            logoutCallback.onSuccess(null);
        }
    }

    private void logoutFailed(Exception error) {
        Log.w("", context.getString(R.string.logout_failed), error);
        if (this.logoutCallback != null) {
            logoutCallback.onError(error);
        }
    }

    private void authSuccess(Identity identity) {
        if (this.authCallback != null) {
            authCallback.onSuccess(identity);
        }
    }

    private void authFailed(Exception error) {
        Log.w("", context.getString(R.string.authentication_failed), error);
        if (this.authCallback != null) {
            authCallback.onError(error);
        }
    }
}
