package com.aerogear.androidshowcase.di;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import com.aerogear.androidshowcase.domain.crypto.AesCrypto;
import com.aerogear.androidshowcase.domain.crypto.AndroidMSecureKeyStore;
import com.aerogear.androidshowcase.domain.crypto.NullAndroidSecureKeyStore;
import com.aerogear.androidshowcase.domain.crypto.PreAndroidMSecureKeyStore;
import com.aerogear.androidshowcase.domain.crypto.RsaCrypto;
import com.aerogear.androidshowcase.domain.crypto.SecureKeyStore;
import com.aerogear.androidshowcase.domain.repositories.NoteRepository;
import com.aerogear.androidshowcase.domain.repositories.NoteRepositoryImpl;
import com.aerogear.androidshowcase.domain.services.NoteCrudlService;
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
    SecureKeyStore providesSecureKeyStore(Context context) {
        int currentSDKVersion = Build.VERSION.SDK_INT;
        //For demo purpose, here we choose different implementation based on the current Android version.
        //However, this means is a device is upgrade from pre-Android M to Android M, the keys will be lost.
        //So it's best to just choose one implementation, based on the minimum API level requirement of the app.
        if ( currentSDKVersion >= Build.VERSION_CODES.M) {
            return new AndroidMSecureKeyStore();
        } else if (currentSDKVersion >= Build.VERSION_CODES.KITKAT) {
            return new PreAndroidMSecureKeyStore(context);
        } else {
            return new NullAndroidSecureKeyStore();
        }
    }

    @Provides @Singleton
    AesCrypto provideAesCrypto(SecureKeyStore keyStore) {
        return new AesCrypto(keyStore);
    }

    @Provides @Singleton
    RsaCrypto provideRsaCrypto(SecureKeyStore keyStore) {
        return new RsaCrypto(keyStore);
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

    // tag::securityServiceInit[]
    @Provides @Singleton
    SecurityService provideSecurityService() {
        return MobileCore.getInstance().getService(SecurityService.class);
    }
    // end::securityServiceInit[]

    // tag::authServiceInit[]
    @Provides @Singleton
    AuthService provideAuthService(Context context) {
        AuthService authService = MobileCore.getInstance().getService(AuthService.class);
        AuthServiceConfiguration authServiceConfig = new AuthServiceConfiguration.AuthConfigurationBuilder()
                .withRedirectUri("com.aerogear.androidshowcase:/callback")
                .build();

        authService.init(context, authServiceConfig);
        return authService;
    }
    // end::authServiceInit[]
}
