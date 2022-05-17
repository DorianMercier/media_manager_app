package com.dorianmercier.mediamanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dorianmercier.mediamanager.Database.AppDatabase;
import com.dorianmercier.mediamanager.Database.Media;
import com.dorianmercier.mediamanager.Database.MediaDAO;
import com.dorianmercier.mediamanager.background.DataManager;

import java.io.Serializable;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.R)
public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    AppDatabase db;
    MediaDAO mediaDAO;
    List<Media> index;
    MyRecyclerViewAdapter adapter;
    Context context;
    AppCompatActivity activity;
    DataManager dataManager;
    boolean reloadDone = false;

    String[] permissions = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if (!Utils.checkAllPermissions(this, permissions)) {
            requestPermissions(permissions, 0);
        }

        context = getApplicationContext();

        db = Room.databaseBuilder(context, AppDatabase.class, "MediaManagerDatabase").build();
        mediaDAO = db.mediaDAO();

        initiate_activity(this);
        activity = this;
        dataManager = new DataManager(context);

        SwipeRefreshLayout myRefreshLayout = findViewById(R.id.swipe_container);
        myRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Log.i("MainActivity", "onRefresh called from SwipeRefreshLayout");
                    dataManager.reset_index(true);
                    index = null;
                    initiate_activity((MyRecyclerViewAdapter.ItemClickListener) activity);
                    myRefreshLayout.setRefreshing(false);
                }
            }
        );
    }

    protected void onResume() {
        super.onResume();
        if(reloadDone) {
            new Thread(new Runnable() {
                public void run() {
                    dataManager.update_dataset_with_local(index, adapter, activity);
                }
            }).start();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        return true;
    }

    /*
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
    */

    private void initiate_activity(MyRecyclerViewAdapter.ItemClickListener listener) {
        new Thread(new Runnable() {
            public void run() {
                Log.d("Thread status", "begin");


                if(index == null) {
                    index = mediaDAO.getIndex();
                    Log.d("Thread status", "Database retrieved");
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int width = displayMetrics.widthPixels;
                    Log.d("Thread status", "width: " + width);
                    Log.d("Thread status", "screen size retrieved");
                    RecyclerView recyclerView = findViewById(R.id.recyclerMain);
                    int numberOfColumns = 4;

                    runOnUiThread(new Runnable() {
                        public void run() {
                            recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));
                            Log.d("Thread status", "LayoutManager configured for recyclerView");
                            adapter = new MyRecyclerViewAdapter(context, index, width, activity);
                            adapter.setClickListener(listener);
                            Log.d("Thread status", "adapter configured");
                            recyclerView.setAdapter(adapter);
                            Log.d("Thread status", "adapter linked to recyclerView");
                            new Thread(new Runnable() {
                                public void run() {
                                    dataManager.update_dataset_with_local(index, adapter, activity);
                                    reloadDone = true;
                                }
                            }).start();
                        }
                    });
                }
            }

        }).start();
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i("TAG", "You clicked number " + adapter.getItem(position) + ", which is at cell position " + position);
        Intent intent = new Intent(MainActivity.this, activity_screen_slide.class);
        intent.putExtra("index", (Serializable) index);
        intent.putExtra("count", index.size());
        intent.putExtra("position", position);
        startActivity(intent);
    }
}