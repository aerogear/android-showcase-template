package com.feedhenry.securenativeandroidtemplate.mvp.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import net.openid.appauth.AuthState;
import org.json.JSONException;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by tjackman on 9/16/17.
 */

public class AuthHelper {

    private final SharedPreferences mPrefs;
    private final ReentrantLock mPrefsLock;

    private static final String STORE_NAME = "AuthState";
    private static final String KEY_STATE = "state";

    @Inject
    Context context;

    public AuthHelper(@NonNull Context context) {
        System.out.println(">>>> " + context);
        mPrefs = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
        mPrefsLock = new ReentrantLock();
    }

    /**
     * Read the auth state from shared preferences
     */
    public AuthState readAuthState() {
        mPrefsLock.lock();
        try {
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
        } finally {
            mPrefsLock.unlock();
        }
    }

    /**
     * Write the auth state to shared preferences
     */
    public void writeAuthState(@Nullable AuthState state) {
        mPrefsLock.lock();
        try {
            if (state == null) {
                if (!mPrefs.edit().remove(KEY_STATE).commit()) {
                    throw new IllegalStateException("Failed to write state to shared prefs");
                }
            } else {
                if(!mPrefs.edit().putString(KEY_STATE, state.jsonSerializeString()).commit()) {
                    throw new IllegalStateException("Failed to write state to shared prefs");
                }
            }
        } finally {
            mPrefsLock.unlock();
        }
    }

    /**
     * Check if the user is authenticated/authorized
     */
    public boolean isAuthorized() {
        AuthState state = readAuthState();
        return state.isAuthorized();
    }

    /**
     * Get the access token
     */
    public String getAccessToken() {
        AuthState state = readAuthState();
        return state.getAccessToken();
    }

    /**
     * Get the identity token
     */
    public String getIdentityToken() {
        AuthState state = readAuthState();
        return state.getIdToken();
    }

    /**
     * Get the expiration time of the access token
     */
    public Long getAccessTokenExpirationTime() {
        AuthState state = readAuthState();
        return state.getAccessTokenExpirationTime();
    }

    /**
     * Check if a new access token needs to be required
     */
    public boolean getNeedsTokenRefresh() {
        AuthState state = readAuthState();
        return state.getNeedsTokenRefresh();
    }

    /**
     * Request a new access token
     */
    public void setNeedsTokenRefresh() {
        AuthState state = readAuthState();
        state.setNeedsTokenRefresh(true);
    }

    /**
     * Make a request to a resource that requires the access token to be sent with hte request
     */
    public Call makeBearerRequest(String url, okhttp3.Callback callback) {

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
}