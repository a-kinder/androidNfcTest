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
import java.util.Arrays;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;

public class LocationFragment extends Fragment {
    ListView listview;

    public static LocationFragment newInstance() {
        return new LocationFragment();
    }

    View fragmentView;
    ArrayAdapter<Location> myAdapter;
    MainActivity activity;
    ArrayList<Location> locations = new ArrayList<Location>(Arrays.asList(new Location("VIP", 0), new Location("Front Gate", 1)));


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

        activity = (MainActivity) getActivity();
        myAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, locations);

        fragmentView = inflater.inflate(R.layout.fragment_location, container, false);

        // Inflate the layout for this fragment
        listview = (ListView) fragmentView.findViewById(R.id.listView);
        listview.setAdapter(myAdapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Location itemValue = (Location) listview.getItemAtPosition(position);
                getActivity().setTitle(itemValue.name);


                   // activity.sharedpreferences = activity.getSharedPreferences(activity.MyPREFERENCES, Context.MODE_PRIVATE);

                Editor editor = activity.sharedpreferences.edit();
                editor.putString(activity.NameKey, itemValue.name);
                editor.putInt(activity.IdKey, itemValue.id);
                editor.apply();


            }
        });

        return fragmentView;

    }
}