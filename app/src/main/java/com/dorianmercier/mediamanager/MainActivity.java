package com.dorianmercier.mediamanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.dorianmercier.mediamanager.Database.AppDatabase;
import com.dorianmercier.mediamanager.Database.Media;
import com.dorianmercier.mediamanager.Database.MediaDAO;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    AppDatabase db;
    MediaDAO mediaDAO;
    List<Media> index;

    String[] permissions = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Utils.checkAllPermissions(this, permissions)) {
            requestPermissions(permissions, 0);
        }

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "MediaManagerDatabase").build();
        mediaDAO = db.mediaDAO();
        reload();
    }

    public void buttonHandler(View view) {
        switch(view.getId()) {
            case R.id.buttonDebug:
                Intent intent = new Intent(MainActivity.this, DebugActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void reload() {
        new Thread(new Runnable() {
            public void run() {
                index = mediaDAO.getIndex();
                int count = 0;
                LinearLayout linearLayoutMain = new LinearLayout(getApplicationContext());
                linearLayoutMain.setOrientation(LinearLayout.VERTICAL);
                ScrollView scrollView = findViewById(R.id.scrollViewMain);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;

                Log.d("Width of the screen", "" + width);

                LinearLayout currLinearLayout = new LinearLayout(getApplicationContext());

                for(Media media : index) {
                    if(count == 0) {
                        currLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    }
                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setImageResource(R.drawable.unloadedimage);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width/4, width/4);
                    imageView.setLayoutParams(layoutParams);
                    currLinearLayout.addView(imageView);

                    if(count == 3) {
                        linearLayoutMain.addView(currLinearLayout);
                        currLinearLayout = new LinearLayout(getApplicationContext());
                    }

                    count = (count + 1) % 4;
                }
                linearLayoutMain.addView(currLinearLayout);

                runOnUiThread(new Runnable() {
                    public void run() {
                            scrollView.removeAllViews();
                            scrollView.addView(linearLayoutMain);
                    }
                });
            }
        }).start();
    }
}