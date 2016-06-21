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
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.*;

        import java.util.ArrayList;

public class WriteFragment extends Fragment {
Button btnWrite;
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

        btnWrite = (Button)fragmentView.findViewById(R.id.btnWrite);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nfcUtils.writeTag(getActivity(), activity.tag, activity.currData, activity.location.name))
                {
                    activity.showToast(true);
                } else {activity.showToast(false);}
            }
        });
        return fragmentView;



    }
}
