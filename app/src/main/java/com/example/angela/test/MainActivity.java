package com.example.angela.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.view.*;

import java.io.*;

import javax.crypto.*;
import android.app.*;
import java.nio.charset.*;
import android.support.v7.app.AlertDialog;
import android.content.*;
import android.nfc.*;
import android.nfc.tech.*;

import java.io.Console;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//
//                    if (writeTag(getApplicationContext(), tag, "test data")) {
//                        Toast toast = Toast.makeText(getApplicationContext(), "Tag written", Toast.LENGTH_LONG);
//                        toast.show();
//                    }


                }
            });
        }

        ImageButton imgBtn = (ImageButton) findViewById(R.id.imageButton);
        if (imgBtn != null) {
            imgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog d = onCreateDialog(getApplicationContext(),"test","test");
                    d.show();
              }
            });
        }

    }

    public boolean writeTag(Context context, Tag tag, String data) {
        // Record to launch Play Store if app is not installed
        NdefRecord appRecord = NdefRecord.createApplicationRecord(context.getPackageName());

        // Record with actual data we care about
        NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                new String("application/" + context.getPackageName())
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
                    displayErrorDialog(context, R.string.nfcReadOnlyErrorTitle, R.string.nfcReadOnlyError);
                    return false;
                }

                // Check if there's enough space on the tag for the message
                int size = message.toByteArray().length;
                if (ndef.getMaxSize() < size) {
                    displayErrorDialog(context, R.string.nfcBadSpaceErrorTitle, R.string.nfcBadSpaceError);
                    return false;
                }

                try {
                    // Write the data to the tag
                    ndef.writeNdefMessage(message);

                    displayErrorDialog(context, R.string.nfcWrittenTitle, R.string.nfcWritten);//INFO DIALOG
                    return true;
                } catch (TagLostException tle) {
                    displayErrorDialog(context, R.string.nfcTagLostErrorTitle, R.string.nfcTagLostError);
                    return false;
                } catch (IOException ioe) {
                    displayErrorDialog(context, R.string.nfcFormattingErrorTitle, R.string.nfcFormattingError);
                    return false;
                } catch (FormatException fe) {
                    displayErrorDialog(context, R.string.nfcFormattingErrorTitle, R.string.nfcFormattingError);
                    return false;
                }
                // If the tag is not formatted, format it with the message
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        displayErrorDialog(context, R.string.nfcWrittenTitle, R.string.nfcWritten);//INFO DIALOG
                        return true;
                    } catch (TagLostException tle) {
                        displayErrorDialog(context, R.string.nfcTagLostErrorTitle, R.string.nfcTagLostError);
                        return false;
                    } catch (IOException ioe) {
                        displayErrorDialog(context, R.string.nfcFormattingErrorTitle, R.string.nfcFormattingError);
                        return false;
                    } catch (FormatException fe) {
                        displayErrorDialog(context, R.string.nfcFormattingErrorTitle, R.string.nfcFormattingError);
                        return false;
                    }
                } else {
                    displayErrorDialog(context, R.string.nfcNoNdefErrorTitle, R.string.nfcNoNdefError);
                    return false;
                }
            }
        } catch (Exception e) {
            displayErrorDialog(context, R.string.nfcUnknownErrorTitle, R.string.nfcUnknownError);
        }

        return false;
    }

    public void displayErrorDialog(Context context, int title, int error) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(getResources().getString(title));
        alertDialogBuilder.setMessage(getResources().getString(error));
        alertDialogBuilder.show();
    }

    public AlertDialog onCreateDialog(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));

        builder.setTitle(title);
        builder.setMessage(msg);

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });


        return builder.create();


    }
}

