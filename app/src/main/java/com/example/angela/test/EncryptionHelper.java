package com.example.angela.test;

import android.util.Log;

import javax.crypto.SecretKey;

import android.util.Base64;

import org.cryptonode.jncryptor.AES256JNCryptor;
import org.cryptonode.jncryptor.CryptorException;
import org.cryptonode.jncryptor.JNCryptor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
//import javax.xml.bind.DatatypeConverter;

/**
 * Created by angela on 2016-06-23.
 */
public class EncryptionHelper {

public byte[] encrypt(String text)
{
    JNCryptor cryptor = new AES256JNCryptor();
    byte[] plaintext = text.getBytes();
    String password = "secretsquirrel";

    try {
        byte[] ciphertext = cryptor.encryptData(plaintext, password.toCharArray());
        return ciphertext;
    } catch (CryptorException e) {
        // Something went wrong
        e.printStackTrace();
    }
    return null;
}

    public String decrypt(byte[] plaintext)
    {
        JNCryptor cryptor = new AES256JNCryptor();
        //byte[] plaintext = hexToBytes(hex);
        String password = "secretsquirrel";

        try {
            byte[] ciphertext = cryptor.decryptData(plaintext, password.toCharArray());
            return new String(ciphertext);
        } catch (CryptorException e) {
            // Something went wrong
            e.printStackTrace();
        }
        return null;
    }
    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public String bytesToHex(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";
        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }


}
