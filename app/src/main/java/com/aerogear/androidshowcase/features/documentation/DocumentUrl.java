package com.aerogear.androidshowcase.features.documentation;

/**
 * This class enumerates the documentation pages we have.
 */
public enum DocumentUrl {
    PUSH("https://docs.aerogear.org/aerogear/latest/showcase/push.html"),
    IDENTITY_MANAGEMENT("https://docs.aerogear.org/aerogear/latest/showcase/idm.html"),
    DEVICE_SECURITY("https://docs.aerogear.org/aerogear/latest/showcase/device-security.html"),
    METRICS("https://docs.aerogear.org/aerogear/latest/showcase/metrics.html"),
    RUNTIME("https://docs.aerogear.org/aerogear/latest/showcase/runtime.html"),
    NOTES_SERVICE("https://github.com/feedhenry/mobile-security/tree/master/projects/api-server");

    private final String url;

    DocumentUrl(String urlString) {
            this.url = urlString;

    }

    public String getUrl() {
        return url;
    }

}
