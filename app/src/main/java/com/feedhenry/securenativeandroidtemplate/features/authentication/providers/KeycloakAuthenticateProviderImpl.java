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
import com.feedhenry.securenativeandroidtemplate.mvp.components.AuthHelper;

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

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by tjackman on 9/8/17.
 */

@Singleton
public class KeycloakAuthenticateProviderImpl implements OpenIDAuthenticationProvider {

    private static final Uri AUTHORIZATION_ENDPOINT = Constants.KEYCLOAK_CONFIG.AUTHORIZATION_ENDPOINT;
    private static final Uri TOKEN_ENDPOINT = Constants.KEYCLOAK_CONFIG.TOKEN_ENDPOINT;
    private static final String CLIENT_ID = Constants.KEYCLOAK_CONFIG.CLIENT_ID;
    private static final Uri REDIRECT_URI = Constants.KEYCLOAK_CONFIG.REDIRECT_URI;
    private static final String OPEN_ID_SCOPE = Constants.KEYCLOAK_CONFIG.OPEN_ID_SCOPE;

    private AuthState authState;
    private AuthorizationService authService;
    private AuthorizationRequest authRequest;
    private AuthorizationServiceConfiguration serviceConfig;
    private static boolean logoutSuccess = false;
    private Callback authCallback;
    private AuthHelper authHelper;

    @Inject
    Context context;

    @Inject
    public KeycloakAuthenticateProviderImpl(@NonNull Context context) {
        this.context = context;
        this.authHelper = new AuthHelper(context);
    }

    /**
     * Create the config for the initial Keycloak auth request to get a temporary token and create an intent to handle the response
     */
    @Override
    public void performAuthRequest(Activity fromActivity, Callback authCallback) {
        this.authCallback = authCallback;
        // Setup the config for the AuthorizationService
        serviceConfig =
                new AuthorizationServiceConfiguration(
                        AUTHORIZATION_ENDPOINT, // the clients keycloak authorization endpoint
                        TOKEN_ENDPOINT); // the clients keycloak token endpoint

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
                        CLIENT_ID, // the client ID, typically pre-registered and static
                        ResponseTypeValues.CODE, // the response_type value: we want a code
                        REDIRECT_URI) // the redirect URI to which the auth response is sent
                        .setScopes(OPEN_ID_SCOPE);

        authRequest = authRequestBuilder.build();
        // perform the auth request
        Intent authIntent = authService.getAuthorizationRequestIntent(authRequest);
        fromActivity.startActivityForResult(authIntent, Constants.REQUEST_CODES.AUTH_CODE);

    }

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

    /**
     * Token exchange against the token endpoint
     *
     * @param response - the auth response from the intent/server
     */
    public void exchangeTokens(AuthorizationResponse response) {
        authService.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
            @Override
            public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                if (tokenResponse != null) {
                    authState.update(tokenResponse, exception);
                    authHelper.writeAuthState(authState);
                    authSuccess(authState);
                } else {
                    authFailed(exception);
                }
            }
        });
    }

    /**
     * Perform a logout
     */
    public void logout(Callback logoutCallback) {
        String baseLogoutEndpoint = Constants.KEYCLOAK_CONFIG.LOGOUT_ENDPOINT;
        String identityToken = authHelper.getIdentityToken();
        String redirectUri = Constants.KEYCLOAK_CONFIG.REDIRECT_URI.toString();

        // Construct the Keycloak logout URL
        String logoutRequestUri = baseLogoutEndpoint + "?id_token_hint=" + identityToken + "&redirect_uri=" +redirectUri;

        authHelper.makeBearerRequest(logoutRequestUri, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logoutSuccess = false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                logoutSuccess = true;
                authHelper.writeAuthState(null);
            }
        });

        if (logoutSuccess) {
            logoutCallback.onSuccess(null);
        } else {
            logoutCallback.onError(null);
        }
    }

    private void authSuccess(AuthState authState) {
        if (this.authCallback != null) {
            authCallback.onSuccess(authState);
        }
    }

    private void authFailed(Exception error) {
        Log.w("", context.getString(R.string.authentication_failed), error);
        if (this.authCallback != null) {
            authCallback.onError(error);
        }
    }
}
