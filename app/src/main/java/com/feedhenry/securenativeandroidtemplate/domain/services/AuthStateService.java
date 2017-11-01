package com.feedhenry.securenativeandroidtemplate.domain.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.datatheorem.android.trustkit.TrustKit;
import com.feedhenry.securenativeandroidtemplate.domain.models.Identity;
import com.feedhenry.securenativeandroidtemplate.mvp.components.HttpHelper;

import net.openid.appauth.AuthState;

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

    private static final String STORE_NAME = "AuthState";
    private static final String KEY_STATE = "state";
    private SharedPreferences mPrefs;
    private AuthState authState;

    @Inject
    public AuthStateService(Context context) {
        mPrefs = context.getSharedPreferences(STORE_NAME, MODE_PRIVATE);
        authState = readAuthState();
    }

    // tag::readAuthState[]

    /**
     * Read the auth state from shared preferences
     */
    private AuthState readAuthState() {
        String currentState = mPrefs.getString(KEY_STATE, null);
        if (currentState == null) {
            return new AuthState();
        }
        try {
            return AuthState.jsonDeserialize(currentState);
        } catch (JSONException ex) {
            Log.w("AuthState", "Failed to deserialize stored auth state: " + ex);
            return new AuthState();
        }
    }
    // end::readAuthState[]

    // tag::writeAuthState[]

    /**
     * Write the auth state to shared preferences
     *
     * @param state - The Authstate to write to shared preferences
     */
    public void writeAuthState(@Nullable AuthState state) {
        this.authState = state;
        if (this.authState == null) {
            this.authState = new AuthState();
            if (!mPrefs.edit().remove(KEY_STATE).commit()) {
                throw new IllegalStateException("Failed to write state to shared prefs");
            }
        } else {
            if (!mPrefs.edit().putString(KEY_STATE, this.authState.jsonSerializeString()).commit()) {
                throw new IllegalStateException("Failed to write state to shared prefs");
            }
        }
    }
    // end::writeAuthState[]

    /**
     * Check if the user is authenticated/authorized
     */
    public boolean isAuthorized() {
        return authState.isAuthorized();
    }

    /**
     * Get the access token
     */
    public String getAccessToken() {
        return authState.getAccessToken();
    }

    /**
     * Get the identity token
     */
    public String getIdentityToken() {
        return authState.getIdToken();
    }

    // tag::getIdentityInformation[]

    /**
     * Get the authenticated users identity information
     */
    public JSONObject getIdentityInformation() {
        String accessToken = getAccessToken();
        JSONObject decodedIdentityData = new JSONObject();

        try {
            // Decode the Access Token to Extract the Identity Information
            String[] splitToken = accessToken.split("\\.");
            byte[] decodedBytes = Base64.decode(splitToken[1], Base64.URL_SAFE);
            String decoded = new String(decodedBytes, "UTF-8");
            try {
                decodedIdentityData = new JSONObject(decoded);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            Log.e("", "Error Decoding Access Token", e);
        }
        return decodedIdentityData;
    }
    // end::getIdentityInformation[]

    // tag::hasRole[]

    /**
     * Check if the user has the specified role
     *
     * @param role - the role to check
     */
    public boolean hasRole(String role) {
        boolean hasRole = false;
        try {
            Identity identity = Identity.fromJson(getIdentityInformation());
            ArrayList userRoles = identity.getRealmRoles();
            if (userRoles.contains(role)) {
                hasRole = true;
            }
        } catch (Exception e) {
            Log.e("", "Error - Exception", e);
        }
        return hasRole;
    }
    // end::hasRole[]


    /**
     * Check if a new access token needs to be required
     */
    public boolean getNeedsTokenRefresh() {
        return authState.getNeedsTokenRefresh();
    }

    /**
     * Request a new access token
     */
    public void setNeedsTokenRefresh() {
        authState.setNeedsTokenRefresh(true);
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
            // Ensure that a non-expired access token is being used for the request
            if (getNeedsTokenRefresh()) {
                setNeedsTokenRefresh();
            }
            
            String accessToken = getAccessToken();
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
