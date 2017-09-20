package com.feedhenry.securenativeandroidtemplate;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import com.feedhenry.securenativeandroidtemplate.di.SecureTestApplication;

/**
 * Define the test runner. Override the default Android one as we need use the application class defined by ourselves to setup DI.
 */

public class MockTestRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, SecureTestApplication.class.getName(), context);
    }
}
