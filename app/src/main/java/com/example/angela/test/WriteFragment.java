package com.example.angela.test;

import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.angela.test.db.DbAccess;

import java.util.ArrayList;

public class WriteFragment extends Fragment {
    Button btnCheck;
    Button btnWrite;
    Button btnRead;
    View fragmentView;
    NfcUtils nfcUtils = new NfcUtils();
    MainActivity activity;
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Fragment2.
     */
    public static WriteFragment newInstance() {
        return new WriteFragment();
    }

    public WriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_write, container, false);
        activity = (MainActivity) getActivity();
        // Inflate the layout for this fragment
        final DbAccess dbAccess = new DbAccess(activity.getBaseContext());

        btnCheck = (Button) fragmentView.findViewById(R.id.btnCheck);//checks and inserts to database
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.uid == null) {
                    Log.e("Tag error", "Tag UID is null");
                    activity.showToast(false);
                } else {
                    if (dbAccess.getTag(activity.uid) == null) {//if tag is not in DB
                        activity.t = dbAccess.insertTag(activity.uid, activity.location);//insert it
                        dbAccess.addLocationTag(activity.location, activity.t);
                    }
                    activity.showToast(true);

                }
            }
        });


        btnRead = (Button) fragmentView.findViewById(R.id.btnRead);//read and decrypt
        btnRead.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           //if(nfcUtils.writeTag(getActivity(), activity.tag, activity.currData, activity.location.name))
                                           ArrayList<String> al = nfcUtils.readTag(activity.tag, activity.ntnt);
                                           if (al == null) {
                                               Log.e("Nfc read error", "Nfc read error");
                                               activity.showToast(false);
                                           } else {
                                               if (!al.isEmpty()) {
                                                   activity.createDialog(activity.getApplicationContext(), "Tag Contents", al.get(0), null).show();

                                               } else {
                                                   activity.createDialog(activity.getApplicationContext(), "Tag Contents", "Empty", null).show();
                                                   Log.e("Nfc read error", "No data on tag");

                                               }
                                           }
                                       }
                                   }

        );


        btnWrite = (Button) fragmentView.findViewById(R.id.btnWrite);//write and encrypt
        btnWrite.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View v) {
                                            ArrayList<String> al = new ArrayList<>();
                                            al.add("some data");

                                            if (nfcUtils.writeTag(activity.getApplicationContext(), activity.tag, al, activity.location.getName())) {
                                                activity.showToast(true);
                                            } else {
                                                activity.showToast(false);
                                                Log.e("Nfc write error", "Chip write failed");
                                            }
                                        }
                                    }

        );
        return fragmentView;


    }

}
