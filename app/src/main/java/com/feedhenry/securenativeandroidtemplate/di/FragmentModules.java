package com.feedhenry.securenativeandroidtemplate.di;

import com.feedhenry.securenativeandroidtemplate.MainActivity;
import com.feedhenry.securenativeandroidtemplate.features.authentication.AuthenticationDetailsFragment;
import com.feedhenry.securenativeandroidtemplate.features.authentication.AuthenticationFragment;
import com.feedhenry.securenativeandroidtemplate.features.authentication.providers.KeycloakAuthenticateProviderImpl;
import com.feedhenry.securenativeandroidtemplate.features.home.HomeFragment;
import com.feedhenry.securenativeandroidtemplate.features.storage.NotesListFragment;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

/**
 * Define dependencies used by the NotesListFragment. Used by Dagger2 to build dependency graph.
 */

@Module
public abstract class FragmentModules {

    @ContributesAndroidInjector
    abstract NotesListFragment contributeNotesListFragmentInjector();

    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeFragmentInjector();

    @ContributesAndroidInjector
    abstract AuthenticationFragment contributeAuthenticationFragmentInjector();

    @ContributesAndroidInjector
    abstract AuthenticationDetailsFragment contributeAuthenticationDetailsFragmentInjector();
}
