package com.example.angela.test;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by angela on 2016-06-16.
 */
public class Location {
    public String name;
    public int id;

    public Location() {
    }
    public Location(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public Location(String name) {
        this.name = name;
    }

    @Override
    public String toString() {//this is what shows in the listview
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}

