package com.aerogear.androidshowcase.features.authentication.presenters;

import com.aerogear.androidshowcase.MainActivity;
import com.aerogear.androidshowcase.features.authentication.providers.OpenIDAuthenticationProvider;
import com.aerogear.androidshowcase.features.authentication.views.AuthenticationDetailsView;
import com.aerogear.androidshowcase.mvp.presenters.BasePresenter;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.core.Callback;

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

    public void logout(){
        authProvider.logout(new Callback<UserPrincipal>() {
            @Override
            public void onSuccess(UserPrincipal user) {
                view.logoutSuccess(user);
            }

            @Override
            public void onError(Throwable error) {
                view.logoutFailure((Exception) error);
            }
        });
    }
}
