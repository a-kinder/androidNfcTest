package com.example.angela.test;

/**
 * Created by angela on 2016-06-21.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.*;
import android.content.Context;
import android.util.Log;

import com.abc.greendaoexample.db.DaoMaster;
import com.abc.greendaoexample.db.DaoSession;
import com.abc.greendaoexample.db.Location;
import com.abc.greendaoexample.db.LocationDao;
import com.abc.greendaoexample.db.Tag;
import com.abc.greendaoexample.db.TagDao;

import java.util.ArrayList;
import java.util.List;

public class DbAccess {
    static final String tagTableName = "TAG";
    static final String tagId = "id";
    static final String tagUid = "uid";
    static final String tagName = "tags";
    static final String locTableName = "LOCATION";
    static final String locId = "id";
    static final String locName = "name";
    static final String locTagTableName = locTableName + tagTableName;
    static final String ltTagId = "tagId";
    static final String ltLocId = "locId";


    public static final String DATABASE_NAME = "NfcDb.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TAG_TABLE =
            "CREATE TABLE " + tagTableName + " (" +
                    tagId + " INTEGER PRIMARY KEY , " +
                    tagUid + " TEXT);";
    private static final String CREATE_LOCATION_TABLE =
            "CREATE TABLE " + locTableName + " (" +
                    locId + " INTEGER PRIMARY KEY , " +
                    locName + " TEXT);";
    private static final String CREATE_LOCATIONTAG_TABLE =
            "CREATE TABLE " + locTagTableName + " (" +
                    ltLocId + " INTEGER REFERENCES locations(id), " +
                    ltTagId + " INTEGER REFERENCES tags(id));";
    private SQLiteDatabase db;
    //SQLiteOpenHelper dbHelper;
    DaoMaster daoMaster;
    DaoSession daoSession;
    TagDao tagDao;
    LocationDao locDao;
    DaoMaster.DevOpenHelper dbHelper;

    public DbAccess(Context context) {

        // seed();
        dbHelper = new DaoMaster.DevOpenHelper(context, "nfc-db", null);
        db = dbHelper.getWritableDatabase();
        deleteAllLocations();
        seed();
    }

    public boolean insertLocation(Location location) {
        try {


            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            locDao = daoSession.getLocationDao();

            com.abc.greendaoexample.db.Location loc = new com.abc.greendaoexample.db.Location(null, location.getName());
            locDao.insert(loc);
            Log.d("DaoExample", "Inserted new location, ID: " + loc.getId());
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean insertTag(String uid) {
        try {
            if (getTag(uid) == null) {//tag not in database


                daoMaster = new DaoMaster(db);
                daoSession = daoMaster.newSession();
                tagDao = daoSession.getTagDao();

                com.abc.greendaoexample.db.Tag note = new com.abc.greendaoexample.db.Tag(null, uid);
                tagDao.insert(note);
                Log.d("DaoExample", "Inserted new tag, ID: " + uid);


            }
            return true;
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            return false;
        }
    }

    public boolean checkUid(String uid) {
//

        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        tagDao = daoSession.getTagDao();

        List<Tag> tags = tagDao.queryBuilder().list();
        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i).getUid() == uid) {
                return true;
            }
        }
        return false;
    }

    public List<Tag> getAllTags() {
//
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        tagDao = daoSession.getTagDao();

        List<Tag> list = tagDao.queryBuilder().list();
        return list;
    }

    public Tag getTag(String uid) {
//
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        tagDao = daoSession.getTagDao();

        return tagDao.queryBuilder().where(TagDao.Properties.Uid.eq(uid)).list().get(0);
    }

    public Location getLocation(int id) {
//
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        locDao = daoSession.getLocationDao();

        return locDao.queryBuilder().where(TagDao.Properties.Id.eq(id)).list().get(0);
    }

    public List<Location> getAllLocations() {


        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        locDao = daoSession.getLocationDao();
        List<Location> list = locDao.queryBuilder().list();
        return list;
    }

    public boolean deleteAllLocations() {

        try {
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            locDao = daoSession.getLocationDao();
            locDao.deleteAll();
            return true;
        } catch (Exception e) {
            Log.d("Location delete error", e.getMessage());
            return false;
        }
    }

    public void seed() {

        insertLocation(new Location(null, "Front Gate"));
        insertLocation(new Location(null, "VIP"));
        insertLocation(new Location(null, "Backstage"));
    }

    public void openDb() {
        if (db == null) {
            db = dbHelper.getWritableDatabase();
        }
    }

    public void closeDb() {
        db.close();
    }

    public boolean addLocationToTag(int locationId, String tagUid) {
        try {
            ContentValues values = new ContentValues();
            values.put(ltTagId, tagUid);
            values.put(ltLocId, locationId);
            db.insert(locTagTableName, null, values);

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {


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
