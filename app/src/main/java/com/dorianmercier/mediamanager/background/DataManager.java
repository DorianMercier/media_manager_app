package com.dorianmercier.mediamanager.background;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.room.Room;

import com.dorianmercier.mediamanager.Database.AppDatabase;
import com.dorianmercier.mediamanager.Database.Icon;
import com.dorianmercier.mediamanager.Database.IconDAO;
import com.dorianmercier.mediamanager.Database.Media;
import com.dorianmercier.mediamanager.Database.MediaDAO;
import com.dorianmercier.mediamanager.Database.Setting;
import com.dorianmercier.mediamanager.Database.SettingDAO;
import com.dorianmercier.mediamanager.http.RequestHandler;

import java.util.ArrayList;

public class DataManager {
    private final AppDatabase db;
    private final MediaDAO mediaDAO;
    private final IconDAO iconDAO;
    private final SettingDAO settingDAO;

    public DataManager(Context context) {
        db = Room.databaseBuilder(context, AppDatabase.class, "MediaManagerDatabase").build();
        mediaDAO = db.mediaDAO();
        iconDAO = db.iconDAO();
        settingDAO = db.settingDAO();
    }


    public void reset_index() {
        new Thread(new Runnable() {
            public void run() {
                ArrayList<Media> index = RequestHandler.requestIndex();
                if(index != null) {
                    mediaDAO.voidAll();
                    for(Media media : index) {
                        mediaDAO.insertAll(media);
                    }
                }
            }
        }).start();
    }

    public void set_setting(String name, String value) {
        new Thread(new Runnable() {
            public void run() {
                settingDAO.insertAll(new Setting(name, value));
            }
        }).start();

    }

    private void save_new_icon(Icon icon) {
        new Thread(new Runnable() {
            public void run() {
                int tmp_count = iconDAO.getCount();
                if (tmp_count < 200) {
                    icon.last_use = (int) (System.currentTimeMillis() / 1000);
                    iconDAO.insertAll(icon);
                } else {
                    icon.id = iconDAO.get_oldest_id();
                    icon.last_use = (int) (System.currentTimeMillis() / 1000);
                    iconDAO.update(icon);
                }
            }
        }).start();
    }

    public Bitmap load_icon(int year, int month, int day, int hour, int minute, int second, int size) {
        Bitmap bitmap = iconDAO.getBitmap(year, month, day, hour, minute, second);
        if(bitmap == null) {
            bitmap = RequestHandler.get_icon(year, month, day, hour, minute, second, size);
            if(bitmap == null) return null;
            Icon icon = new Icon(bitmap, year, month, day, hour, minute, second);
            save_new_icon(icon);
        }
        return bitmap;
    }

    public Bitmap load_icon(Media media, int size) {
        return load_icon(media.year, media.month, media.day, media.hour, media.minute, media.second, size);
    }
}

