package com.feedhenry.securenativeandroidtemplate;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.feedhenry.securenativeandroidtemplate.domain.Constants;
import com.feedhenry.securenativeandroidtemplate.domain.models.Identity;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.features.authentication.AuthenticationDetailsFragment;
import com.feedhenry.securenativeandroidtemplate.features.authentication.AuthenticationFragment;
import com.feedhenry.securenativeandroidtemplate.features.authentication.providers.OpenIDAuthenticationProvider;
import com.feedhenry.securenativeandroidtemplate.features.storage.NotesDetailFragment;
import com.feedhenry.securenativeandroidtemplate.features.storage.NotesListFragment;
import com.feedhenry.securenativeandroidtemplate.domain.services.AuthStateService;
import com.feedhenry.securenativeandroidtemplate.mvp.components.HttpHelper;
import com.feedhenry.securenativeandroidtemplate.navigation.Navigator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, AuthenticationFragment.AuthenticationListener, NotesListFragment.NoteListListener, NotesDetailFragment.SaveNoteListener, AuthenticationDetailsFragment.AuthenticationDetailsListener, HasFragmentInjector {


    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Inject
    OpenIDAuthenticationProvider authProvider;

    @Inject
    Navigator navigator;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Inject
    Context context;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     * @param savedInstanceState - the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // initialise the httphelper
        HttpHelper.init();

        // load the main menu fragment
        navigator.navigateToHomeView(this);
    }

    /**
     * Handling to close the sidebar on back button press
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (navigator.canGoBack(this)){
            navigator.goBack(this);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Handling for Sidebar Navigation
     * @param item - the menu item that was selected from the menu
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Visit the Authentication Screen
        if (id == R.id.nav_home) {
            navigator.navigateToHomeView(this);
        }
        // Visit the Authentication Screen
        if (id == R.id.nav_authentication) {
            navigator.navigateToAuthenticationView(this);
        }
        // Visit the Access Control Screen
        if (id == R.id.nav_accesscontrol) {
            navigator.navigateToAccessControlView(this);
        }
        // Visit the Storage Screen
        if (id == R.id.nav_storage) {
            navigator.navigateToStorageView(this);
        }
        // Visit the Device Screen
        if (id == R.id.nav_device) {
            navigator.navigateToDeviceView(this);
        }
        // Visit the Network Screen
        if (id == R.id.nav_network) {
            navigator.navigateToNetworkView(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public AndroidInjector<Fragment> fragmentInjector() {
        return fragmentInjector;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODES.AUTH_CODE) {
            authProvider.onAuthResult(data);
        }
    }

    @Override
    public void onAuthSuccess(Identity identityData) {
        navigator.navigateToAuthenticateDetailsView(this, identityData);
    }

    @Override
    public void onAuthError(Exception error) {

    }

    @Override
    public void onLogoutSuccess(Identity state) {
        navigator.navigateToAuthenticationView(this);
    }

    @Override
    public void onLogoutError(Exception error) {

    }

    @Override
    public void onNoteClicked(Note note) {
        Log.i("SecureAndroidApp", "Note selected: " + note.getContent());
        navigator.navigateToSingleNoteView(this, note);
    }

    @Override
    public void onCreateNote() {
        navigator.navigateToSingleNoteView(this, null);
    }


    @Override
    public void onNoteSaved(Note note) {
        navigator.navigateToStorageView(this);
    }
}


