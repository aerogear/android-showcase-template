package com.feedhenry.securenativeandroidtemplate.domain.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tjackman on 22/09/17.
 */

public class Identity implements java.io.Serializable {
    private String username;
    private String fullName;
    private String emailAddress;
    private ArrayList<String> realmRoles;

    public Identity(String username, String fullName, String emailAddress, ArrayList<String> realmRoles) {
        this.username = username;
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.realmRoles = realmRoles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public ArrayList<String> getRealmRoles() {
        return realmRoles;
    }

    public void setRealmRoles(ArrayList<String> realmRoles) {
        this.realmRoles = realmRoles;
    }

    /**
     * Takes in an OpenID Connect Access Token in JSON format and Creates a new Identity Object from it.
     *
     * @param identityDataJSON - the identity data to extract from JSON format
     * @return
     * @throws JSONException
     */
    public static Identity fromJson(JSONObject identityDataJSON) throws JSONException {

        // setup default values for the UI if no values are provided in the JSON
        String username = "Unknown Username";
        String fullName = "Unknown Name";
        String emailAddress = "Unknown Email Address";
        ArrayList<String> realmRoles = new ArrayList<>();

        if (identityDataJSON != null) {
            // get the users username
            if (identityDataJSON.has("username") && identityDataJSON.getString("username").length() > 0) {
                username = identityDataJSON.getString("username");
            }
            // get the users full name
            if (identityDataJSON.has("name") && identityDataJSON.getString("name").length() > 0) {
                fullName = identityDataJSON.getString("name");
            }
            // get the users email
            if (identityDataJSON.has("email") && identityDataJSON.getString("email").length() > 0) {
                emailAddress = identityDataJSON.getString("email");
            }
            // get the users realm level roles
            if (identityDataJSON.has("realm_access") && identityDataJSON.getJSONObject("realm_access").has("roles")) {
                String tokenRealmRolesJSON = identityDataJSON.getJSONObject("realm_access").getString("roles");

                Type listType = new TypeToken<List<String>>() {
                }.getType();
                realmRoles = new Gson().fromJson(tokenRealmRolesJSON, listType);
            }
        }
        // construct the new identity
        Identity identity = new Identity(username, fullName, emailAddress, realmRoles);

        return identity;
    }
}


