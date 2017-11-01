package com.feedhenry.securenativeandroidtemplate.domain.configurations;

import android.content.Context;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.utils.StreamUtils;

import org.json.JSONObject;

import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by weili on 31/10/2017.
 */
@Singleton
public class AppConfiguration {

    private static final String AUTH_SERVER_KEY = "auth-server";
    private static final String API_SERVER_KEY = "api-server";

    private Context context;
    private Exception configurationError;
    private JSONObject appConfigJson;

    private AuthenticationConfiguration authConfiguration;
    private ApiServerConfiguration apiServerConfiguration;

    @Inject
    public AppConfiguration(Context context) {
        this.context = context;
        try {
            readConfigurations();
        } catch (Exception e) {
            configurationError = e;
        }
    }

    private void readConfigurations() throws Exception {
        InputStream in = context.getResources().openRawResource(R.raw.app_config);
        try {
            String content = StreamUtils.readStream(in);
            appConfigJson = new JSONObject(content);
            authConfiguration = new AuthenticationConfiguration(appConfigJson.getJSONObject(AUTH_SERVER_KEY));
            apiServerConfiguration = new ApiServerConfiguration(appConfigJson.getJSONObject(API_SERVER_KEY));
        } finally {
            in.close();
        }
    }

    public boolean isValid() {
        return this.configurationError == null;
    }

    public Exception getConfigurationError() {
        return this.configurationError;
    }

    public AuthenticationConfiguration getAuthConfiguration() {
        return this.authConfiguration;
    }

    public ApiServerConfiguration getAPIServerConfiguration() {
        return this.apiServerConfiguration;
    }
}
