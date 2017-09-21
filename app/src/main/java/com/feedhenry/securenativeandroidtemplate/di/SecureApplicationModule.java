package com.feedhenry.securenativeandroidtemplate.di;

import android.app.Application;
import android.content.Context;

import com.feedhenry.securenativeandroidtemplate.domain.crypto.AesGcmCrypto;
import com.feedhenry.securenativeandroidtemplate.domain.repositories.NoteRepository;
import com.feedhenry.securenativeandroidtemplate.domain.repositories.NoteRepositoryImpl;
import com.feedhenry.securenativeandroidtemplate.domain.services.NoteCrudlService;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteDataStore;
import com.feedhenry.securenativeandroidtemplate.domain.store.SecureFileNoteStore;
import com.feedhenry.securenativeandroidtemplate.features.authentication.providers.KeycloakAuthenticateProviderImpl;
import com.feedhenry.securenativeandroidtemplate.features.authentication.providers.OpenIDAuthenticationProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Define the dependencies for the app. Used by Dagger2 to build dependency graph.
 */

@Module
public class SecureApplicationModule {

    @Provides @Singleton
    Context provideApplicationContext(Application app) {
        return app;
    }

    @Provides @Singleton
    AesGcmCrypto provideAesGcmCrypto() {
        return new AesGcmCrypto();
    }

    @Provides @Singleton
    NoteDataStore providesNoteDataStore(Context context, AesGcmCrypto aesGcmCrypto) {
        return new SecureFileNoteStore(context, aesGcmCrypto);
    }

    @Provides @Singleton NoteRepository provideNoteRepository(NoteRepositoryImpl noteRepository) {
        return noteRepository;
    }

    @Provides @Singleton
    OpenIDAuthenticationProvider provideAuthProvider(KeycloakAuthenticateProviderImpl keycloakClient) {
        return keycloakClient;
    }

    @Provides @Singleton
    NoteCrudlService provideNoteCrudleService(NoteRepositoryImpl noteRepository) {
        return new NoteCrudlService(noteRepository);
    }
}
