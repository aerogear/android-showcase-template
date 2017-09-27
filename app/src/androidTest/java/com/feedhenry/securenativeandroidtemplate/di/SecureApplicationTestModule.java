package com.feedhenry.securenativeandroidtemplate.di;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.feedhenry.securenativeandroidtemplate.domain.crypto.AesGcmCrypto;
import com.feedhenry.securenativeandroidtemplate.domain.crypto.PreAndroidMSecureKeyStore;
import com.feedhenry.securenativeandroidtemplate.domain.crypto.SecureKeyStore;
import com.feedhenry.securenativeandroidtemplate.domain.repositories.NoteRepository;
import com.feedhenry.securenativeandroidtemplate.domain.repositories.NoteRepositoryImpl;
import com.feedhenry.securenativeandroidtemplate.domain.store.InMemoryNoteStore;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteDataStore;
import com.feedhenry.securenativeandroidtemplate.features.authentication.providers.KeycloakAuthenticateProviderImpl;
import com.feedhenry.securenativeandroidtemplate.features.authentication.providers.OpenIDAuthenticationProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

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
    NoteDataStore provideNoteDataStore() {
        return new InMemoryNoteStore();
    }

    @Provides @Singleton NoteRepository provideNoteRepository(NoteRepositoryImpl noteRepo) {
        return noteRepo;
    }

    @Provides @Singleton
    OpenIDAuthenticationProvider provideAuthProvider() {
        return mock(KeycloakAuthenticateProviderImpl.class);
    }

    @Provides @Singleton
    SecureKeyStore provideSecureKeyStore(Context context) {
        return new PreAndroidMSecureKeyStore(context);
    }

    @Provides @Singleton
    AesGcmCrypto provideAesGcmCrypto(SecureKeyStore keyStore) {
        return new AesGcmCrypto(keyStore);
    }
}
