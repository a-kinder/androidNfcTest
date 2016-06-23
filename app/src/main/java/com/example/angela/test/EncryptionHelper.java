package com.example.angela.test;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by angela on 2016-06-23.
 */
public class EncryptionHelper {
    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public void invoke(String data) {
        try {
            byte[] b = data.getBytes();

            byte[] keyStart = "this is a key".getBytes();//is this a salt?
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(keyStart);
            kgen.init(128, sr); // 192 and 256 bits may not be available
            SecretKey skey = kgen.generateKey();
            byte[] key = skey.getEncoded();

            // encrypt
            byte[] encryptedData = encrypt(key, b);//raw, clear
            // decrypt
            byte[] decryptedData = decrypt(key, encryptedData);//raw, encrypted
        } catch (NoSuchAlgorithmException a) {
            System.out.println("No Such Algorithm Exception");
            System.out.println(a.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

}
