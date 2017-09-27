package com.feedhenry.securenativeandroidtemplate.di;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.feedhenry.securenativeandroidtemplate.domain.crypto.AesGcmCrypto;
import com.feedhenry.securenativeandroidtemplate.domain.crypto.AndroidMSecureKeyStore;
import com.feedhenry.securenativeandroidtemplate.domain.crypto.NullAndroidSecureKeyStore;
import com.feedhenry.securenativeandroidtemplate.domain.crypto.PreAndroidMSecureKeyStore;
import com.feedhenry.securenativeandroidtemplate.domain.crypto.SecureKeyStore;
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
    AesGcmCrypto provideAesGcmCrypto(SecureKeyStore keyStore) {
        return new AesGcmCrypto(keyStore);
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
