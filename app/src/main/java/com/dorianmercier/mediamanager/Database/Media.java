package com.dorianmercier.mediamanager.Database;


import androidx.room.Entity;
import androidx.room.Ignore;

import java.io.Serializable;

@Entity(primaryKeys = {"year", "month", "day", "hour", "minute", "second"})
public class Media implements Serializable {

    public Media(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.is_sync = false;
        this.hash = null;
    }

    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int second;
    public boolean is_sync;
    public String hash;
    //Only used for local medias
    public String file_name;
    public boolean is_local;

    @Ignore
    String id;

    @Ignore
    public String construct_id() {
        id = year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + second;
        return id;
    }


}
