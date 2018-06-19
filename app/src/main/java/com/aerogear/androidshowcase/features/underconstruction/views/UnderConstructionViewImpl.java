package com.aerogear.androidshowcase.features.underconstruction.views;

import android.app.Fragment;

import com.aerogear.androidshowcase.mvp.views.BaseAppView;

public abstract class UnderConstructionViewImpl extends BaseAppView implements UnderConstructionView {
    public UnderConstructionViewImpl(Fragment fragment) {
        super(fragment);
    }
}