package com.feedhenry.securenativeandroidtemplate.features.authentication.presenters;

import com.feedhenry.securenativeandroidtemplate.MainActivity;
import com.feedhenry.securenativeandroidtemplate.domain.callbacks.Callback;
import com.feedhenry.securenativeandroidtemplate.features.authentication.providers.OpenIDAuthenticationProvider;
import com.feedhenry.securenativeandroidtemplate.features.authentication.views.AuthenticationDetailsView;
import com.feedhenry.securenativeandroidtemplate.mvp.presenters.BasePresenter;

import net.openid.appauth.AuthState;

import javax.inject.Inject;

/**
 * Created by weili on 12/09/2017.
 */

public class AuthenticationDetailsPresenter extends BasePresenter<AuthenticationDetailsView> {

    OpenIDAuthenticationProvider authProvider;
    MainActivity mainActivity;

    @Inject
    public AuthenticationDetailsPresenter(OpenIDAuthenticationProvider authProviderImpl, MainActivity mainActivity) {
        this.authProvider = authProviderImpl;
        this.mainActivity = mainActivity;
    }

    public void logout() {
        authProvider.logout(new Callback() {
            @Override
            public void onSuccess(Object state) {
                AuthState authState = (AuthState) state;
                view.logoutSuccess(authState);
            }

            @Override
            public void onError(Throwable error) {
                view.logoutFailure((Exception) error);
            }
        });
    }
}
