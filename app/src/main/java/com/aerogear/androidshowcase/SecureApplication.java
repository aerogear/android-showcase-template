package com.aerogear.androidshowcase;

import android.app.Activity;
import android.app.Application;

import com.aerogear.androidshowcase.di.DaggerSecureApplicationComponent;
import com.datatheorem.android.trustkit.TrustKit;

import net.sqlcipher.database.SQLiteDatabase;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.reactive.Responder;
import org.aerogear.mobile.push.PushService;
import org.aerogear.mobile.push.UnifiedPushConfig;

import java.util.Arrays;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/**
 * The main application class. Needs to setup dependency injection.
 */

public class SecureApplication extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        initInjector();

        try {
            SQLiteDatabase.loadLibs(this);
        } catch (UnsatisfiedLinkError e) {
            //only thrown during tests, ignore it
        }
    }

    protected void initInjector() {
        DaggerSecureApplicationComponent
                .builder()
                .application(this)
                .build()
                .inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
