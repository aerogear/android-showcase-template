package com.feedhenry.securenativeandroidtemplate.features.http.views;

import android.app.Fragment;

import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseAppView;

/**
 * Created by tjackman on 01/05/18.
 */

public abstract  class HttpViewImpl extends BaseAppView implements HttpView {
    public HttpViewImpl(Fragment fragment) {
        super(fragment);
    }
}
