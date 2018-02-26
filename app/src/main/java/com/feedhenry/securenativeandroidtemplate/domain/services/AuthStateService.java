package com.feedhenry.securenativeandroidtemplate.domain.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.datatheorem.android.trustkit.TrustKit;
import com.feedhenry.securenativeandroidtemplate.domain.models.Identity;
import com.feedhenry.securenativeandroidtemplate.mvp.components.HttpHelper;
import org.aerogear.mobile.auth.AuthService;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by tjackman on 9/16/17.
 */

@Singleton
public class AuthStateService {

    @Inject
    AuthService authService;


    @Inject
    public AuthStateService() {

    }

    // tag::createRequest[]

    /**
     * Make a request to a resource that requires the access token to be sent with the request
     */
    public Call createRequest(String requestUrl, boolean sendAccessToken, okhttp3.Callback callback) {

        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String serverHostname = url.getHost();

        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SSLSocketFactory sslSocketFactory = TrustKit.getInstance().getSSLSocketFactory(serverHostname);
        X509TrustManager trustManager = TrustKit.getInstance().getTrustManager(serverHostname);
        connection.setSSLSocketFactory(sslSocketFactory);

        OkHttpClient httpClient = HttpHelper.getHttpClient()
                .sslSocketFactory(sslSocketFactory, trustManager)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request;

        if (sendAccessToken) {
            
            String accessToken = authService.currentUser().getAccessToken();

            request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", String.format("Bearer %s", accessToken))
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)
                    .build();
        }


        Call call = httpClient.newCall(request);
        call.enqueue(callback);
        return call;
    }
    // end::createRequest[]

    // tag::checkCertificateVerificationError[]
    /**
     * Check if an exception is caused by a certificate verification error
     * @param e
     * @return
     */
    public boolean checkCertificateVerificationError(Exception e) {
        boolean certificateVerificationError = false;
        if (e.getCause() != null &&
                (e.getCause().toString().contains("Certificate validation failed") ||
                e.getCause().toString().contains("Pin verification failed"))) {
            certificateVerificationError = true;
        }
        return certificateVerificationError;
    }
    // end::checkCertificateVerificationError[]

}
