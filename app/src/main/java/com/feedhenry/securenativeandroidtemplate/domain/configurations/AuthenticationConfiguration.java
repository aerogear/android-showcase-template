package com.feedhenry.securenativeandroidtemplate.domain.configurations;

import android.net.Uri;

import org.json.JSONObject;

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

    private JSONObject authConfig;
    private Exception configurationError;

    private String serverUrl;
    private String realmId;
    private String clientId;

    private String baseUrl;

    AuthenticationConfiguration(JSONObject authConfigJson) {
        authConfig = authConfigJson;

        try {
            serverUrl = authConfig.getString(SERVER_URL_NAME);
            realmId = authConfig.getString(REALM_ID_NAME);
            clientId = authConfig.getString(CLIENT_ID_NAME);
        } catch (Exception e) {
            this.configurationError = e;
        }

        baseUrl = String.format("%s/auth/realms/%s/protocol/openid-connect", serverUrl, realmId);
    }

    public boolean isValid() {
        return this.configurationError == null;
    }

    public Exception getConfigurationError() {
        return this.configurationError;
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
