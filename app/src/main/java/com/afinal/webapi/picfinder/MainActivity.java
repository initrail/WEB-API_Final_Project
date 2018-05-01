package com.afinal.webapi.picfinder;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.afinal.webapi.picfinder.PrimaryTasks.Preferences;
import com.afinal.webapi.picfinder.UserInterface.GuessWhere;
import com.afinal.webapi.picfinder.UserInterface.NotSignedIn;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 42;
    private boolean mHasCameraPermissions;
    private boolean mHasLocationPermissions;
    private boolean mHasWriteExternalStoragePermissions;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initializeCustomActionBar();
        setContentView(R.layout.main);
        Preferences preferences = new Preferences(this);
        FragmentManager fM = getFragmentManager();
        FragmentTransaction fT = fM.beginTransaction();
        mHasCameraPermissions = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        mHasLocationPermissions = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        mHasWriteExternalStoragePermissions = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if(preferences.signedIn()){
            getFragmentManager().beginTransaction().
                    replace(R.id.fragment, new GuessWhere(), "").
                    setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                    commit();
        } else {
            Fragment notSignedIn = new NotSignedIn();
            fT.replace(R.id.fragment, notSignedIn);
            fT.commit();
        }
    }
    public void initializeCustomActionBar(){
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    public void requestPermissions() {
        if (mHasCameraPermissions && mHasLocationPermissions && mHasWriteExternalStoragePermissions) {
            return;
        }
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE }, PERMISSION_REQUEST_CODE);
    }

    public boolean hasPermissions(){
        return mHasCameraPermissions && mHasLocationPermissions && mHasWriteExternalStoragePermissions;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; ++i) {
            if (permissions[i].equals(Manifest.permission.CAMERA)) {
                mHasCameraPermissions = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            } else if(permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                mHasLocationPermissions = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            } else if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                mHasWriteExternalStoragePermissions = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
    }
}
