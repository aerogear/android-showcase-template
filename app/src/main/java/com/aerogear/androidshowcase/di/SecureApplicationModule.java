package com.aerogear.androidshowcase.di;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;

import com.aerogear.androidshowcase.providers.PushServiceProvider;
import com.aerogear.androidshowcase.domain.crypto.AesCrypto;
import com.aerogear.androidshowcase.domain.crypto.AndroidMSecureKeyStore;
import com.aerogear.androidshowcase.domain.crypto.NullAndroidSecureKeyStore;
import com.aerogear.androidshowcase.domain.crypto.PreAndroidMSecureKeyStore;
import com.aerogear.androidshowcase.domain.crypto.RsaCrypto;
import com.aerogear.androidshowcase.domain.crypto.SecureKeyStore;
import com.aerogear.androidshowcase.features.authentication.providers.KeycloakAuthenticateProviderImpl;
import com.aerogear.androidshowcase.features.authentication.providers.OpenIDAuthenticationProvider;

import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.security.SecurityService;

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

    @Provides @Singleton
    OpenIDAuthenticationProvider provideAuthProvider(KeycloakAuthenticateProviderImpl keycloakClient) {
        return keycloakClient;
    }

    // tag::securityServiceInit[]
    @Provides @Singleton
    SecurityService provideSecurityService() {
        return MobileCore.getInstance().getService(SecurityService.class);
    }
    // end::securityServiceInit[]

    @Provides @Singleton @Nullable
    PushServiceProvider providePushServiceProvider() {
        if (MobileCore.getInstance().getServiceConfigurationsByType("push") == null) {
            return null;
        }
        return PushServiceProvider.getInstance();
    }

    // tag::authServiceInit[]
    @Provides @Singleton @Nullable
    AuthService provideAuthService(Context context) {
        MobileCore core = MobileCore.getInstance();
        if (core.getServiceConfigurationByType("keycloak") == null ) {
            return null;//We are allowing this to be nullable because keycloak is not guaranteed to
                        //be configured.  We are not returning an Optional because we can't guarantee
                        //Optional is available on Android M and L and we don't want to add Guava.
        }
        AuthService authService = core.getService(AuthService.class);
        AuthServiceConfiguration authServiceConfig = new AuthServiceConfiguration.AuthConfigurationBuilder()
                .withRedirectUri("com.aerogear.androidshowcase:/callback")
                .build();

        authService.init(context, authServiceConfig);
        return authService;
    }
    // end::authServiceInit[]
}
