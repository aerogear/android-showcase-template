package com.feedhenry.securenativeandroidtemplate.authenticate;

/**
 * Created by weili on 04/09/2017.
 */

public class SimpleAuthenticationProviderImpl implements SimpleAuthenticationProvider {

    @Override
    public AuthenticateResult authenticateWithUsernameAndPassword(String username, String password) {
        AuthenticateResult loginState = new AuthenticateResult(false);

        try {
            // Simulate network access.
            Thread.sleep(2000);
            // simulate login success
            loginState.setResult(true);
        } catch (InterruptedException e) {
            return null;
        }
        return loginState;
    }
}