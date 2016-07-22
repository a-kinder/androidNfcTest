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
    DaoMaster daoMaster;
    DaoSession daoSession;
    TagDao tagDao;
    LocationDao locDao;
    LocationTagDao locTagDao;
    DaoMaster.DevOpenHelper dbHelper;

    public DbAccess(Context context) {

        // seed();
//        dbHelper = new DaoMaster.DevOpenHelper(context, "nfc-db", null);
//        db = dbHelper.getWritableDatabase("secretsquirrel");

        Database db = new DaoMaster.EncryptedOpenHelper(context, "mydatabase") {
            @Override
            public void onUpgrade(Database db, int oldVersion, int newVersion) {
                // Use db.execSQL(...) to execute SQL for schema updates
            }
        }.getReadableDatabase("secret-password");
        DaoSession daoSession = new DaoMaster(db).newSession();
    }

    public boolean insertLocation(Location location) {
        try {


//            daoMaster = new DaoMaster(db);
//            daoSession = daoMaster.newSession();
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

    public boolean insertTag(String uid, Location loc) {
        com.abc.greendaoexample.db.Tag tag = new com.abc.greendaoexample.db.Tag(null, uid);
        ;
        try {
            if (getTag(uid) == null) {//tag not in database


//                daoMaster = new DaoMaster(db);
//                daoSession = daoMaster.newSession();
                tagDao = daoSession.getTagDao();
                locTagDao = daoSession.getLocationTagDao();


                Log.d("Tag", "inserting new tag to Db");
                tagDao.insert(tag);

                Log.d("DaoExample", "Inserted new tag, ID: " + uid);


            }
            Log.d("Tag", "Tag in Db");
            if (addLocationToTag(loc, tag))//checks for and adds join data
            {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d("Tag error", e.getMessage());
            return false;
        }
    }

    public boolean checkUid(String uid) {
//

//        daoMaster = new DaoMaster(db);
//        daoSession = daoMaster.newSession();
        tagDao = daoSession.getTagDao();

        List<Tag> tags = tagDao.queryBuilder().list();
        for (int i = 0; i < tags.size(); i++) {
            String tagid = tags.get(i).getUid();
            if (tagid.equals(uid)) {
                return true;
            }
        }
        return false;
    }

    public List<Tag> getAllTags() {

        try {
            Log.d("Tag", "Retrieving all records");
//            daoMaster = new DaoMaster(db);

//            daoSession = daoMaster.newSession();
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

//            daoMaster = new DaoMaster(db);
//            daoSession = daoMaster.newSession();
            tagDao = daoSession.getTagDao();
            Tag tag = tagDao.queryBuilder().where(TagDao.Properties.Uid.eq(uid)).list().get(0);
            if (tag != null) {
                return tag;
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d("Tag error", "Message-" + e.getMessage());
            return null;
        }
    }

    public Location getLocation(int id) {
        try {
            Log.d("Location", "Retrieving single record");

//            daoMaster = new DaoMaster(db);
//            daoSession = daoMaster.newSession();
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

//            daoMaster = new DaoMaster(db);
//            daoSession = daoMaster.newSession();
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
//            daoMaster = new DaoMaster(db);
//            daoSession = daoMaster.newSession();
            locDao = daoSession.getLocationDao();
            locDao.deleteAll();
            Log.d("Location", "All records deleted");
            return true;
        } catch (Exception e) {
            Log.d("Location delete error", e.getMessage());
            return false;
        }
    }

    public boolean deleteAllTags() {
//        daoMaster = new DaoMaster(db);
        try {
//            daoSession = daoMaster.newSession();
//            tagDao = daoSession.getTagDao();
            tagDao.deleteAll();
            Log.d("Tag", "All records deleted");
            return true;
        } catch (Exception e) {
            Log.d("Tag delete error", e.getMessage());
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
            Log.d("LT delete error", "message: " + e.getMessage());
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

    public boolean addLocationToTag(Location loc, Tag tag) {
        try {
//            daoMaster = new DaoMaster(db);
//            daoSession = daoMaster.newSession();
            locTagDao = daoSession.getLocationTagDao();

            //checks for existing data
            if (loc.getId() != null && tag.getId() != null) {
                List<LocationTag> ltList = locTagDao.queryBuilder().where(LocationTagDao.Properties.Location_id.eq(loc.getId())).where(LocationTagDao.Properties.Tag_id.eq(tag.getId())).list();
                if (ltList.isEmpty()) {
                    Log.d("Tag Location", "inserting join data to Db");
                    LocationTag lt = new LocationTag(null, tag.getId(), loc.getId());
                    locTagDao.insert(lt);//make this transactional
                }


                return true;
            } else {
                Log.d("LT insert error", "location or tag ID is null");

                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean checkTagLocation(String uid, Location loc) {
        try {
//            daoMaster = new DaoMaster(db);
//            daoSession = daoMaster.newSession();
            locTagDao = daoSession.getLocationTagDao();
            List<LocationTag> ltList = locTagDao.queryBuilder().where(LocationTagDao.Properties.Location_id.eq(loc.getId())).where(LocationTagDao.Properties.Tag_id.eq(uid)).list();
            return ltList.isEmpty();
        } catch (Exception e) {
            Log.d("Check Tag Location", "message: " + e.getMessage());
            return false;
        }
    }
}
