package com.feedhenry.securenativeandroidtemplate.di;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.feedhenry.securenativeandroidtemplate.domain.crypto.AesGcmCrypto;
import com.feedhenry.securenativeandroidtemplate.domain.crypto.PreAndroidMSecureKeyStore;
import com.feedhenry.securenativeandroidtemplate.domain.crypto.SecureKeyStore;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.domain.repositories.NoteRepository;
import com.feedhenry.securenativeandroidtemplate.domain.repositories.NoteRepositoryImpl;
import com.feedhenry.securenativeandroidtemplate.domain.store.InMemoryNoteStore;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteDataStore;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteDataStoreFactory;
import com.feedhenry.securenativeandroidtemplate.domain.store.SecureFileNoteStore;
import com.feedhenry.securenativeandroidtemplate.domain.store.sqlite.SqliteNoteStore;
import com.feedhenry.securenativeandroidtemplate.features.authentication.providers.KeycloakAuthenticateProviderImpl;
import com.feedhenry.securenativeandroidtemplate.features.authentication.providers.OpenIDAuthenticationProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
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

    @Provides @Singleton @Named("fileStore")
    NoteDataStore providesNoteDataStore(Context context, AesGcmCrypto aesGcmCrypto) {
        return new SecureFileNoteStore(context, aesGcmCrypto);
    }

    @Provides @Singleton @Named("sqliteStore")
    NoteDataStore providesSqliteNoteDataStore(Context context) {
        return new SqliteNoteStore(context);
    }

    @Provides @Singleton
    NoteDataStoreFactory provideNoteDataStoreFactory(Context context, @Named("fileStore") NoteDataStore fileStore, @Named("sqliteStore") NoteDataStore sqlStore) {
        List<NoteDataStore> stores = new ArrayList<NoteDataStore>();
        stores.add(fileStore);
        stores.add(sqlStore);
        return new NoteDataStoreFactory(context, stores);
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
