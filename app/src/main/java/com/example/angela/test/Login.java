package com.example.angela.test;

import android.app.Activity;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Login extends Activity {
    ListView listview;
    ArrayList<Location> locations = new ArrayList<Location>();


//    Location[] locations = {
//            new Location("Front Door", 1),
//            new Location("VIP", 2)
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        locations.add(new Location("VIP", 0));
        locations.add(new Location("Front Gate", 1));
        listview = (ListView)
                findViewById(R.id.listView);

        ArrayAdapter<Location> myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locations);

        listview.setAdapter(myAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Location itemValue = (Location) listview.getItemAtPosition(position);

                Intent intent = new Intent(Login.this, MainActivity.class);
                intent.putExtra("locName", itemValue.name);
                intent.putExtra("locId", itemValue.id);
                startActivity(intent);
            }
        });


    }

}

