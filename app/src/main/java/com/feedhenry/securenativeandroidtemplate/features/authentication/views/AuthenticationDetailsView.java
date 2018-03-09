package com.feedhenry.securenativeandroidtemplate.features.authentication.views;

import com.feedhenry.securenativeandroidtemplate.mvp.views.AppView;
import org.aerogear.mobile.auth.user.UserPrincipal;

/**
 * Created by weili on 12/09/2017.
 */

public interface AuthenticationDetailsView extends AppView {

    public void logoutSuccess(UserPrincipal user);

    public void logoutFailure(Exception error);
}
