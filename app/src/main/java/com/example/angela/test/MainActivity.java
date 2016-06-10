package com.example.angela.test;

import android.app.*;
import android.content.*;
import android.content.IntentFilter.*;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.nfc.*;
import android.nfc.tech.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;//
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View.*;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.nio.charset.*;
import java.util.Arrays;

import javax.crypto.*;


public class MainActivity extends Activity {
    public static final String TAG = "NfcDemo";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private NfcAdapter myNfcAdapter;
    Button button;
    ImageButton imgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        handleIntent(getIntent());

    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown. 
         */
        setupForegroundDispatch(this, myNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, myNfcAdapter);

        super.onPause();
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
        handleIntent(intent);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {

        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }


    private void handleIntent(Intent intent) {
        if (checkNfcAdapter()) {
            String action = intent.getAction();
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

                String type = intent.getType();
                if (MIME_TEXT_PLAIN.equals(type)) {

                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    new NdefReaderTask().execute(tag);

                } else {
                    Log.d(TAG, "Wrong mime type: " + type);
                }
            } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

                // In case we would still use the Tech Discovered Intent
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String[] techList = tag.getTechList();
                String searchedTech = Ndef.class.getName();

                for (String tech : techList) {
                    if (searchedTech.equals(tech)) {
                        new NdefReaderTask().execute(tag);
                        break;
                    }
                }
            }
        }
    }

    public boolean writeTag(Context context, Tag tag, String data) {
        // Record to launch Play Store if app is not installed
        NdefRecord appRecord = NdefRecord.createApplicationRecord(context.getPackageName());

        // Record with actual data we care about
        NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                ("application/" + context.getPackageName())
                        .getBytes(Charset.forName("US-ASCII")),
                null, data.getBytes());

        // Complete NDEF message with both records
        NdefMessage message = new NdefMessage(new NdefRecord[]{relayRecord, appRecord});

        try {
            // If the tag is already formatted, just write the message to it
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                // Make sure the tag is writable
                if (!ndef.isWritable()) {
                    createDialog(context, R.string.nfcReadOnlyErrorTitle, R.string.nfcReadOnlyError, null);
                    return false;
                }

                // Check if there's enough space on the tag for the message
                int size = message.toByteArray().length;
                if (ndef.getMaxSize() < size) {
                    createDialog(context, R.string.nfcBadSpaceErrorTitle, R.string.nfcBadSpaceError, null);
                    return false;
                }

                try {
                    // Write the data to the tag
                    ndef.writeNdefMessage(message);

                    createDialog(context, R.string.nfcWrittenTitle, R.string.nfcWritten, null);//INFO DIALOG
                    return true;
                } catch (TagLostException tle) {
                    createDialog(context, R.string.nfcTagLostErrorTitle, R.string.nfcTagLostError, null);
                    return false;
                } catch (IOException ioe) {
                    createDialog(context, R.string.nfcFormattingErrorTitle, R.string.nfcFormattingError, null);
                    return false;
                } catch (FormatException fe) {
                    createDialog(context, R.string.nfcFormattingErrorTitle, R.string.nfcFormattingError, null);
                    return false;
                }
                // If the tag is not formatted, format it with the message
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        createDialog(context, R.string.nfcWrittenTitle, R.string.nfcWritten, null);//INFO DIALOG
                        return true;
                    } catch (TagLostException tle) {
                        createDialog(context, R.string.nfcTagLostErrorTitle, R.string.nfcTagLostError, null);
                        return false;
                    } catch (IOException ioe) {
                        createDialog(context, R.string.nfcFormattingErrorTitle, R.string.nfcFormattingError, null);
                        return false;
                    } catch (FormatException fe) {
                        createDialog(context, R.string.nfcFormattingErrorTitle, R.string.nfcFormattingError, null);
                        return false;
                    }
                } else {
                    createDialog(context, R.string.nfcNoNdefErrorTitle, R.string.nfcNoNdefError, null);
                    return false;
                }
            }
        } catch (Exception e) {
            createDialog(context, R.string.nfcUnknownErrorTitle, R.string.nfcUnknownError, null);
        }

        return false;
    }

    public String readTag() {
        String data = "";
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
            text.setText("SUCCESS");
        } else {
            text.setText("FAIL");
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
            createDialog(getApplicationContext(), R.string.nfcNotAvailableTitle, R.string.nfcNotAvailable, null).show();
            return false;
        } else if (!myNfcAdapter.isEnabled()) {//nfc not enabled
            Runnable block = new Runnable() {
                @Override
                public void run() {
                    final Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(intent);
                }
            };
            createDialog(getApplicationContext(), R.string.nfcNotEnabledTitle, R.string.nfcNotEnabled, block).show();
        }
        return true;
    }

    public void initUI() {
        button = (Button) findViewById(R.id.button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//
//                    if (writeTag(getApplicationContext(), tag, "test data")) {
//                       showToast(true
//                    }
                    showToast(false);
                    //playNoise(R.raw.fail);

                }
            });
        }

        imgBtn = (ImageButton) findViewById(R.id.imageButton);
        if (imgBtn != null) {
            imgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showToast(true);
                    //playNoise(R.raw.success);
                }
            });
        }
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
               // mTextView.setText("Read content: " + result);
                createDialog(getApplicationContext(), "NFC Data", result, null);
            }
        }
    }
}
