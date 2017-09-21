package com.feedhenry.securenativeandroidtemplate.navigation;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;

import com.feedhenry.securenativeandroidtemplate.BaseActivity;
import com.feedhenry.securenativeandroidtemplate.domain.Constants;
import com.feedhenry.securenativeandroidtemplate.features.authentication.AuthenticationDetailsFragment;
import com.feedhenry.securenativeandroidtemplate.features.authentication.AuthenticationFragment;
import com.feedhenry.securenativeandroidtemplate.features.home.HomeFragment;
import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.features.storage.NotesDetailFragment;
import com.feedhenry.securenativeandroidtemplate.features.storage.NotesListFragment;
import com.feedhenry.securenativeandroidtemplate.mvp.components.AuthHelper;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;

import net.openid.appauth.TokenResponse;

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
        loadFragment(activity, homeView, HomeFragment.TAG);
    }

    public void navigateToAuthenticationView(BaseActivity activity) {
        AuthenticationFragment authFragment = new AuthenticationFragment();
        AuthHelper authHelper = new AuthHelper(context);
        if(authHelper.isAuthorized()) {
            navigateToAuthenticateDetailsView(activity, authHelper.getIdentityInfomation());
        } else {
            loadFragment(activity, authFragment, AuthenticationFragment.TAG);
        }
    }

    public void navigateToAuthenticateDetailsView(BaseActivity activity, String identityData) {
        AuthenticationDetailsFragment authDetailsView = AuthenticationDetailsFragment.forIdentityData(identityData);
        loadFragment(activity, authDetailsView, AuthenticationDetailsFragment.TAG);
    }

    public void navigateToStorageView(BaseActivity activity) {
        NotesListFragment notesListView = new NotesListFragment();
        loadFragment(activity, notesListView, NotesListFragment.TAG);
    }

    public void navigateToSingleNoteView(BaseActivity activity, Note note) {
        NotesDetailFragment noteDetails = NotesDetailFragment.forNote(note);
        loadFragment(activity, noteDetails, NotesDetailFragment.TAG);
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