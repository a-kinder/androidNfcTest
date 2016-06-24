package com.example.angela.test;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.concurrent.RunnableFuture;

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
    ArrayList<Location> locations;// = new ArrayList<Location>(Arrays.asList(new Location("VIP", 0), new Location("Front Gate", 1)));


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

        DbAccess dbAccess = new DbAccess(this.getActivity().getBaseContext());

        locations = dbAccess.getAllLocations();

        myAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, locations);
        fragmentView = inflater.inflate(R.layout.fragment_location, container, false);
        listview = (ListView) fragmentView.findViewById(R.id.listView);
        listview.setAdapter(myAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final Location location =(Location) listview.getItemAtPosition(position);
                Runnable block = new Runnable() {
                    @Override
                    public void run() {
                        activity.location = location;
                        getActivity().setTitle(activity.location.name);


                        Editor editor = activity.sharedpreferences.edit();
                        editor.putString(activity.NameKey, activity.location.name);
                        editor.putInt(activity.IdKey, activity.location.id);
                        editor.apply();
                    }
                };
                activity.createDialog(activity, "Changing Location", location.name.toUpperCase(), block).show();


            }
        });

        return fragmentView;

    }
}