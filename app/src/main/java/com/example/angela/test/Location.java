package com.example.angela.test;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by angela on 2016-06-16.
 */
public class Location  {
    public String name;
    public int id;

    public Location(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {//this is what shows in the listview
        return this.name;
    }


}

