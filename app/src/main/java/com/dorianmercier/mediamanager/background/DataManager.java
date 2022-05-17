package com.dorianmercier.mediamanager.background;

import static java.lang.Math.max;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.dorianmercier.mediamanager.Database.AppDatabase;
import com.dorianmercier.mediamanager.Database.Icon;
import com.dorianmercier.mediamanager.Database.IconDAO;
import com.dorianmercier.mediamanager.Database.Media;
import com.dorianmercier.mediamanager.Database.MediaDAO;
import com.dorianmercier.mediamanager.Database.Setting;
import com.dorianmercier.mediamanager.Database.SettingDAO;
import com.dorianmercier.mediamanager.MyRecyclerViewAdapter;
import com.dorianmercier.mediamanager.R;
import com.dorianmercier.mediamanager.http.RequestHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DataManager {
    private final AppDatabase db;
    private final MediaDAO mediaDAO;
    private final IconDAO iconDAO;
    private final SettingDAO settingDAO;
    private BitmapFactory.Options option;
    private RequestHandler requestHandler;


    public DataManager(Context context) {
        db = Room.databaseBuilder(context, AppDatabase.class, "MediaManagerDatabase").build();
        mediaDAO = db.mediaDAO();
        iconDAO = db.iconDAO();
        settingDAO = db.settingDAO();
        requestHandler = new RequestHandler(context);

        option = new BitmapFactory.Options();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        option.inScreenDensity = metrics.densityDpi;
        option.inTargetDensity = metrics.densityDpi;
        option.inDensity = DisplayMetrics.DENSITY_DEFAULT;
    }


    public void reset_index(boolean wait) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                ArrayList<Media> index = requestHandler.requestIndex();
                if(index != null) {
                    mediaDAO.voidAll();
                    for(Media media : index) {
                        media.is_sync = true;
                        media.is_local = false;
                        mediaDAO.insertAll(media);
                    }
                }
            }
        });
        thread.start();
        if(wait) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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

    public Bitmap load_icon(Media media, int size) {
        Bitmap bitmap;
        if(media.is_sync) {
            bitmap = iconDAO.getBitmap(media.year, media.month, media.day, media.hour, media.minute, media.second);
            if (bitmap == null) {
                bitmap = requestHandler.get_icon(media.year, media.month, media.day, media.hour, media.minute, media.second, size);
                if (bitmap == null) return null;
                Icon icon = new Icon(bitmap, media.year, media.month, media.day, media.hour, media.minute, media.second);
                save_new_icon(icon);
            }
        }
        else {
            bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + media.file_name, option);
            bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
            int a = 0;
        }
        return bitmap;
    }

    // Checks if external storage is available for read and write
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
    // Checks if external storage is available to at least read
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY));
    }

    public void put_local_into_database() {

        File directory = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
        File[] files = directory.listFiles();


        Media tmp_media;

        for(File file : files) {
            Calendar last_modified = Calendar.getInstance();
            last_modified.setTimeInMillis(file.lastModified());

            tmp_media = new Media(last_modified.get(Calendar.YEAR), last_modified.get(Calendar.MONTH), last_modified.get(Calendar.DAY_OF_MONTH), last_modified.get(Calendar.HOUR_OF_DAY), last_modified.get(Calendar.MINUTE), last_modified.get(Calendar.SECOND));
            tmp_media.is_sync = false;
            tmp_media.file_name = file.getName();
            tmp_media.is_local = true;
            try {
                mediaDAO.insertAll(tmp_media);
            }
            catch(SQLiteConstraintException e) {
                //Media already in database. Nothing to do
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void update_dataset_with_local(List<Media> dataset, MyRecyclerViewAdapter adapter, AppCompatActivity activity) {
        File directory = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
        File[] files = directory.listFiles();


        Media tmp_media;

        boolean dataset_modified = false;

        for(File file : files) {
            Calendar last_modified = Calendar.getInstance();
            last_modified.setTimeInMillis(file.lastModified());

            tmp_media = new Media(last_modified.get(Calendar.YEAR), last_modified.get(Calendar.MONTH) + 1, last_modified.get(Calendar.DAY_OF_MONTH), last_modified.get(Calendar.HOUR_OF_DAY), last_modified.get(Calendar.MINUTE), last_modified.get(Calendar.SECOND));
            tmp_media.is_sync = false;
            tmp_media.file_name = file.getName();
            tmp_media.is_local = true;

            try {
                mediaDAO.insertAll(tmp_media);
                dataset.add(tmp_media);
                dataset_modified = true;
            }
            catch(SQLiteConstraintException e) {
                //Media already in database. Nothing to do
            }
        }
        if(dataset_modified) {
            dataset.sort(new MediaComparator());
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    public Bitmap getPicture(Media media) {
        return requestHandler.get_picture(media.year, media.month, media.day, media.hour, media.minute, media.second);
    }
}

