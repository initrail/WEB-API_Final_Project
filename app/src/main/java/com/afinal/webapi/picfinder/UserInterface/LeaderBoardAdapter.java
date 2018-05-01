package com.afinal.webapi.picfinder.UserInterface;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afinal.webapi.picfinder.DataRepresentations.Credentials;
import com.afinal.webapi.picfinder.R;

import java.util.List;

public class LeaderBoardAdapter extends RecyclerView.Adapter<CredentialsViewHolder> {
    public static final String USER_NAME="username: ";
    public static final String DISCOVERIES = "discoveries: ";
    public static final String UPLOADS = "uploads: ";
    private List<Credentials> winners;
    public LeaderBoardAdapter(List<Credentials> winners){
        this.winners = winners;
    }
    @Override
    public CredentialsViewHolder onCreateViewHolder(ViewGroup group, int i){
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.credentials, group,false);
        CredentialsViewHolder userRank = new CredentialsViewHolder(view);
        return userRank;
    }

    @Override
    public void onBindViewHolder(CredentialsViewHolder view, int i){
        view.username.setText(USER_NAME+winners.get(i).getUsername());
        view.discoveries.setText(DISCOVERIES+winners.get(i).getDiscoveries());
    }
    @Override
    public int getItemCount(){
        return (null!=winners?winners.size():0);
    }
}