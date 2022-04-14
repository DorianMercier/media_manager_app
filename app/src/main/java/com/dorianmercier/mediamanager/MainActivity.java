package com.dorianmercier.mediamanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dorianmercier.mediamanager.Database.AppDatabase;
import com.dorianmercier.mediamanager.Database.MediaDAO;

public class MainActivity extends AppCompatActivity {

    AppDatabase db;
    MediaDAO mediaDAO;

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

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "index").build();
        mediaDAO = db.mediaDAO();
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
}