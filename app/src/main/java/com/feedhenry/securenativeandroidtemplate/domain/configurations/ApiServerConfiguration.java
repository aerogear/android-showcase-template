package com.feedhenry.securenativeandroidtemplate.domain.configurations;

import org.json.JSONObject;

/**
 * Created by weili on 31/10/2017.
 */

public class ApiServerConfiguration {

    private static final String API_SERVER_URL = "server-url";
    private JSONObject apiServerConfig;
    private Exception configurationError;

    private String apiServerUrl;

    ApiServerConfiguration(JSONObject apiServerConfigJson) throws Exception {
        this.apiServerConfig = apiServerConfigJson;
        try {
            this.apiServerUrl = this.apiServerConfig.getString(API_SERVER_URL);
        } catch (Exception e) {
            this.configurationError = e;
        }
    }

    public boolean isValid() {
        return this.configurationError == null;
    }

    public Exception getConfigurationError() {
        return this.configurationError;
    }

    public String getServerUrl() {
        return this.apiServerUrl;
    }

    public String getNoteAPIUrl() {
        return String.format("%s/note", getServerUrl());
    }
}

