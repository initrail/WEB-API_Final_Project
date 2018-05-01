package com.afinal.webapi.picfinder.DataRepresentations;

public class Credentials {
    private String username;
    private int discoveries;
    private int uploads;
    public Credentials(){

    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getDiscoveries() {
        return discoveries;
    }

    public void setDiscoveries(int discoveries) {
        this.discoveries = discoveries;
    }

    public int getUploads() {
        return uploads;
    }

    public void setUploads(int uploads) {
        this.uploads = uploads;
    }
}
