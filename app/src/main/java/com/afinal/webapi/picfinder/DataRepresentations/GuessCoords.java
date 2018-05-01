package com.afinal.webapi.picfinder.DataRepresentations;

public class GuessCoords {
    private double longA;
    private double latA;
    private double longB;
    private double latB;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public GuessCoords(){

    }
    public double getLongA() {
        return longA;
    }

    public void setLongA(double longA) {
        this.longA = longA;
    }

    public double getLatA() {
        return latA;
    }

    public void setLatA(double latA) {
        this.latA = latA;
    }

    public double getLongB() {
        return longB;
    }

    public void setLongB(double longB) {
        this.longB = longB;
    }

    public double getLatB() {
        return latB;
    }

    public void setLatB(double latB) {
        this.latB = latB;
    }
}
