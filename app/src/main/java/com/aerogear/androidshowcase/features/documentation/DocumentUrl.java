package com.aerogear.androidshowcase.features.documentation;

/**
 * This class enumerates the documentation pages we have.
 */
public enum DocumentUrl {
    PUSH("https://docs.aerogear.org/aerogear/latest/showcase/push.html#nochrome"),
    IDENTITY_MANAGEMENT("https://docs.aerogear.org/aerogear/latest/showcase/idm.html#nochrome"),
    DEVICE_SECURITY("https://docs.aerogear.org/aerogear/latest/showcase/device-security.html#nochrome"),
    METRICS("https://docs.aerogear.org/aerogear/latest/showcase/metrics.html#nochrome"),
    RUNTIME("https://docs.aerogear.org/aerogear/latest/showcase/runtime.html#nochrome"),
    IDENTITY_MANAGEMENT_SSO("https://docs.aerogear.org/aerogear/latest/keycloak/index.html?sso=1#nochrome"),
    NOTES_SERVICE("https://github.com/feedhenry/mobile-security/tree/master/projects/api-server");

    private final String url;

    DocumentUrl(String urlString) {
            this.url = urlString;

    }

    public String getUrl() {
        return url;
    }

}
