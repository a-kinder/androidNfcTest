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
import com.example.angela.test.MainActivity;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.List;

import de.greenrobot.dao.database.Database;
import de.greenrobot.dao.database.Database.*;
import de.greenrobot.dao.database.EncryptedDatabase;


import de.greenrobot.dao.database.EncryptedDatabaseOpenHelper;
import de.greenrobot.dao.query.QueryBuilder;

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
Context context;
    public DbAccess(Context context) {
        this.context = context;
        open(context);
    }

    public boolean open(Context context) {
        try {
            db = new DaoMaster.EncryptedOpenHelper(context, "mydatabase") {
                @Override
                public void onUpgrade(Database db, int oldVersion, int newVersion) {
                    // Use db.execSQL(...) to execute SQL for schema updates
                }
            }.getReadableDatabase("secret-password");
            daoSession = new DaoMaster(db).newSession();
            return true;
        } catch (Exception e) {
            Log.e("DB open error", "Message:" + e.getMessage());
            return false;
        }
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
        open(context);
        try {

            locDao = daoSession.getLocationDao();

            com.abc.greendaoexample.db.Location loc = new com.abc.greendaoexample.db.Location(null, name);
            loc.setId(locDao.insert(loc));
            Log.d("DaoExample", "Inserted new location, ID: " + loc.getId());
            close();
            return loc;
        } catch (Exception e) {
            Log.d("Location error", e.getMessage());
        }
        close();
        return null;
    }

    public Location insertLocation(Long id, String name) {
        open(context);
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
        open(context);
        Tag tag = new Tag();
        try {

            tagDao = daoSession.getTagDao();
            locTagDao = daoSession.getLocationTagDao();

            Log.d("Tag", "inserting new tag to Db");
            tag.setId(tagDao.insert(new com.abc.greendaoexample.db.Tag(null, uid)));
            tag.setUid(uid);
            Log.d("DaoExample", "Inserted new tag, ID: " + uid);

            return tag;
        } catch (Exception e) {
            Log.d("Tag error", "message: " + e.getMessage());
            return null;
        }
    }

    public List<Tag> getAllTags() {
        open(context);

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
        open(context);
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
        open(context);
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
        open(context);
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
        open(context);

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
        open(context);
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
        open(context);
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
        open(context);
        deleteAllLocations();
        deleteAllLocationTags();
        deleteAllLocations();
        deleteAllTags();
        try {
            insertLocation(Long.valueOf(1), "Front Gate");
            insertLocation(Long.valueOf(2), "VIP");
            insertLocation(Long.valueOf(3), "Backstage");
            Log.d("Database seed", "Inserted 3 locations");
        } catch (Exception e) {
            Log.e("DB seed error", e.getMessage());

        }
    }

    public boolean addLocationTag(Location loc, Tag tag) {
        open(context);
        try {
            locTagDao = daoSession.getLocationTagDao();

            //checks for existing data
            if (loc.getId() != null && tag.getId() != null) {
                List<LocationTag> ltList = locTagDao.queryBuilder().where(LocationTagDao.Properties.Location_id.eq(loc.getId())).where(LocationTagDao.Properties.Tag_id.eq(tag.getId())).list();
                if (ltList.isEmpty()) {
                    Log.d("Tag Location", "inserting join data to Db");
                    LocationTag lt = new LocationTag(null, loc.getId(), tag.getId());
                    locTagDao.insert(lt);//make this transactional
                    Log.d("Tag Location", "inserted location " + loc.getId() + " and tag " + tag.getId());
//TEST
                    List<LocationTag> l = locTagDao.loadAll();
                    for (int i = 0; i < l.size(); i++) {
                        Log.i("LocationTag: " + l.get(i).getId().toString(), l.get(i).getLocation_id() + " - " + l.get(i).getTag_id());
                    }

                    //TEST
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
        open(context);
        Log.d("checking join table", "Looking for Location " + loc.getId() + " and tag " + tag.getId());
        try {
            locTagDao = daoSession.getLocationTagDao();


            QueryBuilder qb = locTagDao.queryBuilder();

            qb.where(LocationTagDao.Properties.Location_id.eq(loc.getId()), LocationTagDao.Properties.Tag_id.eq(tag.getId()));
            List<LocationTag> ltList = qb.list();


            //TEST
            Log.i("LocationTags", "Listing:");

            for (int i = 0; i < ltList.size(); i++) {

                Log.i("LocationTag: " + ltList.get(i).getId().toString(), "location: " + ltList.get(i).getLocation_id() + " - tag: " + ltList.get(i).getTag_id());
            }

            //TEST

            if (ltList.size() > 0) {
                return true;
            } else {
                return false;
            }
            //  return ltList.isEmpty();
        } catch (Exception e) {
            Log.e("Tag Location Error", "message: " + e.getMessage());
            return false;
        }
    }
}
