package com.afinal.webapi.picfinder.UserInterface;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.afinal.webapi.picfinder.R;

public class WorldGalleryImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    protected View view;
    protected ImageView worldGalleryImage;
    protected WorldGallery context;
    public WorldGalleryImageViewHolder(View view, WorldGallery context){
        super(view);
        this.context = context;
        worldGalleryImage = (ImageView) view.findViewById(R.id.worldGalleryImage);
        view.setOnClickListener(this);
    }
    @Override
    public void onClick(View view){
        int index = getAdapterPosition();
        context.showGuessImage(index);
    }
}
