package com.feedhenry.securenativeandroidtemplate.di;

import com.feedhenry.securenativeandroidtemplate.MainActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Define dependencies for the MainActivity. Used by Dagger2 to build dependency graph.
 */
@Module
public abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = {FragmentModules.class})
    abstract MainActivity contributeMainActivityInjector();

}
