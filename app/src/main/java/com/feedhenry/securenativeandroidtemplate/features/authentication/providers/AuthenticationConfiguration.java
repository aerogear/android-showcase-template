package com.feedhenry.securenativeandroidtemplate.features.authentication.providers;

import android.content.Context;
import android.net.Uri;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.utils.StreamUtils;

import org.json.JSONObject;

import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by weili on 26/10/2017.
 */

@Singleton
public class AuthenticationConfiguration {

    private static final String SERVER_URL_NAME = "auth-server-url";
    private static final String REALM_ID_NAME = "realm-id";
    private static final String CLIENT_ID_NAME = "client-id";

    private static final String TOKEN_HINT_FRAGMENT = "id_token_hint";
    private static final String REDIRECT_FRAGMENT = "redirect_uri";

    private Context context;
    private JSONObject authConfig;
    private Exception configurationError;

    private String serverUrl;
    private String realmId;
    private String clientId;

    private String baseUrl;

    @Inject
    public AuthenticationConfiguration(Context context) {
        this.context = context;
        try {
            readConfigurations();
        } catch (Exception e) {
            configurationError = e;
        }
    }

    private void readConfigurations() throws Exception {
        InputStream in = context.getResources().openRawResource(R.raw.auth_config);
        try {
            String content = StreamUtils.readStream(in);
            authConfig = new JSONObject(content);

            serverUrl = authConfig.getString(SERVER_URL_NAME);
            realmId = authConfig.getString(REALM_ID_NAME);
            clientId = authConfig.getString(CLIENT_ID_NAME);

            baseUrl = String.format("%s/auth/realms/%s/protocol/openid-connect", serverUrl, realmId);

        } finally {
            in.close();
        }
    }

    public boolean isValid() {
        return this.configurationError == null;
    }

    public Exception getConfigurationError() {
        return configurationError;
    }

    public Uri getAuthenticationEndpoint() {
        return Uri.parse(this.baseUrl + "/auth");
    }

    public Uri getTokenEndpoint() {
        return Uri.parse( this.baseUrl + "/token");
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getLogoutUrl(String identityToken, String redirectUri) {
        return String.format("%s/logout?%s=%s&%s=%s", this.baseUrl, TOKEN_HINT_FRAGMENT, identityToken, REDIRECT_FRAGMENT, redirectUri);
    }

    public String getHostUrl() {
        return this.serverUrl;
    }
}
