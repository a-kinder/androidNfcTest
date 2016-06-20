package com.example.angela.test;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;
public class LocationFragment extends Fragment {
    ArrayList<Location> locations = new ArrayList<>();
    ListView listview;
    public static LocationFragment newInstance() {
        return new LocationFragment();
    }

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        locations.add(new Location("VIP", 0));
        locations.add(new Location("Front Gate", 1));

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_location, container, false);

        listview = (ListView)fragmentView.findViewById(R.id.listView);
        ArrayAdapter<Location> myAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,  locations);
        listview.setAdapter(myAdapter);




        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Location itemValue = (Location) listview.getItemAtPosition(position);
                getActivity().setTitle(itemValue.name);

                MainActivity activity = (MainActivity) getActivity();
                Editor editor = activity.sharedpreferences.edit();
                editor.putString(activity.NameKey, itemValue.name);
                editor.putInt(activity.IdKey, itemValue.id);
                editor.apply();
            }
        });

        return fragmentView;

    }
}