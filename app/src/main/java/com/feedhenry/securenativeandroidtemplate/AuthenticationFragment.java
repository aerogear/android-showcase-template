package com.feedhenry.securenativeandroidtemplate;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.feedhenry.securenativeandroidtemplate.authenticate.OpenIDAuthenticationProvider;

/**
 * A login screen that offers login via Keycloak.
 */
public class AuthenticationFragment extends Fragment {

    private View view;
    private OpenIDAuthenticationProvider keycloakListener;


    public AuthenticationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            keycloakListener = (OpenIDAuthenticationProvider) activity;
        } catch (ClassCastException castException) {
            /** The activity does not implement the listener. */
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment - Set the view as the authenticate fragment
        view = inflater.inflate(R.layout.fragment_authentication, container, false);

        // Reference the keycloak login button
        Button loginButton = (Button) view.findViewById(R.id.keycloakLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                keycloakListener.performAuthRequest();
            }
        });

        // Reference the keycloak logout button
        Button logoutButton = (Button) view.findViewById(R.id.keycloakLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                keycloakListener.logout();
            }
        });

        return view;
    }
}
