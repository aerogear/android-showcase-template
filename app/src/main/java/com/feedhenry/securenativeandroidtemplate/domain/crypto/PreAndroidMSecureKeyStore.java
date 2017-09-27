package com.feedhenry.securenativeandroidtemplate.domain.crypto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.util.Base64;

import com.feedhenry.securenativeandroidtemplate.domain.utils.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
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
    private static final String RSA_MODE = "RSA/ECB/PKCS1Padding";
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
    public boolean hasKeyAlias(String keyAlias) throws GeneralSecurityException, IOException {
       return this.sharedPreferences.contains(keyAlias);
    }

    @Override
    public Key getKey(String keyAlias) throws GeneralSecurityException, IOException {
        String encodedKey = this.sharedPreferences.getString(keyAlias, null);
        if (encodedKey != null) {
            byte[] encryptedKeyBytes = Base64.decode(encodedKey, BASE64_FLAG);
            byte[] keyBytes = rsaDecrypt(encryptedKeyBytes);
            return new SecretKeySpec(keyBytes, KeyProperties.KEY_ALGORITHM_AES);
        }
        return null;
    }

    @Override
    public void generateAESKey(String keyAlias) throws GeneralSecurityException, IOException {
        byte[] secretKey = generateSecretKey(AES_KEYSIZE_128);
        byte[] encryptedKey = rsaEncrypt(secretKey);
        String encodedSecretKey = Base64.encodeToString(encryptedKey, BASE64_FLAG);
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(keyAlias, encodedSecretKey);
        editor.commit();
    }

    @Override
    public void deleteKey(String keyAlias) throws GeneralSecurityException, IOException {
        if (hasKeyAlias(keyAlias)) {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.remove(keyAlias);
            editor.commit();
        }
    }

    private byte[] generateSecretKey(int size) {
        byte[] secretKey = new byte[size/BIT_PER_BYTE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(secretKey);
        return secretKey;
    }

    private void generateRSAKeyForAndroidM() throws GeneralSecurityException, IOException {
        //pre android-M, the keystore only support RSA key generation. So here we will generate a RSA keypair first, then generate the AES key.
        //we then encrypt the AES key using the generated RSA public key, and save it using the SharedPreferences
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 99);
        KeyPairGeneratorSpec generatorSpec = new KeyPairGeneratorSpec
                .Builder(context)
                .setAlias(RSA_KEY_ALIAS)
                .setSubject(new X500Principal("CN=" + RSA_KEY_ALIAS))
                .setSerialNumber(BigInteger.TEN)
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();
        KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);
        generator.initialize(generatorSpec);
        generator.generateKeyPair();

    }

    private byte[] rsaEncrypt(byte[] keyToEncrypt) throws GeneralSecurityException, IOException {
        KeyStore keyStore = loadKeyStore();
        if (!keyStore.containsAlias(RSA_KEY_ALIAS)) {
            generateRSAKeyForAndroidM();
        }
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(RSA_KEY_ALIAS, null);
        // Encrypt the text
        Cipher inputCipher = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL");
        inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());

        //The key to encrypt should be either 16 (128 bit) or 32 (256 bit) in size, well below the block size for RSA (should be around 214 bytes)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inputCipher);
        cipherOutputStream.write(keyToEncrypt);
        cipherOutputStream.close();

        byte[] vals = outputStream.toByteArray();
        return vals;
    }

    private byte[] rsaDecrypt(byte[] dataToDecrypt) throws GeneralSecurityException, IOException {
        KeyStore keyStore = loadKeyStore();
        if (!keyStore.containsAlias(RSA_KEY_ALIAS)) {
            throw new GeneralSecurityException("missing key pair with alias " + RSA_KEY_ALIAS);
        }
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(RSA_KEY_ALIAS, null);
        Cipher output = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL");
        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
        ByteArrayInputStream inputStream = new ByteArrayInputStream(dataToDecrypt);
        CipherInputStream cipherInputStream = new CipherInputStream(inputStream, output);
        return StreamUtils.readStreamBytes(cipherInputStream);
    }
}
