package com.feedhenry.securenativeandroidtemplate.features.authentication.presenters;

import com.feedhenry.securenativeandroidtemplate.MainActivity;
import com.feedhenry.securenativeandroidtemplate.features.authentication.providers.OpenIDAuthenticationProvider;
import com.feedhenry.securenativeandroidtemplate.features.authentication.views.AuthenticationView;
import com.feedhenry.securenativeandroidtemplate.mvp.presenters.BasePresenter;

import org.aerogear.mobile.auth.Callback;
import org.aerogear.mobile.auth.user.UserPrincipal;

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
        authProvider.login(mainActivity, new Callback<UserPrincipal>() {
            @Override
            public void onSuccess(UserPrincipal user) {
                view.renderIdentityInfo(user);
            }

            @Override
            public void onError(Throwable error) {
                view.showAuthError((Exception) error);
            }
        });
    }

}
