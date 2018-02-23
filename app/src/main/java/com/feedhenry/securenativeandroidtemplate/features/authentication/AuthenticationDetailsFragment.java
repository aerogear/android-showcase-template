package com.feedhenry.securenativeandroidtemplate.features.authentication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.auth.user.UserRole;

import java.util.ArrayList;
import java.util.Set;

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

        void onLogoutSuccess(UserPrincipal user);

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

    @BindView(R.id.roles)
    ListView listViewRoles;

    View view;
    ArrayAdapter<UserRole> roles;

    private AuthenticationDetailsListener authenticationDetailsListener;

    public AuthenticationDetailsFragment() {
        // Required empty public constructor
    }

    public static AuthenticationDetailsFragment forIdentityData(UserPrincipal user) {
        AuthenticationDetailsFragment detailsFragment = new AuthenticationDetailsFragment();
        if (user != null) {
            Bundle args = new Bundle();
            args.putSerializable(Constants.TOKEN_FIELDS.IDENTITY_DATA, user);
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
            public void logoutSuccess(UserPrincipal user) {
                showMessage(R.string.logout_success);
                if (authenticationDetailsListener != null) {
                    authenticationDetailsListener.onLogoutSuccess(user);
                }
            }

            @Override
            public void logoutFailure(Exception error) {
                showMessage(R.string.logout_failed + ": " + error.getCause());
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
        UserPrincipal user = (UserPrincipal) args.get(Constants.TOKEN_FIELDS.IDENTITY_DATA);
        if (user != null) {
            // get the users name
            user_name.setText(user.getName());
            // get the users email
            user_email.setText(user.getEmail());
            // get the users roles
            UserRole[] rolesArray = new UserRole[user.getRoles().size()];
            rolesArray = user.getRoles().toArray(rolesArray);
            roles = new ArrayAdapter<UserRole>(context, android.R.layout.simple_list_item_1, rolesArray);
            listViewRoles.setAdapter(roles);
        }
    }

    @OnClick(R.id.keycloakLogout)
    public void logout() {
        if (authDetailsPresenter != null) {
            authDetailsPresenter.logout();
        }
    }


}
