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
            Log.d("Location error", e.getMessage());
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
            Log.d("Tag error", e.getMessage());
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

        try {
            Log.d("Tag", "Retrieving all records");
            daoMaster = new DaoMaster(db);

            daoSession = daoMaster.newSession();
            tagDao = daoSession.getTagDao();

            List<Tag> list = tagDao.queryBuilder().list();
            return list;
        } catch (Exception e) {
            Log.d("Tag error", e.getMessage());
            return null;
        }
    }

    public Tag getTag(String uid) {
        try {
            Log.d("Tag", "Retrieving single record");

            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            tagDao = daoSession.getTagDao();

            return tagDao.queryBuilder().where(TagDao.Properties.Uid.eq(uid)).list().get(0);
        } catch (Exception e) {
            Log.d("Tag error", e.getMessage());
            return null;
        }
    }

    public Location getLocation(int id) {
        try {
            Log.d("Location", "Retrieving single record");

            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            locDao = daoSession.getLocationDao();

            return locDao.queryBuilder().where(TagDao.Properties.Id.eq(id)).list().get(0);
        } catch (Exception e) {
            Log.d("Location retrieve error", e.getMessage());
            return null;
        }
    }

    public List<Location> getAllLocations() {
        try {
            Log.d("Location", "Retrieving all records");

            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            locDao = daoSession.getLocationDao();
            List<Location> list = locDao.queryBuilder().list();
            return list;
        } catch (Exception e) {
            Log.d("Location error", e.getMessage());
            return null;
        }
    }

    public boolean deleteAllLocations() {

        try {
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            locDao = daoSession.getLocationDao();
            locDao.deleteAll();
            Log.d("Location delete", "All records deleted");
            return true;
        } catch (Exception e) {
            Log.d("Location delete error", e.getMessage());
            return false;
        }
    }

    public void seed() {
        try {
            insertLocation(new Location(null, "Front Gate"));
            insertLocation(new Location(null, "VIP"));
            insertLocation(new Location(null, "Backstage"));
            Log.d("Database seed", "Inserted 3 locations");
        } catch (Exception e) {
            Log.d("DB seed error", e.getMessage());

        }
    }

//    public void openDb() {
//        if (db == null) {
//            db = dbHelper.getWritableDatabase();
//        }
//    }
//
//    public void closeDb() {
//        db.close();
//    }

    public boolean addLocationToTag(int locationId, String tagUid) {
        try {

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }


}
