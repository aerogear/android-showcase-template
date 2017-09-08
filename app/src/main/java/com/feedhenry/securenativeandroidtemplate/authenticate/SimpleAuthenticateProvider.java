package com.feedhenry.securenativeandroidtemplate.authenticate;

/**
 * Created by weili on 04/09/2017.
 */

/**
 * An interface for Simple Authentication Providers
 */
public interface SimpleAuthenticateProvider {

    /**
     * Perform the authentication request synchronously
     * @return
     */
    public AuthenticateResult performAuthentication(String username, String password);
}
