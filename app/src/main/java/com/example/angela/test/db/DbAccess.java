package com.example.angela.test.db;

/**
 * Created by angela on 2016-06-21.
 */

import net.sqlcipher.database.*;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import com.abc.greendaoexample.db.DaoMaster;
import com.abc.greendaoexample.db.DaoSession;
import com.abc.greendaoexample.db.Location;
import com.abc.greendaoexample.db.LocationDao;
import com.abc.greendaoexample.db.LocationTag;
import com.abc.greendaoexample.db.LocationTagDao;
import com.abc.greendaoexample.db.Tag;
import com.abc.greendaoexample.db.TagDao;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.List;

import de.greenrobot.dao.database.Database;
import de.greenrobot.dao.database.Database.*;
import de.greenrobot.dao.database.EncryptedDatabase;


import de.greenrobot.dao.database.EncryptedDatabaseOpenHelper;

public class DbAccess {

    //private Database db;
    //SQLiteOpenHelper dbHelper;
//    DaoMaster daoMaster;
    DaoSession daoSession;
    TagDao tagDao;
    LocationDao locDao;
    LocationTagDao locTagDao;
    //    DaoMaster.DevOpenHelper dbHelper;
    Database db;

    public DbAccess(Context context) {

        db = new DaoMaster.EncryptedOpenHelper(context, "mydatabase") {
            @Override
            public void onUpgrade(Database db, int oldVersion, int newVersion) {
                // Use db.execSQL(...) to execute SQL for schema updates
            }
        }.getReadableDatabase("secret-password");
        daoSession = new DaoMaster(db).newSession();
    }

    public boolean close() {
        try {
            db.close();
            return true;
        } catch (Exception e) {
            Log.e("DB close error", "Message:" + e.getMessage());
            return false;
        }
    }

    public Location insertLocation(String name) {
        try {

            locDao = daoSession.getLocationDao();

            com.abc.greendaoexample.db.Location loc = new com.abc.greendaoexample.db.Location(null, name);
            loc.setId(locDao.insert(loc));
            Log.d("DaoExample", "Inserted new location, ID: " + loc.getId());
            return loc;
        } catch (Exception e) {
            Log.d("Location error", e.getMessage());
        }
        return null;
    }
    public Location insertLocation(Long id, String name) {
        try {

            locDao = daoSession.getLocationDao();

            com.abc.greendaoexample.db.Location loc = new com.abc.greendaoexample.db.Location(id, name);
            locDao.insert(loc);
            Log.d("DaoExample", "Inserted new location, ID: " + loc.getId());
            return loc;
        } catch (Exception e) {
            Log.d("Location error", e.getMessage());
        }
        return null;
    }
    public Tag insertTag(String uid, Location loc) {
        Tag tag = new Tag();
        try {

            tagDao = daoSession.getTagDao();
            locTagDao = daoSession.getLocationTagDao();

            Log.d("Tag", "inserting new tag to Db");
            tag.setId(tagDao.insert(new com.abc.greendaoexample.db.Tag(null, uid)));
            tag.setUid(uid);
            Log.d("DaoExample", "Inserted new tag, ID: " + uid);
            if (loc != null)//checks for and adds join data
            {
                addLocationToTag(loc, tag);
            }
            return tag;
        } catch (Exception e) {
            Log.d("Tag error", "message: " + e.getMessage());
            return null;
        }
    }

//    public Tag checkUid(String uid) {
//
//        Tag tag;
//        tagDao = daoSession.getTagDao();
//
//        List<Tag> tags = getAllTags();
//        for (int i = 0; i < tags.size(); i++) {
//            tag = tags.get(i);
//            if (uid.equals(tag.getUid())) {
//                return tag;
//            }
//        }
//        insertTag(uid, null);
//        return getTag(uid);
//    }

    public List<Tag> getAllTags() {

        try {
            Log.d("Tag", "Retrieving all records");
//            daoMaster = new DaoMaster(db);

//            daoSession = daoMaster.newSession();
            tagDao = daoSession.getTagDao();

            List<Tag> list = tagDao.queryBuilder().list();
            Log.d("Tag", "Retrieved " + list.size() + " records");

            return list;
        } catch (Exception e) {
            Log.e("Tag error", e.getMessage());
            return null;
        }
    }

    public Tag getTag(String uid) {
        try {
            Log.d("Tag", "Retrieving single record");

            tagDao = daoSession.getTagDao();
            Tag tag = tagDao.queryBuilder().where(TagDao.Properties.Uid.eq(uid)).list().get(0);
            Log.d("Tag", "Retrieved tag: " + tag.getUid());


            return tag;
        } catch (Exception e) {
            Log.e("Tag error", "Message-" + e.getMessage() + ", returning null");
            return null;
        }
    }

