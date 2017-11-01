package com.feedhenry.securenativeandroidtemplate.features.authentication;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.configurations.AppConfiguration;
import com.feedhenry.securenativeandroidtemplate.domain.models.Identity;
import com.feedhenry.securenativeandroidtemplate.features.authentication.presenters.AuthenticationViewPresenter;
import com.feedhenry.securenativeandroidtemplate.features.authentication.views.AuthenticationView;
import com.feedhenry.securenativeandroidtemplate.features.authentication.views.AuthenticationViewImpl;
import com.feedhenry.securenativeandroidtemplate.domain.services.AuthStateService;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;
import com.feedhenry.securenativeandroidtemplate.navigation.Navigator;

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

        void onAuthSuccess(Identity identityData);

        void onAuthError(Exception error);
    }

    @Inject
    Navigator navigator;

    @Inject
    AppConfiguration appConfiguration;

    @Inject
    AuthenticationViewPresenter authenticationViewPresenter;

    @Inject
    AuthStateService authStateService;

    @BindView(R.id.keycloakLogin)
    TextView keycloakLogin;

    @BindView(R.id.auth_message)
    TextView authMessage;

    @BindView(R.id.background)
    ImageView background;

    @BindView(R.id.logo)
    ImageView logo;

    private View view;
    private AuthenticationListener authenticationListener;


    public AuthenticationFragment() {
        // Required empty public constructor
    }

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

        // Check for a valid server certificate before allow a user to authenticate on the server
        performCertPinningVerification();

        return view;
    }

    @Override
    protected AuthenticationViewPresenter initPresenter() {
        return authenticationViewPresenter;
    }

    @Override
    protected AuthenticationView initView() {
        return new AuthenticationViewImpl(this) {
            @Override
            public void renderIdentityInfo(Identity identity) {
                showMessage(R.string.authentication_success);
                if (authenticationListener != null) {
                    authenticationListener.onAuthSuccess(identity);
                }
            }

            @Override
            public void showAuthError(Exception e) {
                if (authStateService.checkCertificateVerificationError(e)) {
                    showMessage(R.string.cert_pin_verification_failed);
                } else {
                    showMessage(R.string.authentication_failed);
                }

                if (authenticationListener != null) {
                    authenticationListener.onAuthError(e);
                }
            }
        };
    }

    @Override
    public int getHelpMessageResourceId() {
        return R.string.popup_authentication_fragment;
    }

    @OnClick(R.id.keycloakLogin)
    public void doLogin() {
        if (authenticationViewPresenter != null) {
            authenticationViewPresenter.doLogin();
        }
    }

    /**
     * Perform certificate pinning verification before allowing a user to login to the application using a secure channel
     */
    public void performCertPinningVerification() {

        // disable allowing a user to login until the channel is secure
        keycloakLogin.setEnabled(false);

        String hostURL = appConfiguration.getAuthConfiguration().getHostUrl();
        boolean sendAccessToken = false;

        authStateService.createRequest(hostURL, sendAccessToken, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {

                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG)
                        .show();

                if (authStateService.checkCertificateVerificationError(e)) {
                    Log.w("Certificate Pinning", "Certificate Pinning Validation Failed", e);

                    // run the UI updates on the UI thread
                    getActivity().runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            // hide the authentication button
                            keycloakLogin.setVisibility(view.GONE);

                            // update the UI to state the connection is insecure
                            authMessage.setText(getString(R.string.cert_pin_verification_failed) + e.getMessage());
                            background.setImageResource(R.drawable.ic_error_background);
                            logo.setImageResource(R.drawable.ic_lock);

                            // Show a warning message to the user
                            Snackbar.make(view, R.string.insecure_connection_prevent_auth, Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // No Certificate Pinning Errors, allow a user to login

                getActivity().runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        keycloakLogin.setEnabled(true);
                    }
                });

            }
        });
    }

}
