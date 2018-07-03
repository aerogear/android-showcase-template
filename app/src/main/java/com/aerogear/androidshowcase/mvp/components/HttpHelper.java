package com.aerogear.androidshowcase.mvp.components;

import android.content.Context;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.MobileCoreConfiguration;
import org.aerogear.mobile.core.configuration.MobileCoreJsonParser;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.reactive.Request;
import org.aerogear.mobile.core.reactive.Requester;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by tjackman on 11/10/17.
 */

public class HttpHelper {

    private static OkHttpClient.Builder httpClient;
    private static Boolean CERTIFICATES_CONFIGURED = null;//Start unset
    public HttpHelper() {

    }

    public static void init() {
        httpClient = new OkHttpClient.Builder();
    }

    public static Request<Boolean> checkCertificates(Context context) {
        return Requester.call(() -> {
            if (CERTIFICATES_CONFIGURED == null) {
                InputStream jsonStream = context.getAssets().open("mobile-services.json");

                if (jsonStream != null) {
                    MobileCoreConfiguration configuration = new MobileCoreJsonParser(jsonStream).parse();
                    Map<String, ServiceConfiguration> configurations = configuration.getServicesConfigPerId();

                    if (configurations.size() != 0) {
                        HttpServiceModule httpService = MobileCore.getInstance().getHttpLayer();
                        ServiceConfiguration serviceConfiguration = configurations.values().iterator().next();
                        String serviceUrl = serviceConfiguration.getUrl();
                        OkHttpClient client = new OkHttpClient();
                        okhttp3.Request okHttpRequest = new okhttp3.Request.Builder().url(serviceUrl).get().build();
                        try {
                            Response response = client.newCall(okHttpRequest).execute();
                            response.body().close();
                            CERTIFICATES_CONFIGURED = true; //Call didn't blow up
                        } catch (Exception ex) {
                            CERTIFICATES_CONFIGURED = false;
                        }

                    } else {
                        CERTIFICATES_CONFIGURED = true; //No Services to call, so all is OK,
                    }

                } else {
                    CERTIFICATES_CONFIGURED = false;
                }



            }
            return CERTIFICATES_CONFIGURED;
        }).requestOn(new AppExecutors().networkThread());

    }

    public static OkHttpClient.Builder getHttpClient() {
        return httpClient;
    }
}
