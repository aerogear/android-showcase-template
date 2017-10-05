package com.feedhenry.securenativeandroidtemplate.features.accesscontrol.views;

import android.app.Fragment;

import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseAppView;

/**
 * Created by tjackman on 04/10/17.
 */

public abstract class AccessControlViewImpl extends BaseAppView implements AccessControlView {
    public AccessControlViewImpl(Fragment fragment) {
        super(fragment);
    }
}