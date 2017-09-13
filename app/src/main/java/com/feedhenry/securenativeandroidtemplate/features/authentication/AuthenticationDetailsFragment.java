package com.feedhenry.securenativeandroidtemplate.features.authentication;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.Constants;
import com.feedhenry.securenativeandroidtemplate.features.authentication.presenters.AuthenticationDetailsPresenter;
import com.feedhenry.securenativeandroidtemplate.features.authentication.views.AuthenticationDetailsView;
import com.feedhenry.securenativeandroidtemplate.features.authentication.views.AuthenticationDetailsViewImpl;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

/**
 * A fragment for showing the user details view after they have logged in.
 */
public class AuthenticationDetailsFragment extends BaseFragment<AuthenticationDetailsPresenter, AuthenticationDetailsView> {

    @Inject
    AuthenticationDetailsPresenter authDetailsPresenter;

    @BindView(R.id.authResView)
    TextView authResField;

    View view;

    public AuthenticationDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_authentication_details, container, false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();
        showAuthToken(args);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(this);
        super.onAttach(activity);
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
        };
    }

    @Override
    public int getHelpMessageResourceId() {
        return R.string.popup_authentication_fragment;
    }

    private void showAuthToken(Bundle args) {
        String token = args.getString(Constants.TOKEN_FIELDS.AUTH_TOKEN);
        if (token != null ) {
            authResField.setText(token);
        }
    }
}
