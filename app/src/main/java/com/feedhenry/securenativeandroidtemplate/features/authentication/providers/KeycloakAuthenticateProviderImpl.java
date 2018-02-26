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
import com.feedhenry.securenativeandroidtemplate.domain.callbacks.CallbackHandler;
import com.feedhenry.securenativeandroidtemplate.domain.configurations.AppConfiguration;
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

import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.Callback;
import org.aerogear.mobile.auth.authenticator.OIDCAuthenticateOptions;
import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.core.MobileCore;
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

    private AuthState authState;
    private AuthorizationRequest authRequest;
    private AuthorizationServiceConfiguration serviceConfig;
    private AuthStateService authStateService;
    public static int LOGIN_RESULT_CODE = 1;
    private CallbackHandler logoutCallback;

    @Inject
    Context context;

    @Inject
    MobileCore mobileCore;

    @Inject
    AuthService authService;

    @Inject
    public KeycloakAuthenticateProviderImpl(@NonNull Context context, AuthStateService authStateService, MobileCore mobileCore) {
        this.context = context;
        this.authStateService = authStateService;
    }

    // tag::performAuthRequest[]
    /**
     * Create the config for the initial Keycloak auth request to get a temporary token and create an intent to handle the response
     */
    @Override
    public void login(Activity fromActivity, Callback authCallback) {

        // Build the options object and start the authentication flow. Provide an activity to handle the auth response.
        OIDCAuthenticateOptions options = new OIDCAuthenticateOptions(fromActivity, LOGIN_RESULT_CODE);
        authService.login(options, authCallback);
    }
    // end::performAuthRequest[]

    // tag::logout[]
    /**
     * Perform a logout request against the openid connect server
     */
    public void logout(CallbackHandler logoutCallback) {
        this.logoutCallback = logoutCallback;
        UserPrincipal currentUser = authService.currentUser();
        authService.logout(currentUser);
        logoutSuccess();
    }
    // end::logout[]

    private void logoutSuccess() {
        Log.w("", context.getString(R.string.logout_success));
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

}
