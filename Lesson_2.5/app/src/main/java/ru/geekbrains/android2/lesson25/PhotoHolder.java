package ru.geekbrains.android2.lesson25;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

// Класс Вьюхолдера для класса PhotoAdapter

class PhotoHolder extends RecyclerView.ViewHolder {

    ImageView imageItem;

    PhotoHolder(View itemView) {
        super(itemView);
        imageItem = (ImageView) itemView.findViewById(R.id.photo_gallery_item);
    }

}
