package com.aerogear.androidshowcase.features.home.views;

import android.app.Fragment;

import com.aerogear.androidshowcase.mvp.views.BaseAppView;

/**
 * Created by weili on 12/09/2017.
 */

public abstract  class HomeViewImpl extends BaseAppView implements HomeView {
    public HomeViewImpl(Fragment fragment) {
        super(fragment);
    }
}
