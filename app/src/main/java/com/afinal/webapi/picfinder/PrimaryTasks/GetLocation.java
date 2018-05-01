package com.afinal.webapi.picfinder.PrimaryTasks;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.afinal.webapi.picfinder.UserInterface.AfterTask;

public class GetLocation implements LocationListener {
    private LocationManager manager;
    private AfterTask task;
    public GetLocation(LocationManager manager, AfterTask task){
        this.manager = manager;
        this.task = task;
    }
    @Override
    public void onLocationChanged(Location location) {
        manager.removeUpdates(this);
        task.update(location);
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
    }
}
