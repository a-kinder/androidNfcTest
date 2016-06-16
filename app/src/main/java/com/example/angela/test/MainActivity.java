package com.example.angela.test;

import android.app.*;
import android.content.*;
import android.content.IntentFilter.*;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.nfc.*;
import android.nfc.tech.*;
import android.os.*;
//import android.os.Bundle;
//import android.os.Handler;//
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View.*;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.io.IOException.*;
import java.nio.charset.*;
import java.util.Arrays;

import javax.crypto.*;


public class MainActivity extends Activity {
    public static final String TAG = "NfcDemo";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private NfcAdapter myNfcAdapter;
    private Button button;
    private ImageButton imgBtn;
    private EditText editText;
    private TextView textView;
    private AlertDialog dialog;
    private PendingIntent pendingIntent;
    private Intent ntnt;
    private Tag tag;
    private String name;
    private Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        name = this.getIntent().getStringExtra("locName");
        id = this.getIntent().getIntExtra("locId", 0);
        textView = (TextView)findViewById(R.id.textView);
        textView.setText(name);
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

    public boolean writeTag(Context context, Tag tag, String data) {
        try {
            NdefRecord[] records = {createRecord(data), createRecord("record two"), createRecord("record three")};///create multiple records here
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

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {

        //create the message in according with the standard
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;

        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
    }

    public String readTag() {
        String data = "No data";
        if (tag != null) {

            Ndef ndef = Ndef.get(tag);


            try {
                ndef.connect();
                Parcelable[] messages = ntnt.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                if (messages != null) {
                    NdefMessage[] ndefMessages = new NdefMessage[messages.length];
                    for (int i = 0; i < messages.length; i++) {
                        ndefMessages[i] = (NdefMessage) messages[i];


                    }
                    NdefRecord record = ndefMessages[0].getRecords()[0];
                    byte[] payload = record.getPayload();
                    data = new String(payload);


                    ndef.close();

                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Cannot Read From Tag.", Toast.LENGTH_LONG).show();
            }
        }
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
        editText = (EditText) findViewById(R.id.editText);
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

                    if (tag != null && writeTag(getApplicationContext(), tag, editText.getText().toString())) {
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
    }

}
