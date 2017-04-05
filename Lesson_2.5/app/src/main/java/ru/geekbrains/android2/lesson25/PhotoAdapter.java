package ru.geekbrains.android2.lesson25;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

// Класс адаптера

class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> implements View.OnClickListener {

    private List<File> mGalleryItems;
    private Context context;

    PhotoAdapter(List<File> mGalleryItems, Context context) {
        this.mGalleryItems = mGalleryItems;
        this.context = context;
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.gallery_item, parent, false);
        return new PhotoHolder(v);
    }

    @Override
    public void onBindViewHolder(PhotoHolder holder, int position) {
        File image = mGalleryItems.get(position);
        if (image.exists()) {
            Bitmap bitmap;
            try {
                bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeStream(new FileInputStream(image)), 375, 375);
                holder.imageItem.setImageBitmap(bitmap);
                holder.imageItem.setOnClickListener(this);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mGalleryItems.size();
    }

    @Override
    public void onClick(View v) {
        ((MainActivity) context).toggle();
    }

}
