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
import com.feedhenry.securenativeandroidtemplate.domain.models.Identity;
import com.feedhenry.securenativeandroidtemplate.features.authentication.presenters.AuthenticationDetailsPresenter;
import com.feedhenry.securenativeandroidtemplate.features.authentication.views.AuthenticationDetailsView;
import com.feedhenry.securenativeandroidtemplate.features.authentication.views.AuthenticationDetailsViewImpl;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;

import org.json.JSONException;

import java.util.ArrayList;

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

        void onLogoutSuccess(Identity authState);

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
    ArrayAdapter<String> realmRolesAdapter;

    private AuthenticationDetailsListener authenticationDetailsListener;

    public AuthenticationDetailsFragment() {
        // Required empty public constructor
    }

    public static AuthenticationDetailsFragment forIdentityData(Identity identityData) {
        AuthenticationDetailsFragment detailsFragment = new AuthenticationDetailsFragment();
        if (identityData != null) {
            Bundle args = new Bundle();
            args.putSerializable(Constants.TOKEN_FIELDS.IDENTITY_DATA, identityData);
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
            public void logoutSuccess(Identity identity) {
                showMessage(R.string.logout_success);
                if (authenticationDetailsListener != null) {
                    authenticationDetailsListener.onLogoutSuccess(identity);
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
     *
     * @param args
     */
    private void renderIdentityInfo(Bundle args) {
        Identity identity = (Identity) args.get(Constants.TOKEN_FIELDS.IDENTITY_DATA);
        if (identity != null) {
            // get the users name
            user_name.setText(identity.getFullName());
            // get the users email
            user_email.setText(identity.getEmailAddress());
            // get the users realm level roles
            ArrayList realmRoles = identity.getRealmRoles();
            realmRolesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, realmRoles);
            listViewRealmRoles.setAdapter(realmRolesAdapter);
        }

    }

    @OnClick(R.id.keycloakLogout)
    public void logout() {
        if (authDetailsPresenter != null) {
            authDetailsPresenter.logout();
        }
    }


}
