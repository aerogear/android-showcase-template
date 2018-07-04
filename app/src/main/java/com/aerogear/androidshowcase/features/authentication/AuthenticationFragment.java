package com.aerogear.androidshowcase.features.authentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aerogear.androidshowcase.R;
import com.aerogear.androidshowcase.features.authentication.presenters.AuthenticationViewPresenter;
import com.aerogear.androidshowcase.features.authentication.views.AuthenticationView;
import com.aerogear.androidshowcase.features.authentication.views.AuthenticationViewImpl;
import com.aerogear.androidshowcase.mvp.components.CertPinningHelper;
import com.aerogear.androidshowcase.mvp.views.BaseFragment;
import com.aerogear.androidshowcase.navigation.Navigator;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.MobileCoreJsonParser;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.executor.AppExecutors;

import java.io.IOException;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import okhttp3.Call;
import okhttp3.Response;

/**
 * A login screen that offers login via Keycloak.
 */
public class AuthenticationFragment extends BaseFragment<AuthenticationViewPresenter, AuthenticationView> {

    public static final String TAG = "auth";

    public interface AuthenticationListener {

        void onAuthSuccess(UserPrincipal identityData);

        void onAuthError(Exception error);
    }

    @Inject
    Navigator navigator;

    @Inject
    AuthenticationViewPresenter authenticationViewPresenter;

    @BindView(R.id.keycloakLogin)
    TextView keycloakLogin;

    @BindView(R.id.auth_message)
    TextView authMessage;

    @BindView(R.id.background)
    ImageView background;

    @BindView(R.id.logo)
    ImageView logo;

    @Inject
    Context context;

    private View view;
    private AuthenticationListener authenticationListener;

    @Override
    public void onAttach(Activity activity) {

        AndroidInjection.inject(this);
        super.onAttach(activity);
        if (activity instanceof AuthenticationListener) {
            authenticationListener = (AuthenticationListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.authenticationViewPresenter = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment - Set the view as the authenticate fragment
        view = inflater.inflate(R.layout.fragment_authentication, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    protected AuthenticationViewPresenter initPresenter() {
        return authenticationViewPresenter;
    }

    @Override
    protected AuthenticationView initView() {
        return new AuthenticationViewImpl(this) {
            /**
             * Render the Users Identity Information upon auth success
             *
             * @param user the current user
             */
            @Override
            public void renderIdentityInfo(final UserPrincipal user) {
                showMessage(R.string.authentication_success);
                if (authenticationListener != null) {
                    authenticationListener.onAuthSuccess(user);
                }
            }

            /**
             * Render the Users Identity Information upon auth success
             *
             * @param error the error exception from the failed auth
             */
            @Override
            public void showAuthError(final Exception error) {
                    showMessage(R.string.authentication_failed);

                if (authenticationListener != null) {
                    authenticationListener.onAuthError(error);
                }
            }
        };
    }

    /**
     * Handler for the login button click
     */
    @OnClick(R.id.keycloakLogin)
    public void doLogin() {
        if (authenticationViewPresenter != null) {
            authenticationViewPresenter.doLogin();
        }
    }

}
