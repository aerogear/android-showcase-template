package com.feedhenry.securenativeandroidtemplate.mvp.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.feedhenry.securenativeandroidtemplate.domain.models.Identity;

import net.openid.appauth.AuthState;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by tjackman on 9/16/17.
 */

public class AuthHelper {

    private static final String STORE_NAME = "AuthState";
    private static final String KEY_STATE = "state";
    private static SharedPreferences mPrefs;

    public AuthHelper() {
    }

    public static void init(Context context) {
        mPrefs = context.getSharedPreferences(STORE_NAME, MODE_PRIVATE);
    }

    // tag::readAuthState[]
    /**
     * Read the auth state from shared preferences
     */
    public static AuthState readAuthState() {
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
     * @param state - The Authstate to write to shared preferences
     */
    public static void writeAuthState(@Nullable AuthState state) {
        if (state == null) {
            if (!mPrefs.edit().remove(KEY_STATE).commit()) {
                throw new IllegalStateException("Failed to write state to shared prefs");
            }
        } else {
            if (!mPrefs.edit().putString(KEY_STATE, state.jsonSerializeString()).commit()) {
                throw new IllegalStateException("Failed to write state to shared prefs");
            }
        }
    }
    // end::writeAuthState[]

    /**
     * Check if the user is authenticated/authorized
     */
    public static boolean isAuthorized() {
        return readAuthState().isAuthorized();
    }

    /**
     * Get the access token
     */
    public static String getAccessToken() {
        return readAuthState().getAccessToken();
    }

    /**
     * Get the identity token
     */
    public static String getIdentityToken() {
        return readAuthState().getIdToken();
    }

    // tag::getIdentityInformation[]
    /**
     * Get the authenticated users identity information
     */
    public static JSONObject getIdentityInformation() {
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

    /**
     * Check if the user has the specified role
     * @param role - the role to check
     */
    public static boolean hasRole(String role) {
        boolean hasRole = false;
        try {
            Identity identity = Identity.fromJson(AuthHelper.getIdentityInformation());
            ArrayList userRoles = identity.getRealmRoles();
            if(userRoles.contains(role)) {
                hasRole = true;
            }
        } catch (JSONException e) {
            Log.e("", "Error - JSON Exception", e);
        }
        return hasRole;
    }


    /**
     * Check if a new access token needs to be required
     */
    public static boolean getNeedsTokenRefresh() {
        return readAuthState().getNeedsTokenRefresh();
    }

    /**
     * Request a new access token
     */
    public static void setNeedsTokenRefresh() {
        readAuthState().setNeedsTokenRefresh(true);
    }

    // tag::makeBearerRequest[]
    /**
     * Make a request to a resource that requires the access token to be sent with the request
     */
    public static Call makeBearerRequest(String url, okhttp3.Callback callback) {

        // Ensure that a non-expired access token is being used for the request
        if (getNeedsTokenRefresh()) {
            setNeedsTokenRefresh();
        }

        String accessToken = getAccessToken();

        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", String.format("Bearer %s", accessToken))
                .build();

        Call call = httpClient.newCall(request);
        call.enqueue(callback);
        return call;
    }
    // end::makeBearerRequest[]
}