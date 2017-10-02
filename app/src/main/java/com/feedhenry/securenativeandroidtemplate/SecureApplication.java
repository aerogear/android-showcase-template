package com.feedhenry.securenativeandroidtemplate;

import android.app.Activity;
import android.app.Application;


import com.feedhenry.securenativeandroidtemplate.di.DaggerSecureApplicationComponent;

import net.sqlcipher.database.SQLiteDatabase;

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
