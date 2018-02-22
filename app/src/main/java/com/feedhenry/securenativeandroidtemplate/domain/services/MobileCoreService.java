package com.feedhenry.securenativeandroidtemplate.domain.services;

import android.content.Context;

import org.aerogear.mobile.core.MobileCore;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by tjackman on 22/02/18.
 */

@Singleton
public class MobileCoreService {

    private MobileCore mobileCore;

    @Inject
    public MobileCoreService(Context context) {
        this.mobileCore = MobileCore.init(context);
    }

    public MobileCore getMobileCore() {
        return mobileCore;
    }

}
