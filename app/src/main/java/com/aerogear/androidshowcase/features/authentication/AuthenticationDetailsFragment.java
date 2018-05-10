package com.aerogear.androidshowcase.features.authentication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.aerogear.androidshowcase.R;
import com.aerogear.androidshowcase.domain.Constants;
import com.aerogear.androidshowcase.features.authentication.presenters.AuthenticationDetailsPresenter;
import com.aerogear.androidshowcase.features.authentication.views.AuthenticationDetailsView;
import com.aerogear.androidshowcase.features.authentication.views.AuthenticationDetailsViewImpl;
import com.aerogear.androidshowcase.mvp.views.BaseFragment;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.auth.user.UserRole;
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

        void onLogoutSuccess(final UserPrincipal user);

        void onLogoutError(final Exception error);
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

    /**
     * Passing of the user identity data into the fragment
     *
     * @param user the current user
     *
     * @return the authentication details fragment
     */
    public static AuthenticationDetailsFragment forIdentityData(final UserPrincipal user) {
        AuthenticationDetailsFragment detailsFragment = new AuthenticationDetailsFragment();
        if (user != null) {
            Bundle args = new Bundle();
            args.putSerializable(Constants.TOKEN_FIELDS.IDENTITY_DATA, user);
            detailsFragment.setArguments(args);
        }
        return detailsFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
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
    public void onAttach(final Activity activity) {
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
            /**
             * The handler for the logout failure
             *
             * @param user the user that was logged out
             */
            @Override
            public void logoutSuccess(final UserPrincipal user) {
                showMessage(R.string.logout_success);
                if (authenticationDetailsListener != null) {
                    authenticationDetailsListener.onLogoutSuccess(user);
                }
            }

            /**
             * The handler for the logout failure
             *
             * @param error the error exception from the failed logout
             */
            @Override
            public void logoutFailure(final Exception error) {
                showMessage(R.string.logout_failed + ": " + error.getCause());
                if (authenticationDetailsListener != null) {
                    authenticationDetailsListener.onLogoutError(error);
                }
            }
        };
    }

    /**
     * Render the Users Identity Information in the View
     *
     * @return the help message for the current view
     */
    @Override
    public int getHelpMessageResourceId() {
        return R.string.popup_authentication_details_fragment;
    }

    /**
     * Render the Users Identity Information in the View
     *
     * @param args the args containing the identity information
     */
    private void renderIdentityInfo(final Bundle args) {
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
