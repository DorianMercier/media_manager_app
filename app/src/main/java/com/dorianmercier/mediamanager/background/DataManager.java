package com.dorianmercier.mediamanager.background;

import android.content.Context;

import androidx.room.Room;

import com.dorianmercier.mediamanager.Database.AppDatabase;
import com.dorianmercier.mediamanager.Database.Media;
import com.dorianmercier.mediamanager.Database.MediaDAO;
import com.dorianmercier.mediamanager.http.RequestHandler;

import java.util.ArrayList;

public class DataManager {
    private final AppDatabase db;
    private final MediaDAO mediaDAO;

    public DataManager(Context context) {
        db = Room.databaseBuilder(context, AppDatabase.class, "MediaManagerDatabase").build();
        mediaDAO = db.mediaDAO();
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
}
