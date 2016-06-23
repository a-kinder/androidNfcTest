package com.example.angela.test;

/**
 * Created by angela on 2016-06-21.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.*;
import android.content.Context;

import java.io.Console;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

public class DbAccess {
    static final String tagTableName = "tags";
    static final String tagId = "id";
    static final String tagUid = "uid";
    static final String tagName = "tags";
    static final String locTableName = "locations";
    static final String locId = "id";
    static final String locName = "name";

    public static final String DATABASE_NAME = "NfcDb.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TAG_TABLE =
            "CREATE TABLE " + tagTableName + " (" +
                    tagId + " INTEGER PRIMARY KEY , " +
                    tagUid + " TEXT," +
                    tagName + " TEXT);";
    private static final String CREATE_LOCATION_TABLE =
            "CREATE TABLE " + locTableName + " (" +
                    locId + " INTEGER PRIMARY KEY , " +
                    locName + " TEXT);";
    private static final String CREATE_LOCATIONTAG_TABLE =
            "CREATE TABLE " + locTableName + tagTableName + " (" +
                    "locId INTEGER REFERENCES locations(id), " +
                    "tagId INTEGER REFERENCES locations(id));";
    private SQLiteDatabase db;
    DbHelper dbHelper;

    public DbAccess(Context context) {
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();

    }

    public boolean insertLocation(Location location) {
        try {
            //  SQLiteatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(locName, location.getName());
            values.put(locId, location.getId());
            db.insert(locTableName, null, values);

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean insertTag(String uid) {
        try {
            if (getTag(uid) == null) {
                //  db = this.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(tagUid, uid);
                db.insert(tagTableName, null, values);
                //  db.close(); // Closing database connection
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            return false;
        }
    }
public boolean checkUid(String uid)
{
    ArrayList<mTag> list = new ArrayList<mTag>();

    // Select All Query
    String selectQuery = "SELECT  * FROM " + tagTableName;

    SQLiteDatabase db = dbHelper.getReadableDatabase();
    try {

        Cursor cursor = db.rawQuery(selectQuery, null);
        try {

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    String s = cursor.getString(1);
                   if(s.equals(uid))
                   {
                       return true;
                   }
                } while (cursor.moveToNext());
            }

        } finally {
            try {
                cursor.close();
            } catch (Exception ignore) {
            }
        }

    } finally {
        try {
            db.close();
        } catch (Exception ignore) {
        }
    }
return false;
}
    public ArrayList<mTag> getAllTags() {
        ArrayList<mTag> list = new ArrayList<mTag>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + tagTableName;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {

            Cursor cursor = db.rawQuery(selectQuery, null);
            try {

                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        mTag obj = new mTag();
                        obj.setId(cursor.getInt(0));
                        obj.setUid(cursor.getString(1));
                        list.add(obj);
                    } while (cursor.moveToNext());
                }

            } finally {
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }

        } finally {
            try {
                db.close();
            } catch (Exception ignore) {
            }
        }

        return list;
    }

    public mTag getTag(String uid) {
        try {
            openDb();
            Cursor cursor = db.query(tagTableName, new String[]{tagId, tagUid}, tagUid + "=" + uid, new String[]{uid}, null, null, null, null);
            //query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)

            if (cursor != null)
                cursor.moveToFirst();
            mTag tag = new mTag(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
            cursor.close();
            return tag;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Location getLocation(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(locTableName, new String[]{locId,
                        locName}, locId + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Location contact = new Location(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
        cursor.close();
        return contact;
    }

    public ArrayList<Location> getAllLocations() {
        ArrayList<Location> list = new ArrayList<Location>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + locTableName;

//        SQLiteDatabase db = this.getReadableDatabase();
        try {

            openDb();

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
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }

        } finally {
            try {
                // db.close();
            } catch (Exception ignore) {
            }
        }

        return list;
    }

    public void seed() {
        insertLocation(new Location(0, "Front Gate"));
        insertLocation(new Location(1, "VIP"));
        insertLocation(new Location(2, "Backstage"));
    }

    public void openDb() {
        if (db == null) {
            db = dbHelper.getWritableDatabase();
        }
    }

    public void closeDb() {
        db.close();
    }

    public class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_LOCATION_TABLE);
            db.execSQL(CREATE_TAG_TABLE);
            db.execSQL(CREATE_LOCATIONTAG_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + tagTableName);
            db.execSQL("DROP TABLE IF EXISTS " + locTableName);
            db.execSQL("DROP TABLE IF EXISTS " + locTableName + tagTableName);
            onCreate(db);

        }

    }
}