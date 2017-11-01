package com.feedhenry.securenativeandroidtemplate.features.accesscontrol;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.Constants;
import com.feedhenry.securenativeandroidtemplate.features.accesscontrol.presenters.AccessControlViewPresenter;
import com.feedhenry.securenativeandroidtemplate.features.accesscontrol.views.AccessControlView;
import com.feedhenry.securenativeandroidtemplate.features.accesscontrol.views.AccessControlViewImpl;
import com.feedhenry.securenativeandroidtemplate.domain.services.AuthStateService;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;
import com.feedhenry.securenativeandroidtemplate.navigation.Navigator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccessControlFragment extends BaseFragment<AccessControlViewPresenter, AccessControlView> {

    public static final String TAG = "accessControl";

    @Inject
    AccessControlViewPresenter accessControlViewPresenter;

    @Inject
    AuthStateService authState;

    @Inject
    Navigator navigator;

    @BindView(R.id.roleMobileUser)
    TextView roleMobileUser;

    @BindView(R.id.roleApiAccess)
    TextView roleApiAccess;

    @BindView(R.id.roleSuperuser)
    TextView roleSuperuser;

    View view;

    public AccessControlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_access_control, container, false);
        ButterKnife.bind(this, view);

        // perform access control checks on create
        performAccessControl();

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
        this.accessControlViewPresenter = null;
    }

    @Override
    protected AccessControlViewPresenter initPresenter() {
        return accessControlViewPresenter;
    }

    /**
     * Perform some access control checks a give a UI indicator if the user has a certain role.
     */
    public void performAccessControl() {
        int color = Color.argb(50, 89, 151, 93);
        if (authState.hasRole(Constants.ACCESS_CONTROL_ROLES.ROLE_MOBILE_USER)) {
            setGranted(R.string.role_mobile_user_label, roleMobileUser);
            roleMobileUser.setBackgroundColor(color);
        }
        if (authState.hasRole(Constants.ACCESS_CONTROL_ROLES.ROLE_API_ACCESS)) {
            setGranted(R.string.role_api_access_label, roleApiAccess);
            roleApiAccess.setBackgroundColor(color);
        }
        if (authState.hasRole(Constants.ACCESS_CONTROL_ROLES.ROLE_SUPERUSER)) {
            setGranted(R.string.role_superuser_label, roleSuperuser);
            roleSuperuser.setBackgroundColor(color);
        }
    }

    /**
     * Update the text on an access control text view to show the permission granted indicator
     * @param uiElement - the UI reference for the label
     * @param textview - the textview ui element to update the text for
     */
    public void setGranted(int uiElement, TextView textview) {
        textview.setText(getText(uiElement) + " (" + getText(R.string.access_granted) + ")");
    }

    @Override
    protected AccessControlView initView() {
        return new AccessControlViewImpl(this) {
        };
    }

    @Override
    public int getHelpMessageResourceId() {
        return R.string.popup_accesscontrol_fragment;
    }

}
