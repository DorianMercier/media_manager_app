package com.dorianmercier.mediamanager.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Media.class, Icon.class, Setting.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract MediaDAO mediaDAO();
    public abstract IconDAO iconDAO();
    public abstract SettingDAO settingDAO();

}
