package com.aerogear.androidshowcase.di;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.aerogear.androidshowcase.domain.crypto.AesCrypto;
import com.aerogear.androidshowcase.domain.crypto.PreAndroidMSecureKeyStore;
import com.aerogear.androidshowcase.domain.crypto.RsaCrypto;
import com.aerogear.androidshowcase.domain.crypto.SecureKeyStore;
import com.aerogear.androidshowcase.domain.repositories.NoteRepository;
import com.aerogear.androidshowcase.domain.repositories.NoteRepositoryImpl;
import com.aerogear.androidshowcase.domain.store.NoteDataStore;
import com.aerogear.androidshowcase.domain.store.NoteDataStoreFactory;
import com.aerogear.androidshowcase.domain.store.SecureFileNoteStore;
import com.aerogear.androidshowcase.domain.store.sqlite.SqliteNoteStore;
import com.aerogear.androidshowcase.features.authentication.providers.KeycloakAuthenticateProviderImpl;
import com.aerogear.androidshowcase.features.authentication.providers.OpenIDAuthenticationProvider;

import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.security.SecurityService;

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
    NoteDataStore providesNoteDataStore(Context context, AesCrypto aesCrypto) {
        return new SecureFileNoteStore(context, aesCrypto);
    }

    @Provides @Singleton @Named("sqliteStore")
    NoteDataStore providesSqliteNoteDataStore(Context context, RsaCrypto rsaCrypto) {
        return new SqliteNoteStore(context, rsaCrypto);
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
    AesCrypto provideAesGcmCrypto(SecureKeyStore keyStore) {
        return new AesCrypto(keyStore);
    }

    @Provides @Singleton
    MobileCore provideMobileCore(Context context) {
        MobileCore mobileCore = MobileCore.init(context);
        return mobileCore;
    }

    @Provides @Singleton
    SecurityService securityService(Context context, MobileCore mobileCore) {
        return mobileCore.getInstance(SecurityService.class);
    }

    @Provides @Singleton
    AuthService provideAuthService(Context context, MobileCore mobileCore) {
        AuthService authService = mobileCore.getInstance(AuthService.class);
        AuthServiceConfiguration authServiceConfig = new AuthServiceConfiguration.AuthConfigurationBuilder()
                .withRedirectUri("com.aerogear.androidshowcase:/callback")
                .build();
        authService.init(context, authServiceConfig);
        return authService;
    }
}
