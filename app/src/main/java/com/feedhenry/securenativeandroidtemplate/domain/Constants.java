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


    public static final class OPEN_ID_CONNECT_CONFIG {
        public static final Uri REDIRECT_URI = Uri.parse("com.feedhenry.securenativeandroidtemplate:/callback");
        public static final String OPEN_ID_SCOPE = "openid";
    }

    public static final class ACCESS_CONTROL_ROLES {
        public static final String ROLE_MOBILE_USER = "mobile-user";
        public static final String ROLE_API_ACCESS = "api-access";
        public static final String ROLE_SUPERUSER = "superuser";
    }

    public static final class NOTE_FIELDS {
        public static final String ID_FIELD = "id";
        public static final String TITLE_FIELD = "title";
        public static final String CONTENT_FIELD = "content";
        public static final String STORE_TYPE_FIELD = "storeType";
    }
}
