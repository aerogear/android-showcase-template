package com.feedhenry.securenativeandroidtemplate.domain.configurations;

/**
 * Created by weili on 31/10/2017.
 */

public class ApiServerConfiguration {

    private String apiServerUrl;

    ApiServerConfiguration(String apiServerUrl) {
        this.apiServerUrl = apiServerUrl;
    }

    public String getServerUrl() {
        return this.apiServerUrl;
    }

    public String getNoteAPIUrl() {
        return String.format("%s/note", getServerUrl());
    }
}

