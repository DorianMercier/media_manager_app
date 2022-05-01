package com.dorianmercier.mediamanager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dorianmercier.mediamanager.Database.Media;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private final List<Media> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final Context context;
    int size;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<Media> data, int size) {
        Log.d("MyRecyclerViewAdapter constructor", "begin");
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.size = size;
        Log.d("MyRecyclerViewAdapter constructor", "begin");
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("onCreateViewHolder status", "begin");
        View view = mInflater.inflate(R.layout.recyclerview_image, parent, false);
        Log.d("onCreateViewHolder status", "view inflated");
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.myTextView.setText(mData[position]);
        Log.d("onBindViewHolder status", "begin");
        holder.myImageView.setImageResource(R.drawable.unloadedimage);
        Log.d("onBindViewHolder status", "icon set");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size/4, size/4);
        Log.d("onBindViewHolder status", "size: " + size);
        Log.d("onBindViewHolder status", "LayoutParams created");
        holder.myImageView.setLayoutParams(layoutParams);
        Log.d("onBindViewHolder status", "LayoutParam set");
        Log.d("onBindViewHolder status", "end");
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
            Log.d("Constructor ViewHolder status", "begin");
            myImageView = itemView.findViewById(R.id.icon_image);
            itemView.setOnClickListener(this);
            Log.d("Constructor ViewHolder status", "end");
        }

        @Override
        public void onClick(View view) {
            Log.d("onClick ViewHolder status", "begin");
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            Log.d("onClick ViewHolder status", "end");
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