package com.afinal.webapi.picfinder.DataRepresentations;

public class UserCoordinates {
    //username: string pLongitude: int pLatitude: in
    private String username;
    private double pLongitude;
    private double pLatitude;
    public UserCoordinates(){

    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getpLongitude() {
        return pLongitude;
    }

    public void setpLongitude(double pLongitude) {
        this.pLongitude = pLongitude;
    }

    public double getpLatitude() {
        return pLatitude;
    }

    public void setpLatitude(double pLatitude) {
        this.pLatitude = pLatitude;
    }
}
