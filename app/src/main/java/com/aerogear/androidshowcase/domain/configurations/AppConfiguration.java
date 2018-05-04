package com.aerogear.androidshowcase.domain.configurations;

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

    @Inject
    public AppConfiguration() {
        readConfigurations();
    }

    private void readConfigurations() {
        final ServiceConfiguration serviceConfiguration = MobileCore.getInstance().getServiceConfiguration(NOTES_SERVER_KEY);
        final String serviceUrl = serviceConfiguration.getUrl();
        this.apiServerConfiguration = new ApiServerConfiguration(serviceUrl);
    }

    public ApiServerConfiguration getAPIServerConfiguration() {
        return this.apiServerConfiguration;
    }
}
