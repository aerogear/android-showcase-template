package com.feedhenry.securenativeandroidtemplate.domain.crypto;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;

import javax.crypto.KeyGenerator;

/**
 * Implement the SecureKeyStore using the Android native KeyStore APIs. This requires Android M and later versions, as from this version, the Android keyStore supports generating AES keys.
 */
@RequiresApi(Build.VERSION_CODES.M)
public class AndroidMSecureKeyStore extends SecureKeyStoreImpl implements SecureKeyStore {


    @Override
    public String getSupportedAESMode() {
        return ALG_AES_GCM_NOPADDING;
    }

    @Override
    public String getSupportedRSAMode() {
        return ALG_RSA_ECB_PCKS1Padding;
    }

    @Override
    public boolean hasSecretKey(String keyAlias) throws GeneralSecurityException, IOException {
        KeyStore ks = loadKeyStore();
        return ks.containsAlias(keyAlias);
    }

    @Override
    public Key getSecretKey(String keyAlias) throws GeneralSecurityException, IOException {
        KeyStore ks = loadKeyStore();
        return loadKeyStore().getKey(keyAlias, null);
    }

    // tag::generateAESKey[]
    /**
     * Generate the AES key for encryption/decryption. The key will be 128bit and it can only be used with AES/GCM/NoPadding mode.
     * @param keyAlias the key alias
     * @throws GeneralSecurityException
     * @throws IOException
     */
    @Override
    public void generateAESKey(String keyAlias) throws GeneralSecurityException, IOException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
        //TODO: further control if user authentication is required for accessing the keys
        KeyGenParameterSpec keyGenerationParameters = new KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setKeySize(AES_KEYSIZE_128)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build();
        keyGenerator.init(keyGenerationParameters);
        keyGenerator.generateKey();
    }
    // end::generateAESKey[]

    /**
     * Generate a public/private key pair for encryption/decryption purpose only. The key will be 2048bit and it can only be used with RSA/ECB/PKCS1Padding mode.
     * @param keyAlias
     * @throws GeneralSecurityException
     * @throws IOException
     */
    @Override
    public void generatePrivateKeyPair(String keyAlias) throws GeneralSecurityException, IOException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);
        KeyGenParameterSpec keyPairGenerationParameters = new KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_DECRYPT)
                .setKeySize(RSA_KEY_SIZE)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build();
        keyPairGenerator.initialize(keyPairGenerationParameters);
        keyPairGenerator.generateKeyPair();
    }

    @Override
    public boolean hasKeyPair(String keyAlias) throws GeneralSecurityException, IOException {
        KeyStore ks = loadKeyStore();
        return ks.containsAlias(keyAlias);
    }

    @Override
    public void deleteKey(String keyAlias) throws GeneralSecurityException, IOException {
        if (hasSecretKey(keyAlias)) {
            KeyStore ks = loadKeyStore();
            ks.deleteEntry(keyAlias);
        }
    }
}
