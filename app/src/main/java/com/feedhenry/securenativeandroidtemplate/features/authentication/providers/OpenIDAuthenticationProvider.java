package com.feedhenry.securenativeandroidtemplate.features.authentication.providers;
import android.app.Activity;

import org.aerogear.mobile.core.Callback;


/**
 * An interface for OpenID Connect Authentication Providers
 */
public interface OpenIDAuthenticationProvider {

    /**
     * Perform a login to the OIDC server
     * @param fromActivity - the activity to use for authenticating
     * @param authCallback - the authentication callback
     */
    void login(Activity fromActivity, Callback authCallback);

    /**
     * Perform a logout against the OIDC server
     *
     * @param logoutCallback the logout callback
     */
    void logout(Callback logoutCallback);

}