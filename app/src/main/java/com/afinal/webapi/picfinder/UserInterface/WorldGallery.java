package com.afinal.webapi.picfinder.UserInterface;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afinal.webapi.picfinder.DataRepresentations.WorldGalleryImage;
import com.afinal.webapi.picfinder.PrimaryTasks.NetworkConnection;
import com.afinal.webapi.picfinder.PrimaryTasks.NetworkConstants;
import com.afinal.webapi.picfinder.R;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldGallery extends Fragment implements View.OnClickListener, AfterTask{
    private View view;
    private List<WorldGalleryImage> images;
    private WorldGalleryImage[] imagesA;
    private RecyclerView mRecyclerView;
    private WorldGalleryAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.worldgallery, container, false);
        images = new ArrayList<>(Arrays.asList(imagesA));
        mRecyclerView = (RecyclerView) view.findViewById(R.id.worldGalleryRecyclerView);
        adapter = new WorldGalleryAdapter(images, this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
    public void setList(WorldGalleryImage[] images){
        this.imagesA = images;
    }
    @Override
    public void onClick(View v) {

    }
    @Override
    public void update(NetworkConnection connection, int index){

    }
    @Override
    public void update(Location location){
    }
    public void showGuessImage(int index){
        WorldGalleryImage sImage = images.get(index);
        GuessImage guess = new GuessImage();
        guess.setImage(sImage);
        getFragmentManager().beginTransaction().
                replace(R.id.fragment, guess, "").
                addToBackStack("").
                setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                commit();
    }
}
