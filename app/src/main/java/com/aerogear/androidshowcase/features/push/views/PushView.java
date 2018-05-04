package com.aerogear.androidshowcase.features.push.views;

import com.aerogear.androidshowcase.mvp.views.AppView;

/**
 * Created by tjackman on 02/05/18.
 */

public interface PushView extends AppView {

    void unregisterSuccess();

    void unregisterError(Throwable error);

    void registerSuccess();

    void registerError(Throwable error);

}
