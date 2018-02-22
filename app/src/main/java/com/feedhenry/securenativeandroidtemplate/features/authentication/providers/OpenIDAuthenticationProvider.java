package com.feedhenry.securenativeandroidtemplate.features.authentication.providers;
import android.app.Activity;
import android.content.Intent;


/**
 * An interface for OpenID Connect Authentication Providers
 */
public interface OpenIDAuthenticationProvider {

        /**
         * Perform inital auth request to the auth endpoint
         * @param fromActivity - the activity to use
         */
        public void performAuthRequest(Activity fromActivity);

        /**
         * Perform the logout flow
         *
         */
        public void logout();

        /**
         * Used for check the authentication response from the browser
         *
         * @param intent
         */
        public void onAuthResult(Intent intent);

        /**
         * Handles the initial auth response and create a new request to the token endpoint to get the actual tokens
         *
         * @param intent
         */
        public void handleAuthorizationResponse(Intent intent);
}