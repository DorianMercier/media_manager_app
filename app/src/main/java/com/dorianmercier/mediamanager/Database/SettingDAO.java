package com.dorianmercier.mediamanager.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SettingDAO {

    @Insert
    void insertAll(Setting... setting);

    @Update
    void update(Setting... setting);

    @Query("SELECT value FROM Setting WHERE name LIKE :name")
    String findSetting(String name);

}
