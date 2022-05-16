package com.dorianmercier.mediamanager.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MediaDAO {
    @Query("SELECT * FROM Media ORDER BY year DESC, month DESC, day DESC, hour DESC, minute DESC, second DESC")
    List<Media> getIndex();

    @Query("SELECT * FROM Media WHERE year LIKE :year AND month LIKE :month AND day LIKE :day AND hour LIKE :hour AND minute LIKE :minute AND second LIKE :second LIMIT 1")
    Media findByTime(int year, int month, int day, int hour, int minute, int second);

    @Query("DELETE FROM Media")
    void voidAll();

    @Query("SELECT is_sync FROM Media WHERE year LIKE :year AND month LIKE :month AND day LIKE :day AND hour LIKE :hour AND minute LIKE :minute AND second LIKE :second LIMIT 1")
    boolean is_sync(int year, int month, int day, int hour, int minute, int second);

    @Insert
    void insertAll(Media... media);

    @Delete
    void delete(Media media);

}
