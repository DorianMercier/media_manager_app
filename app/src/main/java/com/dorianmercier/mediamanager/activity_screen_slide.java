package com.dorianmercier.mediamanager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.dorianmercier.mediamanager.Database.Media;
import com.dorianmercier.mediamanager.background.DataManager;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class activity_screen_slide extends FragmentActivity {

    private Context context;
    private List<Media> index;
    private DataManager dataManager;

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static int NUM_PAGES;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager2 viewPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        Intent intent = getIntent();
        index = (List<Media>) intent.getSerializableExtra("index");
        NUM_PAGES = getIntent().getIntExtra("count", 0);
        dataManager = new DataManager(context);

        setContentView(R.layout.activity_screen_slide);

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(intent.getIntExtra("position", 0), false);
    }


    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            final Bitmap[] bitmap = new Bitmap[1];
            Media current = index.get(position);
            Fragment fragment = new ScreenSlidePageFragment();
            Bundle args = new Bundle();
            if(!current.is_local) {

                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        bitmap[0] = dataManager.getPicture(current);
                        int a = 0;
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                bitmap[0].compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNGs */, blob);

                args.putByteArray("bitmap", blob.toByteArray());
                args.putBoolean("is_local", false);
                fragment.setArguments(args);
            }
            else {
                args.putBoolean("is_local", true);
                args.putString("filename", current.file_name);
                fragment.setArguments(args);
            }
            return fragment;
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}
