package com.feedhenry.securenativeandroidtemplate.di;

import com.feedhenry.securenativeandroidtemplate.SecureApplication;
import com.feedhenry.securenativeandroidtemplate.di.DaggerSecureApplicationTestComponent;
import com.feedhenry.securenativeandroidtemplate.di.SecureApplicationComponent;
import com.feedhenry.securenativeandroidtemplate.di.SecureApplicationTestComponent;

/**
 * Setup the DI for the tests. This class will initialise the dagger component for the application.
 */

public class SecureTestApplication extends SecureApplication {

    SecureApplicationTestComponent component;

    @Override
    protected void initInjector() {
        component = DaggerSecureApplicationTestComponent.builder().application(this).build();
        component.inject(this);
    }

    public SecureApplicationTestComponent getComponent() {
        return component;
    }
}
