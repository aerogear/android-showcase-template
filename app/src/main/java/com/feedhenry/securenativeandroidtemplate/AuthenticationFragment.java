package com.feedhenry.securenativeandroidtemplate;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A login screen that offers login via Keycloak.
 */
public class AuthenticationFragment extends Fragment {

    private View view;

    public AuthenticationFragment() {
        // Required empty public constructor
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
                attemptKeycloakLogin();
            }
        });

        // Reference the keycloak logout button
        Button logoutButton = (Button) view.findViewById(R.id.keycloakLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptKeycloakLogout();
            }
        });

        // set the information dialog text
        ((MainActivity)getActivity()).setInformationText(getString(R.string.popup_authentication_fragment));

        return view;
    }

    /**
     * Login via Keycloak with a redirect to the Native browser on the device.
     */
    public void attemptKeycloakLogin() {
        ((MainActivity)getActivity()).performKeycloakAuthentication();
    }

    /**
     * Login via Keycloak with a redirect to the Native browser on the device.
     */
    public void attemptKeycloakLogout() {
        ((MainActivity)getActivity()).performKeycloakLogout();
    }
}
