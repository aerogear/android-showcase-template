package com.feedhenry.securenativeandroidtemplate.features.authentication.views;

import com.feedhenry.securenativeandroidtemplate.domain.models.Identity;
import com.feedhenry.securenativeandroidtemplate.mvp.views.AppView;

/**
 * Created by weili on 12/09/2017.
 */

public interface AuthenticationView extends AppView {

    public void renderIdentityInfo(Identity identity);

    public void showAuthError(Exception error);
}
