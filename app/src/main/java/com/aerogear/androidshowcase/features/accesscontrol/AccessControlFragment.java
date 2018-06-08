package com.aerogear.androidshowcase.features.accesscontrol;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.aerogear.androidshowcase.R;
import com.aerogear.androidshowcase.domain.Constants;
import com.aerogear.androidshowcase.features.accesscontrol.presenters.AccessControlViewPresenter;
import com.aerogear.androidshowcase.features.accesscontrol.views.AccessControlView;
import com.aerogear.androidshowcase.features.accesscontrol.views.AccessControlViewImpl;
import com.aerogear.androidshowcase.mvp.views.BaseFragment;
import com.aerogear.androidshowcase.navigation.Navigator;
import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.user.UserPrincipal;
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
    AuthService authService;

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
        UserPrincipal user = authService.currentUser();
        int backgroundColor = getResources().getColor(R.color.orange);
        int textColor = getResources().getColor(R.color.white);
        if (user != null) {
            if (user.hasRealmRole(Constants.ACCESS_CONTROL_ROLES.ROLE_MOBILE_USER)) {
                setGranted(R.string.role_mobile_user_label, roleMobileUser);
                roleMobileUser.setBackgroundColor(backgroundColor);
                roleMobileUser.setTextColor(textColor);
            }
            if (user.hasRealmRole(Constants.ACCESS_CONTROL_ROLES.ROLE_API_ACCESS)) {
                setGranted(R.string.role_api_access_label, roleApiAccess);
                roleApiAccess.setBackgroundColor(backgroundColor);
                roleApiAccess.setTextColor(textColor);
            }
            if (user.hasRealmRole(Constants.ACCESS_CONTROL_ROLES.ROLE_SUPERUSER)) {
                setGranted(R.string.role_superuser_label, roleSuperuser);
                roleSuperuser.setBackgroundColor(backgroundColor);
                roleSuperuser.setTextColor(textColor);
            }
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

}
