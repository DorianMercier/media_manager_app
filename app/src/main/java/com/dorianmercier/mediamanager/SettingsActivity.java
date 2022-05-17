package com.dorianmercier.mediamanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dorianmercier.mediamanager.Database.AppDatabase;
import com.dorianmercier.mediamanager.Database.Setting;
import com.dorianmercier.mediamanager.Database.SettingDAO;
import com.dorianmercier.mediamanager.http.RequestHandler;

public class SettingsActivity extends AppCompatActivity {

    private SettingDAO settingDAO;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "MediaManagerDatabase").build();
        settingDAO = db.settingDAO();

        new Thread(new Runnable() {
            public void run() {
                String tmp_domain = settingDAO.findSetting("IP");
                if (tmp_domain != null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ((EditText) findViewById(R.id.editDomainName)).setText(tmp_domain);
                        }
                    });
                }
                String tmp_port = settingDAO.findSetting("port");
                if (tmp_port != null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ((EditText) findViewById(R.id.editPortNumber)).setText(tmp_port);
                        }
                    });
                }
            }
        }).start();

    }

    public void save_settings(View view) {
        EditText editDomain = findViewById(R.id.editDomainName);
        EditText editPort = findViewById(R.id.editPortNumber);


        String domain = editDomain.getText().toString();
        String port = editPort.getText().toString();
        new Thread(new Runnable() {
            public void run() {
                settingDAO.update(new Setting("IP", domain));
                settingDAO.update(new Setting("port", port));
                RequestHandler.domain = domain;
                RequestHandler.port = port;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Settings saved", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}