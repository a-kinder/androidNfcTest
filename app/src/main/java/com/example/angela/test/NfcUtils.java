package com.example.angela.test;

import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.crypto.SecretKey;

/**
 * Created by angela on 2016-06-17.
 */
public class NfcUtils {
    protected EncryptionHelper encryption = new EncryptionHelper();

    public NdefRecord createTextRecord(String payload, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);//copies array: original array, start position, dest array, end position, length
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, "application/vnd.angela.locations".getBytes(), new byte[0], data);
        return record;
    }

    public NdefRecord createTextRecord(byte[] payload, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + payload.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);//copies array: original array, start position, dest array, end position, length
        System.arraycopy(payload, 0, data, 1 + langBytes.length, payload.length);
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, "application/vnd.angela.locations".getBytes(), new byte[0], data);
        return record;
    }

    public boolean writeTag(Context context, Tag tag, ArrayList<String> data, String name) {
        try {
            NdefRecord[] records;
            int size = data.size();

            records = new NdefRecord[size];
            //}

            for (int i = 0; i < data.size(); i++) {
                byte[] text = encryption.encrypt(data.get(i));

                records[i] = this.createTextRecord(text, Locale.ENGLISH, true);

            }

            NdefMessage message = new NdefMessage(records);
            Ndef ndef = Ndef.get(tag);

            ndef.connect();
            ndef.writeNdefMessage(message);
            ndef.close();
            return true;
        } catch (UnsupportedEncodingException u) {
            Log.w("UnsupportedEncoding", "message:" + u.getMessage());
            return false;
        } catch (IOException i) {
            Log.w("IOException", "message:" + i.getMessage());
            System.out.println(i.toString());
            return false;
        } catch (FormatException f) {
            Log.w("FormatException", "message:" + f.getMessage());
            return false;
        } catch (Exception e) {
            Log.w("Unknown Exception", "message:" + e.getMessage());
            return false;
        }


    }

    public ArrayList<String> readTag(Tag tag, Intent intent) {
        ArrayList<String> data = new ArrayList<String>();
//

        if (tag != null) {

            Ndef ndef = Ndef.get(tag);

            try {

                ndef.connect();
                Parcelable[] msgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                NdefMessage msg = (NdefMessage) msgs[0];
                for (int i = 0; i < msg.getRecords().length; i++) {
                    NdefRecord record = msg.getRecords()[i];


                    /////
                    byte[] payload = record.getPayload();


                    //Get the Text Encoding
                    String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";//if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1): The text is encoded in UTF16

                    //Get the Language Code
                    int languageCodeLength = payload[0] & 0077;
                    String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

                    //Get the Text
                   // String text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
                    byte[] b= Arrays.copyOfRange(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1);


                    // data.add("text: " + encryption.d(text.getBytes()));

                    String decryptedText = encryption.decrypt(b);

                    data.add(decryptedText);

                }
                if (data.isEmpty()) {
                    data.add("no data");
                }

                ndef.close();


            } catch (Exception e) {
                Log.w("Exception", "message:" + e.getMessage());

                data = null;
            }
        }
        return data;
    }

}
