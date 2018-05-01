package com.afinal.webapi.picfinder.UserInterface;

import android.location.Location;

import com.afinal.webapi.picfinder.PrimaryTasks.NetworkConnection;

public interface AfterTask {
    void update(NetworkConnection connection, int index);
    void update(Location location);
}
