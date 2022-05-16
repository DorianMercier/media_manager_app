package com.dorianmercier.mediamanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dorianmercier.mediamanager.Database.AppDatabase;
import com.dorianmercier.mediamanager.Database.Media;
import com.dorianmercier.mediamanager.background.DataManager;
import com.google.android.gms.common.internal.constants.ListAppsActivityContract;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private final List<Media> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    int size;
    DataManager dataManager;
    AppCompatActivity activity;
    LinearLayout.LayoutParams layoutParams;
    Context context;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<Media> data, int size, AppCompatActivity activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.size = size;
        dataManager = new DataManager(context);
        this.activity = activity;
        layoutParams = new LinearLayout.LayoutParams(size/4, size/4);
        this.context = context;

    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_image, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.myTextView.setText(mData[position]);
        holder.myImageView.setLayoutParams(layoutParams);
        Glide.with(context).load(R.drawable.unloadedimage).placeholder(R.drawable.unloadedimage).into(holder.myImageView);
        int instant_position = position;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Media media = mData.get(instant_position);

                if(media.is_local) {
                    activity.runOnUiThread(new Runnable() {
                    public void run() {
                            Glide.with(context)
                                    .load(Environment.getExternalStorageDirectory() + "/DCIM/Camera/"+ media.file_name)
                                    .apply(new RequestOptions().override(size/4).centerCrop())
                                    .into(holder.myImageView);
                    }
                    });
                }
                else {
                    Bitmap bitmap = dataManager.load_icon(mData.get(instant_position), size/4);
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if(bitmap != null) {
                                Glide.with(context).load(bitmap).into(holder.myImageView);
                            }
                        }
                    });
                }
                }
        }).start();
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.icon_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Media getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}