package com.feedhenry.securenativeandroidtemplate.features.authentication.views;

import com.feedhenry.securenativeandroidtemplate.domain.models.Identity;
import com.feedhenry.securenativeandroidtemplate.mvp.views.AppView;

import net.openid.appauth.AuthState;

/**
 * Created by weili on 12/09/2017.
 */

public interface AuthenticationDetailsView extends AppView {

    public void logoutSuccess(Identity identity);

    public void logoutFailure(Exception error);
}
