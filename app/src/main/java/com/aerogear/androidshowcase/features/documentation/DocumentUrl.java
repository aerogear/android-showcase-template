package com.aerogear.androidshowcase.features.documentation;

/**
 * This class enumerates the documentation pages we have.
 */
public enum DocumentUrl {
    PUSH("https://docs.aerogear.org/external/showcase/android/push.html"),
    IDENTITY_MANAGEMENT("https://docs.aerogear.org/external/showcase/android/idm.html"),
    DEVICE_SECURITY("https://docs.aerogear.org/external/showcase/android/security.html"),
    METRICS("https://docs.aerogear.org/external/showcase/android/metrics.html"),
    RUNTIME("https://docs.aerogear.org/external/showcase/android/runtime.html"),
    IDENTITY_MANAGEMENT_SSO("https://docs.aerogear.org/external/showcase/android/idmsso.html"),
    NOTES_SERVICE("https://github.com/feedhenry/mobile-security/tree/master/projects/api-server");

    private final String url;

    DocumentUrl(String urlString) {
            this.url = urlString;

    }

    public String getUrl() {
        return url;
    }

}
