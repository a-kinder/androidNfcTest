package com.example.angela.test;

/**
 * Created by angela on 2016-06-21.
 */
public class mTag {
    public int id;
    public String uid;

    public mTag(int id, String uid)
    {
        this.id = id;
        this.uid = uid;
    }
    public mTag() {}

    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
    public String getUid()
    {
        return uid;
    }
    public void setUid(String uid)
    {
        this.uid = uid;
    }
}
