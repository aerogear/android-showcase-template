package com.aerogear.androidshowcase.features.push.views;

import android.app.Fragment;

import com.aerogear.androidshowcase.mvp.views.BaseAppView;

/**
 * Created by tjackman on 02/05/18.
 */

public abstract class PushViewImpl extends BaseAppView implements PushView {
    public PushViewImpl(Fragment fragment) {
        super(fragment);
    }
}