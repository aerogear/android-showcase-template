package com.feedhenry.securenativeandroidtemplate.domain.configurations;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by weili on 31/10/2017.
 */
@Singleton
public class AppConfiguration {

    private static final String NOTES_SERVER_KEY = "notes-service";

    private ApiServerConfiguration apiServerConfiguration;

    private final MobileCore mobileCore;

    @Inject
    public AppConfiguration(MobileCore mobileCore) {
        this.mobileCore = mobileCore;

        readConfigurations();
    }

    private void readConfigurations() {
        final ServiceConfiguration serviceConfiguration = mobileCore.getServiceConfiguration(NOTES_SERVER_KEY);
        final String serviceUrl = serviceConfiguration.getUrl();
        this.apiServerConfiguration = new ApiServerConfiguration(serviceUrl);
    }

    public ApiServerConfiguration getAPIServerConfiguration() {
        return this.apiServerConfiguration;
    }
}
