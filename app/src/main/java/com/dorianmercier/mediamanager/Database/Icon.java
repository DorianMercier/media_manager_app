package com.dorianmercier.mediamanager.Database;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class Icon {

    public Icon(Bitmap bitmap, int year, int month, int day, int hour, int minute, int second) {
        this.bitmap = bitmap;

        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;

    }

    public Icon(Bitmap bitmap, Media media) {
        this(bitmap, media.year, media.month, media.day, media.hour, media.minute, media.second);
    }

    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int second;

    public int last_use;

    @PrimaryKey(autoGenerate = true)
    int id;

    Bitmap bitmap;

}
