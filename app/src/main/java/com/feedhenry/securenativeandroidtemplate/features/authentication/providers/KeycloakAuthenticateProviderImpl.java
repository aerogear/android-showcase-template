package com.feedhenry.securenativeandroidtemplate.features.authentication.providers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.Constants;
import com.feedhenry.securenativeandroidtemplate.domain.callbacks.Callback;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.inject.Inject;
import javax.inject.Singleton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by tjackman on 9/8/17.
 */

@Singleton
public class KeycloakAuthenticateProviderImpl implements OpenIDAuthenticationProvider {

    private static final String BASE_SERVER_URI = "https://keycloak-openshift-mobile-security.osm1.skunkhenry.com/auth/realms/secure-app/protocol/openid-connect";
    private static final Uri AUTHORIZATION_ENDPOINT = Uri.parse(BASE_SERVER_URI + "/auth");
    private static final Uri TOKEN_ENDPOINT = Uri.parse(BASE_SERVER_URI + "/token");
    private static final String CLIENT_ID = "client-app";
    private static final Uri REDIRECT_URI = Uri.parse("com.feedhenry.securenativeandroidtemplate:/callback");
    private static final String KEYCLOAK_INTENT = "KEYCLOAK_INTENT";
    private static final String AUTH_CALLBACK_HANDLER = "com.feedhenry.securenativeandroidtemplate.HANDLE_AUTHORIZATION_RESPONSE";
    private static final String OPEN_ID_SCOPE = "openid";

    private AuthState authState;
    private AuthorizationService authService;
    private AuthorizationRequest authRequest;
    private AuthorizationServiceConfiguration serviceConfig;

    private Callback authCallback;

    @Inject
    Context context;

    @Inject
    public KeycloakAuthenticateProviderImpl(){

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
     * @param intent - the intent to check
     */
    public void onAuthResult(@Nullable Intent intent) {
        if (intent != null) {
            handleAuthorizationResponse(intent);
        }
    }

    /**
     * Handles the initial auth response and create a new request to the token endpoint to get the actual tokens
     * @param intent - the auth response intent
     */
    public void handleAuthorizationResponse(Intent intent) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);

        if (response != null) {
            exchangeTokens(response);
        } else {
            authFailed(error);
        }
    }

    /**
     * Token exchange against the token endpoint
     * @param response - the auth response from the intent/server
     */
    public void exchangeTokens(AuthorizationResponse response) {
        authService.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
            @Override
            public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                if (tokenResponse != null) {
                    saveAccessToken(tokenResponse.accessToken);
                    saveIdentityToken(tokenResponse.idToken);
                    authSuccess(tokenResponse);
                }
                else {
                    authFailed(exception);
                }
            }
        });
    }

    /**
     * Save the access token
     * @param accessToken - the OpenID Connect access token
     */
    private void saveAccessToken(String accessToken) {
        saveToFile(accessToken, "accessToken.txt");
    }

    /**
     * Save the identity token
     * @param identityToken - the OpenID Connect identity token
     */
    private void saveIdentityToken(String identityToken) {
        saveToFile(identityToken, "identityToken.txt");
    }

    /**
     * Save data to a file
     * @param data - the data to save to the file
     * @param filename - the filename to save the data in
     */
    private void saveToFile(String data, String filename) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter
                    (context.openFileOutput(filename, MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /**
     * Read data from files and return them in String format
     * @param fileName - the filename to retieve the data from
     */
    public String readFileAsString(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in;

        try {
            in = new BufferedReader(new FileReader(new File(context.getFilesDir(), fileName)));
            while ((line = in.readLine()) != null) stringBuilder.append(line);

        } catch (FileNotFoundException e) {
            Log.e("Exception", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Exception", "File read failed: " + e.toString());
        }

        return stringBuilder.toString();
    }

    /**
     * Perform a logout of the user
     */
    public void logout(Callback logoutCallback) {
        // TODO Perform logout call to keycloak to end session - http://www.keycloak.org/docs/3.0/securing_apps/topics/oidc/oidc-generic.html
        // TODO The user agent can be redirected to the endpoint, in which case the active user session is logged out. Afterward the user agent is redirected back to the application.
        // TODO See Logout Endpoint (the refresh token needs to be included as well as the credentials required to authenticate the client.)

        // TODO: Delete tokens in the storage mechanism we will use

        // TODO: Replace, attempt to perform logout
        boolean logoutSuccess = false;

        if (logoutSuccess) {
            logoutCallback.onSuccess(null);
        } else {
            logoutCallback.onError(null);
        }
    }

    private void authSuccess(TokenResponse token) {
        if (this.authCallback != null) {
            authCallback.onSuccess(token);
        }
    }

    private void authFailed(Exception error) {
        Log.w("", context.getString(R.string.authentication_failed), error);
        if (this.authCallback != null) {
            authCallback.onError(error);
        }
    }
}
