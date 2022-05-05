package com.dorianmercier.mediamanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dorianmercier.mediamanager.Database.AppDatabase;
import com.dorianmercier.mediamanager.Database.Media;
import com.dorianmercier.mediamanager.Database.MediaDAO;
import com.dorianmercier.mediamanager.background.DataManager;
import com.dorianmercier.mediamanager.http.RequestHandler;

import java.util.ArrayList;
import java.util.List;

public class DebugActivity extends AppCompatActivity {

    private DataManager dataManager;
    private AppDatabase db;
    private MediaDAO mediaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        dataManager = new DataManager(getApplicationContext());
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "MediaManagerDatabase").build();
        mediaDAO = db.mediaDAO();
    }

    public void buttonHandler(View view) {
        Log.d("buttonHandler", new String("Entering buttonHandler function"));
        switch(view.getId()) {
            case R.id.buttonDebugGetIndex:
                String message = "We are in buttonDebugGetIndex";
                Log.d("buttonHandler", message);
                new Thread(new Runnable() {
                    public void run() {
                        ArrayList<Media> index = RequestHandler.requestIndex();
                        TextView textView = findViewById(R.id.textDebug);
                        StringBuilder final_text = new StringBuilder("[\n    ");
                        String curr_media;
                        assert index != null;
                        for(Media media : index) {
                            curr_media = "    {\n";
                            curr_media += "        \"year\": " + media.year + ",\n";
                            curr_media += "        \"month\": " + media.month + ",\n";
                            curr_media += "        \"day\": " + media.day + ",\n";
                            curr_media += "        \"hour\": " + media.hour + ",\n";
                            curr_media += "        \"minute\": " + media.minute + ",\n";
                            curr_media += "        \"second\": " + media.second + ",\n";
                            curr_media += "    },\n";
                            final_text.append(curr_media);
                        }
                        textView.setText(final_text.toString());
                    }
                }).start();
                break;
            case R.id.buttonDebugResetIndex:
                dataManager.reset_index();
                break;
            case R.id.buttonDebugSeeDatabase:
                new Thread(new Runnable() {
                    public void run() {
                        List<Media> index = mediaDAO.getIndex();
                        TextView textView = findViewById(R.id.textDebug);
                        StringBuilder final_text = new StringBuilder("[\n    ");
                        String curr_media;
                        assert index != null;
                        for(Media media : index) {
                            curr_media = "    {\n";
                            curr_media += "        \"year\": " + media.year + ",\n";
                            curr_media += "        \"month\": " + media.month + ",\n";
                            curr_media += "        \"day\": " + media.day + ",\n";
                            curr_media += "        \"hour\": " + media.hour + ",\n";
                            curr_media += "        \"minute\": " + media.minute + ",\n";
                            curr_media += "        \"second\": " + media.second + ",\n";
                            curr_media += "    },\n";
                            final_text.append(curr_media);
                        }
                        textView.setText(final_text.toString());
                    }
                }).start();
                break;
            case R.id.buttonGetIcon:
                new Thread(new Runnable() {
                    public void run() {
                        Bitmap icon = RequestHandler.get_icon(2022,6,23,15,45,57,100);
                        ((ImageView) findViewById(R.id.iconImage)).setImageBitmap(icon);
                    }
                }).start();
                break;
            default:
                break;

        }
    }
}