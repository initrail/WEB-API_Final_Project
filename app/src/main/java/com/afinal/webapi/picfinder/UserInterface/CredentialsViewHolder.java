package com.afinal.webapi.picfinder.UserInterface;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afinal.webapi.picfinder.R;

public class CredentialsViewHolder extends RecyclerView.ViewHolder {
    protected View view;
    protected TextView username;
    protected TextView discoveries;
    public CredentialsViewHolder(View view){
        super(view);
        username = view.findViewById(R.id.credentialsUserName);
        discoveries = view.findViewById(R.id.credentialsDiscoveries);
    }
}
