package com.aerogear.androidshowcase.mvp.components;

import com.datatheorem.android.trustkit.TrustKit;
import org.aerogear.mobile.auth.AuthService;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by tjackman on 9/16/17.
 */

public class CertPinningHelper {

    @Inject
    AuthService authService;

    public CertPinningHelper() {

    }

    // tag::createRequest[]
    /**
     * Make a request to a resource that requires the access token to be sent with the request
     *
     * @param requestUrl the request URL
     * @param sendAccessToken boolean on whether to send the access token as part of the request
     * @param callback the OkHTTP callback for the request
     */
    public Call createRequest(final String requestUrl, final boolean sendAccessToken, final okhttp3.Callback callback) {

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
     *
     * @param error the error exception from a failed request
     *
     * @return boolean based on whether or not certificate pinning has failed
     */
    public boolean checkCertificateVerificationError(final Exception error) {
        boolean certificateVerificationError = false;
        if (error.getCause() != null &&
                (error.getCause().toString().contains("Certificate validation failed") ||
                        error.getCause().toString().contains("Pin verification failed"))) {
            certificateVerificationError = true;
        }
        return certificateVerificationError;
    }
    // end::checkCertificateVerificationError[]

}
