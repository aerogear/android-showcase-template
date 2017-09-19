package com.feedhenry.securenativeandroidtemplate.navigation;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.feedhenry.securenativeandroidtemplate.BaseActivity;
import com.feedhenry.securenativeandroidtemplate.domain.Constants;
import com.feedhenry.securenativeandroidtemplate.features.authentication.AuthenticationDetailsFragment;
import com.feedhenry.securenativeandroidtemplate.features.authentication.AuthenticationFragment;
import com.feedhenry.securenativeandroidtemplate.features.home.HomeFragment;
import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.features.storage.NotesListFragment;
import com.feedhenry.securenativeandroidtemplate.mvp.components.AuthHelper;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;

import net.openid.appauth.AuthState;

import javax.inject.Inject;

/**
 * A class to control the navigation of the app.
 */
public class Navigator {

    @Inject
    Context context;

    @Inject
    public Navigator() {

    }

    public void navigateToHomeView(BaseActivity activity) {
        HomeFragment homeView = new HomeFragment();
        loadFragment(activity, homeView);
    }

    public void navigateToAuthenticationView(BaseActivity activity) {
        AuthHelper authHelper = new AuthHelper(context);
        if(authHelper.isAuthorized()) {
            navigateToAuthenticateDetailsView(activity, authHelper.getIdentityInfomation());
        } else {
            AuthenticationFragment authFragment = new AuthenticationFragment();
            loadFragment(activity, authFragment);
        }
    }

    public void navigateToAuthenticateDetailsView(BaseActivity activity, String identityData) {
        AuthenticationDetailsFragment authDetailsView = new AuthenticationDetailsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.TOKEN_FIELDS.IDENTITY_DATA, identityData);
        authDetailsView.setArguments(args);
        loadFragment(activity, authDetailsView);
    }

    public void navigateToStorageView(BaseActivity activity) {
        NotesListFragment notesListView = new NotesListFragment();
        loadFragment(activity, notesListView);
    }

    public void navigateToSingleNoteView(BaseActivity activity, Note note) {
        //TODO: implement me!
    }

    public void loadFragment(BaseActivity activity, BaseFragment fragment) {
        activity.setInformationTextResourceId(fragment.getHelpMessageResourceId());
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentManager fm = activity.getFragmentManager();
        fm.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.frameLayout, fragment)
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
