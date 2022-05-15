package com.dorianmercier.mediamanager.Database;

import android.graphics.Bitmap;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface IconDAO {

    @Insert
    void insertAll(Icon... icon);

    @Query("SELECT bitmap FROM Icon WHERE year LIKE :year AND month LIKE :month AND day LIKE :day AND hour LIKE :hour AND minute LIKE :minute AND second LIKE :second LIMIT 1")
    Bitmap getBitmap(int year, int month, int day, int hour, int minute, int second);

    @Query("SELECT COUNT(id) FROM Icon")
    int getCount();

    @Update
    void update(Icon... icon);

    @Query("SELECT id FROM Icon ORDER BY last_use ASC LIMIT 1")
    int get_oldest_id();
}
