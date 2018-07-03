package com.aerogear.androidshowcase.providers;

import com.aerogear.androidshowcase.R;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.reactive.Responder;
import org.aerogear.mobile.push.PushService;
import org.aerogear.mobile.push.UnifiedPushConfig;

import java.util.Arrays;

public class PushServiceProvider {

    private static PushServiceProvider instance;

    private PushService pushService;
    private Exception registrationException;

    protected PushServiceProvider() {
        pushService = new PushService.Builder().openshift().build();
    }

    /**
     * Register the push service.
     */
    public void registerDevice() {
        pushService.registerDevice().respondOn(new AppExecutors().singleThreadService())
            .respondWith(new Responder<Boolean>() {
                @Override
                public void onResult(Boolean value) {
                    MobileCore.getLogger().info(String.valueOf(R.string.push_device_register_success));
                }

                @Override
                public void onException(Exception exception) {
                    MobileCore.getLogger().error(exception.getMessage(), exception);
                    registrationException = exception;
                }
            });
    }

    /**
     * Retrieve the exception that occurred during registration, if any.
     * @return The exception that occurred during registration, or null.
     */
    public Exception getRegistrationException() {
        return this.registrationException;
    }

    public static PushServiceProvider getInstance() {
        if(instance == null) {
            instance = new PushServiceProvider();
        }
        return instance;
    }
}
