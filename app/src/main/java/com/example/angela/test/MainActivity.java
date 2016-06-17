package com.example.angela.test;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.nfc.*;
import android.nfc.tech.*;
import android.os.*;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.ActionBar.LayoutParams;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public class MainActivity extends Activity {
    public static final String TAG = "NfcDemo";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private NfcAdapter myNfcAdapter;
    Button button;
    ImageButton imgBtn;
    TextView textView;
    Button btnBack;
    private AlertDialog dialog;
    private PendingIntent pendingIntent;
    private Intent ntnt;
    private Tag tag;
    ArrayList<Location> locations;
    String name;
    Integer id;
    ArrayList<String> currData = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initUI();
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown. 
         */
        if (checkNfcAdapter()) {
            // setupForegroundDispatch(this, myNfcAdapter);
            myNfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);

        } else {
            dialog = createDialog(getApplicationContext(), R.string.nfcNotAvailableTitle, R.string.nfcNotAvailable, null);
            dialog.show();
        }
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        //stopForegroundDispatch(this, myNfcAdapter);
        if (checkNfcAdapter()) {
            // setupForegroundDispatch(this, myNfcAdapter);
            myNfcAdapter.disableForegroundDispatch(this);

        }
        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        ntnt = intent;
        // handleIntent(intent);
    }

    public boolean writeTag(Context context, Tag tag, ArrayList<String> data) {
        try {
            NdefRecord[] records;
//            if (!currData.isEmpty()) {
//                int size = currData.size() + 1;
//                records = new NdefRecord[size];
//
//                for (int i = 1; i < size + 1; i++) {
//                    records[i] = createTextRecord(data.get(i - 1), Locale.ENGLISH, true);
//                }
//            } else {
                records = new NdefRecord[1];
           // }

            records[0] = createTextRecord(name.toString(), Locale.ENGLISH, true);

            NdefMessage message = new NdefMessage(records);
            Ndef ndef = Ndef.get(tag);

            ndef.connect();
            ndef.writeNdefMessage(message);
            ndef.close();
            return true;
        } catch (UnsupportedEncodingException u) {
            Log.w("UnsupportedEncoding", u.getMessage());
        } catch (IOException i) {
            Log.w("IOException", i.getMessage());
        } catch (FormatException f) {
            Log.w("FormatException", f.getMessage());
        } catch (Exception e) {
            Log.w("Unknown Exception", e.getMessage());
        }

        return false;
    }

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
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
        return record;
    }


    public ArrayList<String> readTag() {
        ArrayList<String> data = new ArrayList<String>();


        if (tag != null) {

            Ndef ndef = Ndef.get(tag);

            try {

                ndef.connect();
                Parcelable[] msgs = ntnt.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                NdefMessage msg = (NdefMessage) msgs[0];
                for (int i = 0; i < msg.getRecords().length; i++) {
                    NdefRecord record = msg.getRecords()[i];


                    /////
                    byte[] payload = record.getPayload();

        /*
     * payload[0] contains the "Status Byte Encodings" field, per the
     * NFC Forum "Text Record Type Definition" section 3.2.1.
     *
     * bit7 is the Text Encoding Field.
     *
     *
     *
     * Bit_6 is reserved for future use and must be set to zero.
     *
     * Bits 5 to 0 are the length of the IANA language code.
     */

                    //Get the Text Encoding
                    String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";//if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1): The text is encoded in UTF16

                    //Get the Language Code
                    int languageCodeLength = payload[0] & 0077;
                    String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

                    //Get the Text
                    String text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);


                    data.add(text);

                }
                if (data.isEmpty()) {
                    data.add("no data");
                }

                ndef.close();


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Cannot Read From Tag.", Toast.LENGTH_LONG).show();
            }
        }
        currData.addAll(data);
        return data;
    }

    public AlertDialog createDialog(Context context, int title, int msg, Runnable block) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        final Runnable b = block;
        builder.setTitle(getString(title));
        builder.setMessage(getString(msg));

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (b != null) {
                    b.run();
                }
            }
        });

        return builder.create();
    }

    public AlertDialog createDialog(Context context, String title, String msg, Runnable block) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        final Runnable b = block;
        builder.setTitle(title);
        builder.setMessage(msg);

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (b != null) {
                    b.run();
                }
            }
        });

        return builder.create();
    }

    public AlertDialog createDialog(Context context, String title, ArrayList<String> msg, Runnable block) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        final Runnable b = block;
        builder.setTitle(title);
        String message = "";
        for (int i = 0; i < msg.size(); i++) {
            message += msg.get(i) + "\n";
        }
        builder.setMessage(message);

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (b != null) {
                    b.run();
                }
            }
        });

        return builder.create();
    }

    public void showToast(boolean success) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        if (success) {
            layout.setBackgroundColor(Color.GREEN);
        } else {
            layout.setBackgroundColor(Color.RED);
        }
        layout.setAlpha(.5f);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setTextSize(50);
        if (success) {
            text.setText(R.string.success);
        } else {
            text.setText(R.string.fail);
        }

        final Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

        Handler handler = new Handler();//custom toast length
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 100);
    }

    void playNoise(int sound) {

        final MediaPlayer mp = MediaPlayer.create(this, sound);
        mp.start();
    }

    public boolean checkNfcAdapter() {
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);


        if (myNfcAdapter == null) {//no nfc available


            return false;
        } else if (!myNfcAdapter.isEnabled()) {//nfc not enabled
            Runnable block = new Runnable() {
                @Override
                public void run() {
                    final Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(intent);
                }
            };
            dialog = createDialog(getApplicationContext(), R.string.nfcNotEnabledTitle, R.string.nfcNotEnabled, block);
            dialog.show();
        }
        return true;
    }

    public void initUI() {

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        name = this.getIntent().getStringExtra("locName");
        id = this.getIntent().getIntExtra("locId", 0);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText(name);


        //editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//
//                    if (writeTag(getApplicationContext(), tag, "test data")) {
//                       showToast(true
//                    }
                    //showToast(false);
                    //playNoise(R.raw.fail);if9

                    if (tag != null && writeTag(getApplicationContext(), tag, currData)) {
                        showToast(true);
                    } else {
                        showToast(false);
                    }

                }
            });
        }

        imgBtn = (ImageButton) findViewById(R.id.imageButton);
        if (imgBtn != null) {
            imgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showToast(true);
                    //playNoise(R.raw.success);
                    if (tag != null) {
                        Runnable block = new Runnable() {
                            @Override
                            public void run() {
                                tag = null;
                            }
                        };
                        createDialog(getApplicationContext(), "Tag Contents", readTag(), block).show();
                    } else {
                        showToast(false);
                    }

                }
            });
        }
        btnBack = (Button)findViewById(R.id.btnBack);
        if(btnBack != null)
        {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


    }

}
