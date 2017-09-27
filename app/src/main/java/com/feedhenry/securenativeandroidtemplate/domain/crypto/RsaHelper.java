package com.feedhenry.securenativeandroidtemplate.domain.crypto;

import com.feedhenry.securenativeandroidtemplate.domain.utils.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

/**
 * Created by weili on 27/09/2017.
 */

public class RsaHelper {
    public static byte[] encrypt(String mode, KeyStore.PrivateKeyEntry keyEntry, byte[] text) throws GeneralSecurityException, IOException {
        // Encrypt the text
        Cipher inputCipher = Cipher.getInstance(mode);
        inputCipher.init(Cipher.ENCRYPT_MODE, keyEntry.getCertificate().getPublicKey());
        //The key to encrypt should be either 16 (128 bit) or 32 (256 bit) in size, well below the block size for RSA (should be around 214 bytes)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inputCipher);
        cipherOutputStream.write(text);
        cipherOutputStream.close();

        byte[] vals = outputStream.toByteArray();
        return vals;
    }

    public static byte[] decrypt(String mode, KeyStore.PrivateKeyEntry keyEntry, byte[] toDecrypt) throws GeneralSecurityException, IOException {
        Cipher output = Cipher.getInstance(mode);
        output.init(Cipher.DECRYPT_MODE, keyEntry.getPrivateKey());
        ByteArrayInputStream inputStream = new ByteArrayInputStream(toDecrypt);
        CipherInputStream cipherInputStream = new CipherInputStream(inputStream, output);
        return StreamUtils.readStreamBytes(cipherInputStream);
    }
}
