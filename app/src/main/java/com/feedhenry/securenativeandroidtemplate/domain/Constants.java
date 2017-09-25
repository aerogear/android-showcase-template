package com.feedhenry.securenativeandroidtemplate.domain;

import android.net.Uri;

/**
 * Created by weili on 13/09/2017.
 */

public class Constants {

    public static final class REQUEST_CODES {
        public static final int AUTH_CODE = 1;
    }

    public static final class TOKEN_FIELDS {
        public static final String AUTH_STATE = "authState";
        public static final String IDENTITY_DATA = "identityData";
    }

    public static final class KEYCLOAK_CONFIG {
        public static final String BASE_SERVER_URI = "https://keycloak-openshift-mobile-security.osm1.skunkhenry.com/auth/realms/secure-app/protocol/openid-connect";
        public static final Uri AUTHORIZATION_ENDPOINT = Uri.parse(BASE_SERVER_URI + "/auth");
        public static final Uri TOKEN_ENDPOINT = Uri.parse(BASE_SERVER_URI + "/token");
        public static final String CLIENT_ID = "client-app";
        public static final Uri REDIRECT_URI = Uri.parse("com.feedhenry.securenativeandroidtemplate:/callback");
        public static final String OPEN_ID_SCOPE = "openid";
        public static final String LOGOUT_ENDPOINT = BASE_SERVER_URI + "/logout";
        public static final String TOKEN_HINT_FRAGMENT = "?id_token_hint=";
        public static final String REDIRECT_FRAGMENT = "&redirect_uri=";
    }

    public static final class NOTE_FIELDS {
        public static final String ID_FIELD = "id";
        public static final String TITLE_FIELD = "title";
        public static final String CONTENT_FIELD = "content";
        public static final String STORE_TYPE_FIELD = "storeType";
    }
}
