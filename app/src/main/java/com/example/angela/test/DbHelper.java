package com.example.angela.test;

/**
 * Created by angela on 2016-06-21.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.*;
import android.content.Context;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

public class DbHelper extends SQLiteOpenHelper {
    static final String tagTableName = "tags";
    static final String tagId = "id";
    static final String tagUid = "uid";
    static final String tagName = "tags";
    static final String locTableName = "locations";
    static final String locId = "id";
    static final String locName = "name";


    public static final String DATABASE_NAME = "NfcDb.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE =
            "CREATE TABLE " + tagTableName + " (" +
                    tagId + " INTEGER PRIMARY KEY , " +
                    tagUid + " INTEGER," +
                    tagName + " TEXT);" +
                    "CREATE TABLE " + locTableName + " (" +
                    locId + " INTEGER PRIMARY KEY , " +
                    locName + " TEXT);" +
                    "CREATE TABLE " + locTableName + tagTableName + " (" +
                    "locId INTEGER REFERENCES locations(id), " +
                    "tagId INTEGER REFERENCES locations(id));";
    private SQLiteDatabase db;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        //Inserts pre-defined departments
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tagTableName);
        db.execSQL("DROP TABLE IF EXISTS " + locTableName);
        db.execSQL("DROP TABLE IF EXISTS " + locTableName + tagTableName);
        onCreate(db);

    }

    public void insertLocation(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(locName, location.getName()); // Shop Name
        values.put(locId, location.getId()); // Shop Phone Number
// Inserting Row
        db.insert(locTableName, null, values);
        db.close(); // Closing database connection
    }

    public Location getLocation(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(locTableName, new String[]{locId,
                        locName}, locId + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Location contact = new Location(cursor.getString(1), (Integer.parseInt(cursor.getString(0))));
// return shop
        return contact;
    }
    public ArrayList<Location> getAllLocations()
    {
        ArrayList<Location> list = new ArrayList<Location>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + locTableName;

        SQLiteDatabase db = this.getReadableDatabase();
        try {

            Cursor cursor = db.rawQuery(selectQuery, null);
            try {

                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        Location obj = new Location();
                        obj.setId(cursor.getInt(0));
                        obj.setName(cursor.getString(1));
                        list.add(obj);
                    } while (cursor.moveToNext());
                }

            } finally {
                try { cursor.close(); } catch (Exception ignore) {}
            }

        } finally {
            try { db.close(); } catch (Exception ignore) {}
        }

        return list;
    }
}