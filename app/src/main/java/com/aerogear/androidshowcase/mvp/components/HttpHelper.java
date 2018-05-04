package com.aerogear.androidshowcase.mvp.components;

import okhttp3.OkHttpClient;

/**
 * Created by tjackman on 11/10/17.
 */

public class HttpHelper {

    private static OkHttpClient.Builder httpClient;

    public HttpHelper() {

    }

    public static void init() {
        httpClient = new OkHttpClient.Builder();
    }

    public static OkHttpClient.Builder getHttpClient() {
        return httpClient;
    }
}
