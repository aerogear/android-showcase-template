package com.aerogear.androidshowcase;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;

import com.aerogear.androidshowcase.providers.PushServiceProvider;
import com.aerogear.androidshowcase.features.authentication.AuthenticationDetailsFragment;
import com.aerogear.androidshowcase.features.authentication.AuthenticationFragment;
import com.aerogear.androidshowcase.features.authentication.providers.KeycloakAuthenticateProviderImpl;
import com.aerogear.androidshowcase.features.authentication.providers.OpenIDAuthenticationProvider;
import com.aerogear.androidshowcase.mvp.components.HttpHelper;
import com.aerogear.androidshowcase.navigation.Navigator;

import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.reactive.Requester;
import org.aerogear.mobile.core.reactive.Responder;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, AuthenticationFragment.AuthenticationListener, AuthenticationDetailsFragment.AuthenticationDetailsListener, HasFragmentInjector {


    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Inject
    OpenIDAuthenticationProvider authProvider;

    @Inject
    @Nullable
    AuthService authService;

    @Inject
    Navigator navigator;

    @Inject
    @Nullable
    PushServiceProvider pushServiceProvider;

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
     *
     * @param savedInstanceState - the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.fragment_title_home);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(this);

        restyleNavigationview();

        // initialise the httphelper
        HttpHelper.init();

        if (pushServiceProvider != null) {
            pushServiceProvider.registerDevice();
        }

        testNetwork();
        // load the main menu fragment
        navigator.navigateToHomeView(this, getString(R.string.fragment_title_home));
    }

    private void restyleNavigationview() {
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem item = m.getItem(i);
            if (item.getIcon() == null) {
                SpannableString newTitle = new SpannableString("  " + item.getTitle());
                newTitle.setSpan(new RelativeSizeSpan(.90f), 0 , newTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                item.setTitle(newTitle);
            } else {
                SpannableString newTitle = new SpannableString(item.getTitle());
                newTitle.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0 , newTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                item.setTitle(newTitle);
            }
        }
    }

    /**
     * Handling to close the sidebar on back button press
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (navigator.canGoBack(this)) {
            navigator.goBack(this);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Handling for Sidebar Navigation
     *
     * @param item - the menu item that was selected from the menu
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                navigator.navigateToHomeView(this, getString(R.string.fragment_title_home));
                break;
            case R.id.nav_identity_management:
                navigator.navigateToLandingIdentityManagement(this, getString(R.string.fragment_title_authenticate));
                break;
            case R.id.nav_identity_management_documentation:
                navigator.navigateToIdentityManagementDocumentation(this, getString(R.string.nav_documentation));
                break;
            case R.id.nav_identity_management_authentication:
                navigator.navigateToAuthenticationView(this, getString(R.string.nav_identity_management_authentication), getString(R.string.nav_identity_management_identity_profile));
                break;
            case R.id.nav_identity_management_sso:
                navigator.navigateToSSODocumentation(this, getString(R.string.nav_identity_management_sso));
                break;
            case R.id.nav_security:
                navigator.navigateToLandingSecurity(this, getString(R.string.fragment_title_security));
                break;
            case R.id.nav_security_documentation:
                navigator.navigateToSecurityDocumentation(this, getString(R.string.nav_documentation));
                break;
            case R.id.nav_security_device_trust:
                navigator.navigateToDeviceView(this, getString(R.string.nav_security_device_trust));
                break;
            case R.id.nav_security_storage:
                navigator.navigateToUnderConstructorView(this, getString(R.string.nav_security_storage));
                break;
            case R.id.nav_security_cert_pinning:
                navigator.navigateToUnderConstructorView(this, getString(R.string.nav_security_cert_pinning));
                break;
            case R.id.nav_push:
                navigator.navigateToLandingPush(this, getString(R.string.nav_push));
                break;
            case R.id.nav_push_documentation:
                navigator.navigateToPushDocumentation(this, getString(R.string.nav_documentation));
                break;
            case R.id.nav_push_messages:
                navigator.navigateToPushView(this, getString(R.string.nav_push_messages));
                break;
            case R.id.nav_metrics:
                navigator.navigateToLandingMetrics(this, getString(R.string.nav_metrics));
                break;
            case R.id.nav_metrics_documentation:
                navigator.navigateToMetricsDocumentation(this, getString(R.string.nav_documentation));
                break;
            case R.id.nav_metrics_device_profile_info:
                navigator.navigateToUnderConstructorView(this, getString(R.string.nav_metrics_device_profile_info));
                break;
            case R.id.nav_metrics_trust_check_info:
                navigator.navigateToUnderConstructorView(this, getString(R.string.nav_metrics_trust_check_info));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public AndroidInjector<Fragment> fragmentInjector() {
        return fragmentInjector;
    }

    // tag::onActivityResult[]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == KeycloakAuthenticateProviderImpl.LOGIN_RESULT_CODE) {
            //Put a note here that the null check is not how we expect people to use the SDK and link the good usage to the auth screen
            if (authService != null)
                authService.handleAuthResult(data);
        }
    }
    // end::onActivityResult[]

    @Override
    public void onAuthSuccess(final UserPrincipal user) {
        navigator.navigateToAuthenticateDetailsView(this, user, getString(R.string.nav_identity_management_identity_profile));
    }

    @Override
    public void onAuthError(final Exception error) {

    }

    @Override
    public void onLogoutSuccess(final UserPrincipal user) {
        navigator.navigateToAuthenticationView(this, getString(R.string.nav_identity_management_authentication), getString(R.string.nav_identity_management_identity_profile));
    }

    @Override
    public void onLogoutError(final Exception error) {

    }

    private void testNetwork() {
        HttpHelper.checkCertificates(this).respondOn(new AppExecutors().mainThread())
                .respondWith(new Responder<Boolean>() {
                    @Override
                    public void onResult(Boolean value) {
                        if (!value) {
                            CertificateErrorDialog dialog = new CertificateErrorDialog();
                            dialog.setGotoDocs(()-> {
                                navigator.navigateToSelfSignedCertificateDocumentation(MainActivity.this, "Getting Started");
                                dialog.dismiss();
                            });
                            dialog.show(getFragmentManager(), "CERT_ERROR");
                        }
                    }

                    @Override
                    public void onException(Exception exception) {

                    }
                });
    }

}


