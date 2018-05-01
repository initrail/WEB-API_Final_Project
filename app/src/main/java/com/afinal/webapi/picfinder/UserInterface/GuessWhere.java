package com.afinal.webapi.picfinder.UserInterface;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afinal.webapi.picfinder.DataRepresentations.Credentials;
import com.afinal.webapi.picfinder.DataRepresentations.WorldGalleryImage;
import com.afinal.webapi.picfinder.MainActivity;
import com.afinal.webapi.picfinder.PrimaryTasks.NetworkConnection;
import com.afinal.webapi.picfinder.PrimaryTasks.NetworkConstants;
import com.afinal.webapi.picfinder.R;
import com.google.gson.GsonBuilder;

public class GuessWhere extends Fragment implements View.OnClickListener, AfterTask{
    private View view;
    private Button profile;
    private Button leaderBoards;
    private Button worldView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.guesswhere, container, false);
        profile = view.findViewById(R.id.guessWhereProfileButton);
        profile.setOnClickListener(this);
        leaderBoards = view.findViewById(R.id.guessWhereLeaderBoardsButton);
        leaderBoards.setOnClickListener(this);
        worldView = view.findViewById(R.id.guessWhereWorldGalleryButton);
        worldView.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.guessWhereProfileButton){
            getFragmentManager().beginTransaction().
                    replace(R.id.fragment, new ProfileDetails(), "").
                    addToBackStack("").
                    setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                    commit();
        } else if(v.getId() == R.id.guessWhereLeaderBoardsButton) {
            NetworkConnection connection = new NetworkConnection(getResources().getString(R.string.main_url)+ NetworkConstants.LEADER_BOARDS, "", true, getActivity(), this, 1);
            connection.setMethod(NetworkConstants.GET);
            try {
                connection.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(v.getId()== R.id.guessWhereWorldGalleryButton) {
            NetworkConnection connection = new NetworkConnection(getResources().getString(R.string.main_url)+ NetworkConstants.WORLD_PHOTO, "", true, getActivity(), this, 0);
            connection.setMethod(NetworkConstants.GET);
            try {
                connection.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void update(NetworkConnection connection, int index){
        if(index == 0) {
            WorldGalleryImage[] wImages = new GsonBuilder().create().fromJson(connection.getReturnData(), WorldGalleryImage[].class);
            WorldGallery boards = new WorldGallery();
            boards.setList(wImages);
            getFragmentManager().beginTransaction().
                    replace(R.id.fragment, boards, "").
                    addToBackStack("").
                    setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                    commit();
        } else if(index == 1){
            Credentials[] ranks = new GsonBuilder().create().fromJson(connection.getReturnData(), Credentials[].class);
            LeaderBoard boards = new LeaderBoard();
            boards.winnersA(ranks);
            getFragmentManager().beginTransaction().
                    replace(R.id.fragment, boards, "").
                    addToBackStack("").
                    setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                    commit();
        }
    }
    @Override
    public void update(Location location){
    }
    public void showPermissionsAlert(){
        ((MainActivity) getActivity()).requestPermissions();
        if(!((MainActivity)getActivity()).hasPermissions()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getActivity().getResources().getString(R.string.app_name)+" requires storage, location and camera permissions to be used. If you have not granted "+getActivity().getResources().getString(R.string.app_name)+" these permissions please go to settings and grant them.");
            builder.setCancelable(false);
            builder.setTitle("Permissions needed!");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            builder.setNegativeButton("Settings",   new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    (getActivity()).startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
