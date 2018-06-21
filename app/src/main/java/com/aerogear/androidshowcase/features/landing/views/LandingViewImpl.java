package com.aerogear.androidshowcase.features.landing.views;

import android.app.Fragment;

import com.aerogear.androidshowcase.mvp.views.BaseAppView;

public abstract class LandingViewImpl extends BaseAppView implements LandingView {
    public LandingViewImpl(Fragment fragment) {
        super(fragment);
    }
}