package com.feedhenry.securenativeandroidtemplate.features.authentication.views;

import android.app.Fragment;

import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseAppView;

/**
 * Created by weili on 12/09/2017.
 */

public abstract class AuthenticationViewImpl extends BaseAppView implements AuthenticationView {
    public AuthenticationViewImpl(Fragment fragment) {
        super(fragment);
    }
}
