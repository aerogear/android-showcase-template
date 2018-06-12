package com.aerogear.androidshowcase.navigation;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.widget.Toast;
import com.aerogear.androidshowcase.BaseActivity;
import com.aerogear.androidshowcase.MainActivity;
import com.aerogear.androidshowcase.domain.Constants;
import com.aerogear.androidshowcase.features.accesscontrol.AccessControlFragment;
import com.aerogear.androidshowcase.features.authentication.AuthenticationDetailsFragment;
import com.aerogear.androidshowcase.features.authentication.AuthenticationFragment;
import com.aerogear.androidshowcase.features.device.DeviceFragment;
import com.aerogear.androidshowcase.features.home.HomeFragment;
import com.aerogear.androidshowcase.R;
import com.aerogear.androidshowcase.domain.models.Note;
import com.aerogear.androidshowcase.features.network.NetworkFragment;
import com.aerogear.androidshowcase.features.push.PushFragment;
import com.aerogear.androidshowcase.features.storage.NotesDetailFragment;
import com.aerogear.androidshowcase.features.storage.NotesListFragment;
import com.aerogear.androidshowcase.mvp.views.BaseFragment;
import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.user.UserPrincipal;
import javax.inject.Inject;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

/**
 * A class to control the navigation of the app.
 */
public class Navigator {

    @Inject
    Context context;

    @Inject @Nullable
    AuthService authService;

    @Inject
    public Navigator() {

    }

    public void navigateToHomeView(BaseActivity activity) {
        HomeFragment homeView = new HomeFragment();
        loadFragment(activity, homeView, HomeFragment.TAG);
    }

    public void navigateToAuthenticationView(final BaseActivity activity) {
        AuthenticationFragment authFragment = new AuthenticationFragment();
        UserPrincipal user = authService.currentUser();
        if (!isConfigured("keycloak")) {
            showNotConfiguredDialog("keycloak");
            return;
        }
        if (user != null) {
            navigateToAuthenticateDetailsView(activity, user);
        } else {
            loadFragment(activity, authFragment, AuthenticationFragment.TAG);
        }
    }

    private void showNotConfiguredDialog(String keycloak) {
        Toast.makeText(context, keycloak + " is not in mobile-core.json", Toast.LENGTH_LONG).show();
    }

    private boolean isConfigured(String serviceId) {
        MobileCore core = MobileCore.getInstance();
        ServiceConfiguration configuration = core
            .getServiceConfigurationById(serviceId);

        return configuration != null;
    }

    public void navigateToAuthenticateDetailsView(final BaseActivity activity, final UserPrincipal user) {
        AuthenticationDetailsFragment authDetailsView = AuthenticationDetailsFragment.forIdentityData(user);
        loadFragment(activity, authDetailsView, AuthenticationDetailsFragment.TAG);
    }

    public void navigateToAccessControlView(final BaseActivity activity) {
        UserPrincipal currentUser = authService.currentUser();
        if (currentUser != null && currentUser.hasRealmRole(Constants.ACCESS_CONTROL_ROLES.ROLE_MOBILE_USER)) {
            AccessControlFragment accessControView = new AccessControlFragment();
            loadFragment(activity, accessControView, AccessControlFragment.TAG);
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.not_authenticated, Snackbar.LENGTH_LONG).show();
            navigateToAuthenticationView(activity);
        }
    }

    public void navigateToStorageView(BaseActivity activity) {
        NotesListFragment notesListView = new NotesListFragment();
        loadFragment(activity, notesListView, NotesListFragment.TAG);
    }

    public void navigateToSingleNoteView(BaseActivity activity, Note note) {
        NotesDetailFragment noteDetails = NotesDetailFragment.forNote(note);
        loadFragment(activity, noteDetails, NotesDetailFragment.TAG);
    }

    public void navigateToDeviceView(MainActivity activity) {
        DeviceFragment deviceFragment = new DeviceFragment();
        loadFragment(activity, deviceFragment, DeviceFragment.TAG);
    }

    public void navigateToPushView(MainActivity activity) {
        PushFragment pushFragment = new PushFragment();
        loadFragment(activity, pushFragment, PushFragment.TAG);
    }

    public void navigateToNetworkView(MainActivity activity) {

        UserPrincipal currentUser = authService.currentUser();
        if (currentUser != null && currentUser.hasRealmRole(Constants.ACCESS_CONTROL_ROLES.ROLE_API_ACCESS)) {
            NetworkFragment networkFragment = new NetworkFragment();
            loadFragment(activity, networkFragment, NetworkFragment.TAG);
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.not_authenticated_api_access, Snackbar.LENGTH_LONG).show();
            navigateToAuthenticationView(activity);
        }
    }

    public void loadFragment(BaseActivity activity, BaseFragment fragment, String fragmentTag) {
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        transaction
                .addToBackStack(null)
                .replace(R.id.frameLayout, fragment, fragmentTag)
                .commit();
    }

    public boolean canGoBack(BaseActivity activity) {
        FragmentManager fm = activity.getFragmentManager();
        return fm.getBackStackEntryCount() > 0;
    }

    public void goBack(BaseActivity activity) {
        FragmentManager fm = activity.getFragmentManager();
        fm.popBackStack();
    }



}