package com.feedhenry.securenativeandroidtemplate.features.authentication.presenters;

import com.feedhenry.securenativeandroidtemplate.MainActivity;
import com.feedhenry.securenativeandroidtemplate.features.authentication.providers.OpenIDAuthenticationProvider;
import com.feedhenry.securenativeandroidtemplate.features.authentication.views.AuthenticationView;
import com.feedhenry.securenativeandroidtemplate.mvp.presenters.BasePresenter;

import javax.inject.Inject;

/**
 * Created by weili on 12/09/2017.
 */

public class AuthenticationViewPresenter extends BasePresenter<AuthenticationView> {

    OpenIDAuthenticationProvider authProvider;
    MainActivity mainActivity;

    @Inject
    public AuthenticationViewPresenter(OpenIDAuthenticationProvider authProviderImpl, MainActivity mainActivity) {
        this.authProvider = authProviderImpl;
        this.mainActivity = mainActivity;
    }

    public void doLogin() {
        authProvider.performAuthRequest(mainActivity);
    }

}
