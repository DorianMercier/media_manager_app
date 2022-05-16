package com.dorianmercier.mediamanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;

public class ScreenSlidePageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

        ImageView imageView = (ImageView) rootView.getChildAt(0);
        Bundle args = getArguments();
        if(!args.getBoolean("is_local")) {
            byte[] byte_bitmap = args.getByteArray("bitmap");
            Bitmap bitmap = BitmapFactory.decodeByteArray(byte_bitmap, 0, byte_bitmap.length);

            imageView.setImageBitmap(bitmap);
        }
        else {
            String path = Environment.getExternalStorageDirectory() + "/DCIM/Camera/"+ args.getString("filename");
            imageView.setImageDrawable(Drawable.createFromPath(path));
        }
        return rootView;
    }
}