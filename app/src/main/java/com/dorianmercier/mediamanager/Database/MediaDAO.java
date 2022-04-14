package com.dorianmercier.mediamanager.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MediaDAO {
    @Query("SELECT * FROM Media")
    List<Media> getIndex();

    @Query("SELECT * FROM Media WHERE year LIKE :year AND month LIKE :month AND day LIKE :day AND hour LIKE :hour AND minute LIKE :minute AND second LIKE :second LIMIT 1")
    Media findByTime(int year, int month, int day, int hour, int minute, int second);

    @Query("DELETE FROM Media")
    void voidAll();

    @Insert
    void insertAll(Media... media);

    @Delete
    void delete(Media media);

}
