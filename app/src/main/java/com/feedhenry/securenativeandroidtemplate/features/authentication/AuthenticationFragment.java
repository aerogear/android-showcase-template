package com.feedhenry.securenativeandroidtemplate.features.authentication;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.models.Identity;
import com.feedhenry.securenativeandroidtemplate.features.authentication.presenters.AuthenticationViewPresenter;
import com.feedhenry.securenativeandroidtemplate.features.authentication.views.AuthenticationView;
import com.feedhenry.securenativeandroidtemplate.features.authentication.views.AuthenticationViewImpl;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;

import net.openid.appauth.AuthState;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

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
    AuthenticationViewPresenter authenticationViewPresenter;

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
            public void showAuthError(Exception error) {
                showMessage(R.string.authentication_failed);
                if (authenticationListener != null) {
                    authenticationListener.onAuthError(error);
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

}
