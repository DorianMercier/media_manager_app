package com.dorianmercier.mediamanager.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface SettingDAO {

    @Insert
    void insertAll(Setting... setting);

    @Query("SELECT value FROM Setting WHERE name LIKE :name")
    String findSetting(String name);

}
