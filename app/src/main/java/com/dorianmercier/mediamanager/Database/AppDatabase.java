package com.dorianmercier.mediamanager.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Media.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MediaDAO mediaDAO();
}
