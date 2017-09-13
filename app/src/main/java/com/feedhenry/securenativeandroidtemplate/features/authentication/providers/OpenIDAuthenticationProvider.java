package com.feedhenry.securenativeandroidtemplate.features.authentication.providers;
import android.app.Activity;
import android.content.Intent;

import com.feedhenry.securenativeandroidtemplate.domain.callbacks.Callback;

/**
 * An interface for OpenID Connect Authentication Providers
 */
public interface OpenIDAuthenticationProvider {

        /**
         * Perform inital auth request to the auth endpoint
         */
        public void performAuthRequest(Activity fromActivity, Callback authCallback);

        /**
         * Perform the logout flow
         */
        public void logout(Callback logoutCallback);

        /**
         * Used for check the authentication response from the browser
         * @param intent
         */
        public void onAuthResult(Intent intent);

}
