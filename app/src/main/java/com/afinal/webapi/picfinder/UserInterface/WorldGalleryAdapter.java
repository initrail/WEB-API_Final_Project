package com.afinal.webapi.picfinder.UserInterface;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afinal.webapi.picfinder.DataRepresentations.WorldGalleryImage;
import com.afinal.webapi.picfinder.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.List;

public class WorldGalleryAdapter extends RecyclerView.Adapter<WorldGalleryImageViewHolder> {
    private List<WorldGalleryImage> images;
    private WorldGallery context;
    public WorldGalleryAdapter(List<WorldGalleryImage> images, WorldGallery context){
        this.images = images;
        this.context = context;
    }
    @Override
    public WorldGalleryImageViewHolder onCreateViewHolder(ViewGroup group, int i){
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.worldgalleryimage, group,false);
        WorldGalleryImageViewHolder worldImage = new WorldGalleryImageViewHolder(view, context);
        return worldImage;
    }

    @Override
    public void onBindViewHolder(WorldGalleryImageViewHolder image, int i){
        Glide.with(context)
                .load(images.get(i).getImg_url())
                .fitCenter()
                .dontAnimate()
                .into(image.worldGalleryImage);
    }
    @Override
    public int getItemCount(){
        return (null!=images?images.size():0);
    }
}