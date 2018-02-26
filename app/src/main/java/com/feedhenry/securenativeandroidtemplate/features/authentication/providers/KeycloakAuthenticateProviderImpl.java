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

    public static int LOGIN_RESULT_CODE = 1;
    private CallbackHandler logoutCallback;

    @Inject
    Context context;

    @Inject
    AuthService authService;

    @Inject
    public KeycloakAuthenticateProviderImpl(@NonNull final Context context) {
        this.context = context;
    }

    // tag::performAuthRequest[]
    /**
     * Create the config for the initial Keycloak auth request to get a temporary token and create an intent to handle the response
     *
     * @param fromActivity the activity used to perform the login
     * @param authCallback the authentication callback
     */
    @Override
    public void login(final Activity fromActivity, final Callback authCallback) {

        // Build the options object and start the authentication flow. Provide an activity to handle the auth response.
        OIDCAuthenticateOptions options = new OIDCAuthenticateOptions(fromActivity, LOGIN_RESULT_CODE);
        authService.login(options, authCallback);
    }
    // end::performAuthRequest[]

    // tag::logout[]
    /**
     * Perform a logout request against the openid connect server
     *
     * @param logoutCallback the logout callback
     */
    public void logout(final CallbackHandler logoutCallback) {
        this.logoutCallback = logoutCallback;
        UserPrincipal currentUser = authService.currentUser();
        authService.logout(currentUser);
        logoutSuccess();
    }
    // end::logout[]

    /**
     * Handler for a successful logout
     */
    private void logoutSuccess() {
        Log.w("", context.getString(R.string.logout_success));
        if (this.logoutCallback != null) {
            logoutCallback.onSuccess(null);
        }
    }

    /**
     * Handler for a failed logout
     *
     * @param error the logout error exception
     */
    private void logoutFailed(final Exception error) {
        Log.w("", context.getString(R.string.logout_failed), error);
        if (this.logoutCallback != null) {
            logoutCallback.onError(error);
        }
    }

}
