package com.feedhenry.securenativeandroidtemplate.features.authentication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.Constants;
import com.feedhenry.securenativeandroidtemplate.features.authentication.presenters.AuthenticationDetailsPresenter;
import com.feedhenry.securenativeandroidtemplate.features.authentication.views.AuthenticationDetailsView;
import com.feedhenry.securenativeandroidtemplate.features.authentication.views.AuthenticationDetailsViewImpl;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;


import net.openid.appauth.AuthState;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

/**
 * A fragment for showing the user details view after they have logged in.
 */
public class AuthenticationDetailsFragment extends BaseFragment<AuthenticationDetailsPresenter, AuthenticationDetailsView> {

    public static final String TAG = "authDetails";

    public interface AuthenticationDetailsListener {

        void onLogoutSuccess(AuthState authState);

        void onLogoutError(Exception error);
    }

    @Inject
    AuthenticationDetailsPresenter authDetailsPresenter;

    @Inject
    Context context;

    @BindView(R.id.divider_realm)
    TextView divider_realm;

    @BindView(R.id.user_name)
    TextView user_name;

    @BindView(R.id.user_email)
    TextView user_email;

    @BindView(R.id.realm_roles)
    ListView listViewRealmRoles;

    View view;
    ArrayList<String> realmRoles;
    ArrayAdapter<String> realmRolesAdapter;

    private AuthenticationDetailsListener authenticationDetailsListener;

    public AuthenticationDetailsFragment() {
        // Required empty public constructor
    }

    public static AuthenticationDetailsFragment forToken(TokenResponse token) {
        AuthenticationDetailsFragment detailsFragment = new AuthenticationDetailsFragment();
        if (token != null) {
            Bundle args = new Bundle();
            args.putString(Constants.TOKEN_FIELDS.AUTH_TOKEN, token.jsonSerializeString());
            detailsFragment.setArguments(args);
        }
        return detailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_authentication_details, container, false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();
        if (args != null) {
            renderIdentityInfo(args);
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(this);
        super.onAttach(activity);
        if (activity instanceof AuthenticationDetailsListener) {
            authenticationDetailsListener = (AuthenticationDetailsListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.authDetailsPresenter = null;
    }

    @Override
    protected AuthenticationDetailsPresenter initPresenter() {
        return authDetailsPresenter;
    }

    @Override
    protected AuthenticationDetailsView initView() {
        return new AuthenticationDetailsViewImpl(this) {
            @Override
            public void logoutSuccess(AuthState authState) {
                showMessage(R.string.logout_success);
                if (authenticationDetailsListener != null) {
                    authenticationDetailsListener.onLogoutSuccess(authState);
                }
            }

            @Override
            public void logoutFailure(Exception error) {
                if (authenticationDetailsListener != null) {
                    authenticationDetailsListener.onLogoutError(error);
                }
            }
        };
    }

    @Override
    public int getHelpMessageResourceId() {
        return R.string.popup_authentication_details_fragment;
    }

    /**
     * Render the Users Identity Information in the View
     * @param args
     */
    private void renderIdentityInfo(Bundle args) {
        String identityData = args.getString(Constants.TOKEN_FIELDS.IDENTITY_DATA);
        if (identityData != null) {
            try {
                JSONObject identityDataJSON = new JSONObject(identityData);
                // get the users name
                if (identityDataJSON.has("name") && identityDataJSON.getString("name").length() > 0) {
                    user_name.setText(identityDataJSON.getString("name"));
                }
                else {
                    user_name.setText(R.string.unknown_name);
                }
                // get the users email
                if (identityDataJSON.has("email") && identityDataJSON.getString("email").length() > 0) {
                    user_email.setText(identityDataJSON.getString("email"));
                } else {
                    user_email.setText(R.string.unknown_email);
                }
                // get the users realm level roles
                if (identityDataJSON.has("realm_access") && identityDataJSON.getJSONObject("realm_access").has("roles")) {
                    String tokenRealmRolesJSON = identityDataJSON.getJSONObject("realm_access").getString("roles");

                    Type listType = new TypeToken<List<String>>() {}.getType();
                    realmRoles = new Gson().fromJson(tokenRealmRolesJSON, listType);

                    if (realmRoles.size() > 0) {
                        realmRolesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, realmRoles);
                        listViewRealmRoles.setAdapter(realmRolesAdapter);

                    } else {
                        listViewRealmRoles.setVisibility(View.GONE);
                        divider_realm.setVisibility(View.GONE);
                    }
                }
            } catch (JSONException e) {
                Log.i("", "Error Parsing Access Token", e);
            }
        }
    }

    @OnClick(R.id.keycloakLogout)
    public void logout() {
        if (authDetailsPresenter != null) {
            authDetailsPresenter.logout();
        }
    }


}
