package com.feedhenry.securenativeandroidtemplate.navigation;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.design.widget.Snackbar;
import com.feedhenry.securenativeandroidtemplate.BaseActivity;
import com.feedhenry.securenativeandroidtemplate.MainActivity;
import com.feedhenry.securenativeandroidtemplate.domain.Constants;
import com.feedhenry.securenativeandroidtemplate.features.accesscontrol.AccessControlFragment;
import com.feedhenry.securenativeandroidtemplate.features.authentication.AuthenticationDetailsFragment;
import com.feedhenry.securenativeandroidtemplate.features.authentication.AuthenticationFragment;
import com.feedhenry.securenativeandroidtemplate.features.device.DeviceFragment;
import com.feedhenry.securenativeandroidtemplate.features.home.HomeFragment;
import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.features.http.HttpFragment;
import com.feedhenry.securenativeandroidtemplate.features.network.NetworkFragment;
import com.feedhenry.securenativeandroidtemplate.features.storage.NotesDetailFragment;
import com.feedhenry.securenativeandroidtemplate.features.storage.NotesListFragment;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;
import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.user.UserPrincipal;
import javax.inject.Inject;

/**
 * A class to control the navigation of the app.
 */
public class Navigator {

    @Inject
    Context context;

    @Inject
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
        if (user != null) {
            navigateToAuthenticateDetailsView(activity, user);
        } else {
            loadFragment(activity, authFragment, AuthenticationFragment.TAG);
        }
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

    public void navigateToHttpView(MainActivity activity) {
        HttpFragment httpFragment = new HttpFragment();
        loadFragment(activity, httpFragment, HttpFragment.TAG);
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
        activity.setInformationTextResourceId(fragment.getHelpMessageResourceId());
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