    public Location getLocation(int id) {
        try {
            Log.d("Location", "Retrieving single record");

//            daoMaster = new DaoMaster(db);
//            daoSession = daoMaster.newSession();
            locDao = daoSession.getLocationDao();
            Location loc = locDao.queryBuilder().where(TagDao.Properties.Id.eq(id)).list().get(0);

            Log.d("Location", "Retrieved location: " + loc.getName());
            return loc;
        } catch (Exception e) {
            Log.e("Location retrieve error", e.getMessage() + ", returning null");
            return null;
        }
    }

    public List<Location> getAllLocations() {
        try {
            Log.d("Location", "Retrieving all records");

//            daoMaster = new DaoMaster(db);
//            daoSession = daoMaster.newSession();
            locDao = daoSession.getLocationDao();
            List<Location> list = locDao.queryBuilder().list();
            Log.d("Location", "Retrieved " + list.size() + " records");
            return list;
        } catch (Exception e) {
            Log.e("Location error", e.getMessage() + ", returning null");
            return null;
        }
    }

    public boolean deleteAllLocations() {

        try {
//            daoMaster = new DaoMaster(db);
//            daoSession = daoMaster.newSession();
            locDao = daoSession.getLocationDao();
            locDao.deleteAll();
            Log.d("Location", "All records deleted");
            return true;
        } catch (Exception e) {
            Log.e("Location delete error", e.getMessage());
            return false;
        }
    }

    public boolean deleteAllTags() {
        try {
            tagDao = daoSession.getTagDao();
            tagDao.deleteAll();
            Log.d("Tag", "All records deleted");
            return true;
        } catch (Exception e) {
            Log.e("Tag delete error", e.getMessage());
            return false;
        }
    }

    public boolean deleteAllLocationTags() {
        try {
//            daoSession = daoMaster.newSession();
            locTagDao = daoSession.getLocationTagDao();
            locTagDao.deleteAll();
            Log.d("Location Tag", "All records deleted");
            return true;
        } catch (Exception e) {
            Log.e("LT delete error", "message: " + e.getMessage());
            return false;
        }
    }

    public void seed() {
        deleteAllLocations();
        deleteAllLocationTags();
        deleteAllLocations();
        deleteAllTags();
        try {
            insertLocation(Long.valueOf(1),"Front Gate");
            insertLocation(Long.valueOf(2),"VIP");
            insertLocation(Long.valueOf(3),"Backstage");
            Log.d("Database seed", "Inserted 3 locations");
        } catch (Exception e) {
            Log.e("DB seed error", e.getMessage());

        }
    }

    public boolean addLocationToTag(Location loc, Tag tag) {
        try {
            locTagDao = daoSession.getLocationTagDao();

            //checks for existing data
            if (loc.getId() != null && tag.getId() != null) {
                List<LocationTag> ltList = locTagDao.queryBuilder().where(LocationTagDao.Properties.Location_id.eq(loc.getId())).where(LocationTagDao.Properties.Tag_id.eq(tag.getId())).list();
                if (ltList.isEmpty()) {
                    Log.d("Tag Location", "inserting join data to Db");
                    LocationTag lt = new LocationTag(null, tag.getId(), loc.getId());
                    locTagDao.insert(lt);//make this transactional
                    Log.d("Tag Location", "inserted location " + loc.getId() + " and tag " + tag.getId());

                    return true;
                }


            } else {
                if (loc.getId() == null) {
                    Log.e("LT insert error", "Location ID is null");

                } else if (tag.getId() == null) {
                    Log.e("LT insert error", "Tag ID is null");

                } else {
                    Log.e("LT insert error", "Location or tag is null");

                }

                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean checkTagLocation(Tag tag, Location loc) {
        Log.d("checking join table", "Looking for Location " + loc.getId() + " and tag " + tag.getId());
        try {
            locTagDao = daoSession.getLocationTagDao();
            List<LocationTag> ltList = locTagDao.queryBuilder().where(LocationTagDao.Properties.Location_id.eq(loc.getId())).where(LocationTagDao.Properties.Tag_id.eq(tag.getId())).list();//checks join table for ids
            return ltList.isEmpty();
        } catch (Exception e) {
            Log.e("Check Tag Location", "message: " + e.getMessage());
            return false;
        }
    }
}
