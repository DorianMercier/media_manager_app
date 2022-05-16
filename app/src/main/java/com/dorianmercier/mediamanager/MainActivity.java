package com.dorianmercier.mediamanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.dorianmercier.mediamanager.Database.AppDatabase;
import com.dorianmercier.mediamanager.Database.Media;
import com.dorianmercier.mediamanager.Database.MediaDAO;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.R)
public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    AppDatabase db;
    MediaDAO mediaDAO;
    List<Media> index;
    MyRecyclerViewAdapter adapter;
    Context context;
    AppCompatActivity activity;

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

        if (!Utils.checkAllPermissions(this, permissions)) {
            requestPermissions(permissions, 0);
        }

        context = getApplicationContext();

        db = Room.databaseBuilder(context, AppDatabase.class, "MediaManagerDatabase").build();
        mediaDAO = db.mediaDAO();

        reload(this);
        activity = this;
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

    private void reload(MyRecyclerViewAdapter.ItemClickListener listener) {
        new Thread(new Runnable() {
            public void run() {
                Log.d("Thread status", "begin");
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
                    }
                });

                /*
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

                */

                Log.d("Thread status", "end");
            }

        }).start();
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i("TAG", "You clicked number " + adapter.getItem(position) + ", which is at cell position " + position);
    }
}