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

        // Initialize TrustKit for Certificate Pinning
        TrustKit.initializeWithNetworkSecurityConfiguration(this, R.xml.network_security_config);

        try {
            SQLiteDatabase.loadLibs(this);
        } catch (UnsatisfiedLinkError e) {
            //only thrown during tests, ignore it
        }

        if (MobileCore.getInstance().getServiceConfigurationByType("push") != null ) {
            registerDeviceOnUnifiedPushServer();
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

    private void registerDeviceOnUnifiedPushServer() {

        UnifiedPushConfig unifiedPushConfig = new UnifiedPushConfig();
        unifiedPushConfig.setAlias("AeroGear");
        unifiedPushConfig.setCategories(Arrays.asList("Android", "Example"));

        PushService pushService = new PushService.Builder().openshift().build();
        pushService.registerDevice().respondOn(new AppExecutors().mainThread())
                .respondWith(new Responder<Boolean>() {
                    @Override
                    public void onResult(Boolean value) {
                        MobileCore.getLogger().info("Device registered on Unified Push Server");
                    }

                    @Override
                    public void onException(Exception exception) {
                        MobileCore.getLogger().error(exception.getMessage(), exception);
                    }
                });
    }

}
