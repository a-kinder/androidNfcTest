package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MainGenerator {
    private static final String PROJECT_DIR = System.getProperty("user.dir");

    public static void main(String[] args) {
        Schema schema = new Schema(1, "com.abc.greendaoexample.db");
        schema.enableKeepSectionsByDefault();

        addTables(schema);

        try {
            new DaoGenerator().generateAll(schema, PROJECT_DIR + "//app//src//main//java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTables(final Schema schema) {
        Entity location = addLocation(schema);
        Entity tag = addTag(schema);

        //JOIN TABLE
        Entity locationTag = schema.addEntity("LocationTag");
        locationTag.addIdProperty();
        Property exerciseId = locationTag.addLongProperty("location_id").getProperty();
        Property accessoryId = locationTag.addLongProperty("tag_id").getProperty();

        location.addToMany(locationTag, accessoryId).setName("locationFK");
        tag.addToMany(locationTag, exerciseId).setName("tagFK");
    }

    private static Entity addLocation(final Schema schema) {
        Entity location = schema.addEntity("Location");
        location.addIdProperty().primaryKey().autoincrement();
        location.addStringProperty("name").notNull();

        return location;
    }

    private static Entity addTag(final Schema schema) {
        Entity tag = schema.addEntity("Tag");
        tag.addIdProperty().primaryKey().autoincrement();
        tag.addStringProperty("uid").notNull();
        return tag;
    }

}
