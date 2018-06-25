package com.aerogear.androidshowcase.di;


import com.aerogear.androidshowcase.features.authentication.AuthenticationDetailsFragment;
import com.aerogear.androidshowcase.features.authentication.AuthenticationFragment;
import com.aerogear.androidshowcase.features.device.DeviceFragment;
import com.aerogear.androidshowcase.features.documentation.DocumentationFragment;
import com.aerogear.androidshowcase.features.home.HomeFragment;
import com.aerogear.androidshowcase.features.landing.LandingFragment;
import com.aerogear.androidshowcase.features.push.PushFragment;
import com.aerogear.androidshowcase.features.underconstruction.UnderConstructionFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Define dependencies used by the NotesListFragment. Used by Dagger2 to build dependency graph.
 */

@Module
public abstract class FragmentModules {

    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeFragmentInjector();

    @ContributesAndroidInjector
    abstract AuthenticationFragment contributeAuthenticationFragmentInjector();

    @ContributesAndroidInjector
    abstract AuthenticationDetailsFragment contributeAuthenticationDetailsFragmentInjector();

    @ContributesAndroidInjector
    abstract DeviceFragment contributeDeviceFragmentInjector();

    @ContributesAndroidInjector
    abstract PushFragment contributePushFragmentInjector();

    @ContributesAndroidInjector
    abstract DocumentationFragment contributeDocumentationFragmentInjector();

    @ContributesAndroidInjector
    abstract UnderConstructionFragment contributeUnderConstructionFragmentInjector();

    @ContributesAndroidInjector
    abstract LandingFragment contributeLandingFragmentInjector();
}
