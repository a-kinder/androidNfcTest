package com.example.angela.test;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.nfc.*;
import android.nfc.tech.*;
import android.os.*;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.*;
import android.widget.*;
import android.support.v4.widget.*;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> tabTitles = new ArrayList<>();
    MyPagerAdapter pagerAdapter;
    TabLayout tabLayout;
    public static final String TAG = "NfcDemo";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private NfcAdapter myNfcAdapter;
    private AlertDialog dialog;
    private PendingIntent pendingIntent;
    private Intent ntnt;
    private Tag tag;
    private NfcUtils nfcUtil = new NfcUtils();
    ArrayList<Location> locations;
    String name;
    Integer id;
    String uid;
    ArrayList<String> currData = new ArrayList<String>();
    private final String[][] techList = new String[][]{
            new String[]{
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };
    public static final String MyPREFERENCES = "MyPrefs";
     SharedPreferences sharedpreferences;
    public static final String NameKey = "nameKey";
    public static final String IdKey = "idKey";

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);

//        pendingIntent = PendingIntent.getActivity(
//                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
//        name = this.getIntent().getStringExtra("locName");
//        id = this.getIntent().getIntExtra("locId", 0);

        fragmentList.add(LocationFragment.newInstance());
        fragmentList.add(MainFragment.newInstance());
        fragmentList.add(WriteFragment.newInstance());

        tabTitles.add("Locations");
        tabTitles.add("Scan");
        tabTitles.add("Write");

        // Set a toolbar which will replace the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup the viewPager
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // Setup the Tabs
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        // This method ensures that tab selection events update the ViewPager and page changes update the selected tab.
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(1);


        setTitle(sharedpreferences.getString(NameKey, "No Location Selected"));


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
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            // creating intent receiver for NFC events:
            IntentFilter filter = new IntentFilter();
            filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
            filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
            filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
            // enabling foreground dispatch for getting intent from NFC event:
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);


            //  myNfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);


        } else {
            dialog = createDialog(getApplicationContext(), R.string.nfcNotAvailableTitle, R.string.nfcNotAvailable, null);
            dialog.show();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        viewPager.setCurrentItem(1);

        uid = this.ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        ntnt = intent;
        checkCreds();
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

    public void checkCreds() {
        if (tag != null) {

            currData.addAll(nfcUtil.readTag(tag, ntnt));
            if (currData.contains(name)) {
                showToast(true);
            } else {
                showToast(false);
            }

        } else {
            showToast(false);
        }
        tag = null;
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

    public void showToast(boolean success) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        if (success) {
            layout.setBackgroundColor(Color.GREEN);
        } else {
            layout.setBackgroundColor(Color.RED);
        }
        layout.setAlpha(.7f);

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

    private String ByteArrayToHexString(byte[] inarray) {
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


    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            return fragmentList.get(pos);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        // This is called when notifyDataSetChanged() is called. Without this, getItem() is not triggered
        @Override
        public int getItemPosition(Object object) {
            // refresh all fragments when data set changed
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);
        }


    }
}

