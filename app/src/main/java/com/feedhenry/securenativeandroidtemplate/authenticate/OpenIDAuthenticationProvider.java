package com.feedhenry.securenativeandroidtemplate.authenticate;
import android.content.Intent;
import net.openid.appauth.AuthorizationResponse;

/**
 * Created by tjackman on 9/8/17.
 */

/**
 * An interface for OpenID Connect Authentication Providers
 */
public interface OpenIDAuthenticationProvider {

        /**
         * Perform inital auth request to the auth endpoint
         */
        public void performAuthRequest();

        /**
         * Perform the logout flow
         */
        public void logout();

}
