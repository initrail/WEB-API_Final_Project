package com.afinal.webapi.picfinder.PrimaryTasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.afinal.webapi.picfinder.DataRepresentations.Credentials;
import com.afinal.webapi.picfinder.DataRepresentations.SignInSuccess;

public class Preferences {
    public final static String TOKEN = "token";
    public final static String SIGNED_IN = "signedIn";
    public final static String USER_NAME = "username";
    public final static String DISCOVERIES = "discoveries";
    public final static String UPLOADS = "uploads";
    public final static String STORAGE_NAME = "preferences";
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;

    public Preferences(Activity activity) {
        pref = activity.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        edit = pref.edit();
    }
    public Preferences(Context context) {
        pref = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        edit = pref.edit();
    }
    public int discoveries(){
        return pref.getInt(DISCOVERIES, 0);
    }
    public int uploads(){
        return pref.getInt(UPLOADS, 0);
    }
    public void setDiscoveries(int discoveries){
        edit.putInt(DISCOVERIES, discoveries);
        edit.commit();
    }
    public void setUploads(int uploads){
        edit.putInt(UPLOADS, uploads);
        edit.commit();
    }
    public boolean signedIn() {
        return pref.getBoolean(SIGNED_IN, false);
    }

    public String token() {
        return pref.getString(TOKEN, null);
    }

    public Credentials getCreds() {
        Credentials creds = new Credentials();
        creds.setUsername(pref.getString(USER_NAME, null));
        creds.setDiscoveries(pref.getInt(DISCOVERIES, 0));
        creds.setUploads(pref.getInt(UPLOADS, 0));
        return creds;
    }

    public void signOut() {
        edit.putString(TOKEN, null);
        edit.putBoolean(SIGNED_IN, false);
        edit.putString(USER_NAME, null);
        edit.putInt(DISCOVERIES, 0);
        edit.putInt(UPLOADS, 0);
        edit.commit();
    }

    public void signedIn(SignInSuccess success) {
        edit.putString(TOKEN, success.getToken());
        edit.putBoolean(SIGNED_IN, success.getSuccess());
        edit.putString(USER_NAME, success.getUser().getUsername());
        edit.putInt(DISCOVERIES, success.getUser().getDiscoveries());
        edit.putInt(UPLOADS, success.getUser().getUploads());
        edit.commit();
    }
}
