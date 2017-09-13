package com.feedhenry.securenativeandroidtemplate.di;

import com.feedhenry.securenativeandroidtemplate.MainActivity;
import com.feedhenry.securenativeandroidtemplate.features.authentication.providers.KeycloakAuthenticateProviderImpl;
import com.feedhenry.securenativeandroidtemplate.features.authentication.providers.OpenIDAuthenticationProvider;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

/**
 * Define dependencies for the MainActivity. Used by Dagger2 to build dependency graph.
 */
@Module
public abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = {FragmentModules.class})
    abstract MainActivity contributeMainActivityInjector();

}
