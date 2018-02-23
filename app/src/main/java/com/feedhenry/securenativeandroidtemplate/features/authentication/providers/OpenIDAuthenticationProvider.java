package com.feedhenry.securenativeandroidtemplate.features.authentication.providers;
import android.app.Activity;
import android.content.Intent;

import com.feedhenry.securenativeandroidtemplate.domain.callbacks.CallbackHandler;

import org.aerogear.mobile.auth.Callback;


/**
 * An interface for OpenID Connect Authentication Providers
 */
public interface OpenIDAuthenticationProvider {

        /**
         * Perform inital auth request to the auth endpoint
         * @param fromActivity - the activity to use
         * @param authCallback - the authentication callback
         */
        public void login(Activity fromActivity, Callback authCallback);

        /**
         * Perform the logout flow
         *
         */
        public void logout(CallbackHandler logoutCallback);

}