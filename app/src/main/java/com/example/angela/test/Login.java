package com.example.angela.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.List;

public class Login extends Activity {
    ListView listview;
    Location[] locations = {
            new Location("Front Door", 1),
            new Location("VIP", 2)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
private class Location
{
    public String name;
    public int id;

    public Location(String name, int id)
    {
        this.name = name;
        this.id = id;
    }
    @Override
    public String toString() {//this is what shows in the listview
        return this.name;
    }
}
}

