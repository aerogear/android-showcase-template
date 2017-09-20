package com.feedhenry.securenativeandroidtemplate.di;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.feedhenry.securenativeandroidtemplate.domain.repositories.NoteRepository;
import com.feedhenry.securenativeandroidtemplate.domain.repositories.NoteRepositoryImpl;
import com.feedhenry.securenativeandroidtemplate.domain.store.InMemoryNoteStore;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteDataStoreFactory;
import com.feedhenry.securenativeandroidtemplate.features.authentication.providers.KeycloakAuthenticateProviderImpl;
import com.feedhenry.securenativeandroidtemplate.features.authentication.providers.OpenIDAuthenticationProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Define the DI providers for the tests here.
 */
@Module
public class SecureApplicationTestModule {


    @Provides @Singleton
    Context provideApplicationContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    @Provides @Singleton
    NoteDataStoreFactory provideNoteDataStoreFactory() {
        NoteDataStoreFactory dataStoreFactory = mock(NoteDataStoreFactory.class);
        when(dataStoreFactory.getDataStore()).thenReturn(new InMemoryNoteStore());
        return dataStoreFactory;
    }

    @Provides @Singleton NoteRepository provideNoteRepository(NoteRepositoryImpl noteRepo) {
        return noteRepo;
    }

    @Provides @Singleton
    OpenIDAuthenticationProvider provideAuthProvider() {
        return mock(KeycloakAuthenticateProviderImpl.class);
    }
}
