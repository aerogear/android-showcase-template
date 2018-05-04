package com.aerogear.androidshowcase.features.http.views;

import android.app.Fragment;

import com.aerogear.androidshowcase.mvp.views.BaseAppView;

/**
 * Created by tjackman on 01/05/18.
 */

public abstract  class HttpViewImpl extends BaseAppView implements HttpView {
    public HttpViewImpl(Fragment fragment) {
        super(fragment);
    }
}
