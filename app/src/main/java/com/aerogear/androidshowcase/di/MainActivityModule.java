package com.aerogear.androidshowcase.di;

import com.aerogear.androidshowcase.MainActivity;
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
