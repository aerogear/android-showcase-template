package com.feedhenry.securenativeandroidtemplate.domain.crypto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.util.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;

/**
 * Implement the SecureKeyStore interface for pre Android M devices, but post Android KITKAT (API level 19 or v4.4).
 * In these versions the Android KeyStore only supports generating/persisting RSA key pairs. So we will used SharedPreferences to persist the encrypted AES key.
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
public class PreAndroidMSecureKeyStore extends SecureKeyStoreImpl implements SecureKeyStore {

    private static final String SHARE_PREF_KEY_NAME = "RANDOM_KEYS";
    private static final int BASE64_FLAG = Base64.NO_WRAP;
    private static final int BIT_PER_BYTE = 8;

    private static final String RSA_KEY_ALIAS = "com.feedhenry.secureapp.rsakeypair";
    private Context context;
    private SharedPreferences sharedPreferences;

    @Inject
    public PreAndroidMSecureKeyStore(Context context) {
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(SHARE_PREF_KEY_NAME, Context.MODE_PRIVATE);
    }

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
        return this.sharedPreferences.contains(keyAlias);
    }

    // tag::getSecretKey[]
    @Override
    public Key getSecretKey(String keyAlias) throws GeneralSecurityException, IOException {
        String encodedKey = this.sharedPreferences.getString(keyAlias, null);
        if (encodedKey != null) {
            byte[] encryptedKeyBytes = Base64.decode(encodedKey, BASE64_FLAG);
            byte[] keyBytes = rsaDecrypt(encryptedKeyBytes);
            return new SecretKeySpec(keyBytes, KeyProperties.KEY_ALGORITHM_AES);
        }
        return null;
    }
    // end::getSecretKey[]

    // tag::generateAESKey[]
    @Override
    public void generateAESKey(String keyAlias) throws GeneralSecurityException, IOException {
        byte[] secretKey = generateSecretKey(AES_KEYSIZE_128);
        byte[] encryptedKey = rsaEncrypt(secretKey);
        String encodedSecretKey = Base64.encodeToString(encryptedKey, BASE64_FLAG);
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(keyAlias, encodedSecretKey);
        editor.commit();
    }
    // end::generateAESKey[]

    // tag::generatePrivateKeyPair[]
    @Override
    public void generatePrivateKeyPair(String keyAlias) throws GeneralSecurityException, IOException {
        //pre android-M, the keystore only support RSA key generation. So here we will generate a RSA keypair first, then generate the AES key.
        //we then encrypt the AES key using the generated RSA public key, and save it using the SharedPreferences
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 99);
        KeyPairGeneratorSpec generatorSpec = new KeyPairGeneratorSpec
                .Builder(context)
                .setAlias(keyAlias)
                .setSubject(new X500Principal("CN=" + keyAlias))
                .setSerialNumber(BigInteger.TEN)
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();
        KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);
        generator.initialize(generatorSpec);
        generator.generateKeyPair();
    }
    // end::generatePrivateKeyPair[]

    @Override
    public boolean hasKeyPair(String keyAlias) throws GeneralSecurityException, IOException {
        KeyStore ks = loadKeyStore();
        return ks.containsAlias(keyAlias);
    }

    @Override
    public void deleteKey(String keyAlias) throws GeneralSecurityException, IOException {
        if (hasSecretKey(keyAlias)) {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.remove(keyAlias);
            editor.commit();
        } else if (hasKeyPair(keyAlias)) {
            KeyStore ks = loadKeyStore();
            ks.deleteEntry(keyAlias);
        }
    }

    // tag::generateSecretKey[]
    private byte[] generateSecretKey(int size) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES);
        keyGen.init(size);
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }
    // end::generateSecretKey[]

    private byte[] rsaEncrypt(byte[] keyToEncrypt) throws GeneralSecurityException, IOException {
        KeyStore keyStore = loadKeyStore();
        if (!keyStore.containsAlias(RSA_KEY_ALIAS)) {
            generatePrivateKeyPair(RSA_KEY_ALIAS);
        }
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(RSA_KEY_ALIAS, null);
        byte[] vals = RsaHelper.encrypt(getSupportedRSAMode(), privateKeyEntry, keyToEncrypt);
        return vals;
    }

    private byte[] rsaDecrypt(byte[] dataToDecrypt) throws GeneralSecurityException, IOException {
        KeyStore keyStore = loadKeyStore();
        if (!keyStore.containsAlias(RSA_KEY_ALIAS)) {
            throw new GeneralSecurityException("missing key pair with alias " + RSA_KEY_ALIAS);
        }
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(RSA_KEY_ALIAS, null);
        return RsaHelper.decrypt(getSupportedRSAMode(), privateKeyEntry, dataToDecrypt);
    }
}
