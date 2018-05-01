package com.afinal.webapi.picfinder.UserInterface;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afinal.webapi.picfinder.DataRepresentations.Credentials;
import com.afinal.webapi.picfinder.DataRepresentations.SignInSuccess;
import com.afinal.webapi.picfinder.DataRepresentations.UserCoordinates;
import com.afinal.webapi.picfinder.MainActivity;
import com.afinal.webapi.picfinder.PrimaryTasks.GetLocation;
import com.afinal.webapi.picfinder.PrimaryTasks.NetworkConnection;
import com.afinal.webapi.picfinder.PrimaryTasks.NetworkConstants;
import com.afinal.webapi.picfinder.PrimaryTasks.Preferences;
import com.afinal.webapi.picfinder.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import static android.app.Activity.RESULT_OK;

public class ProfileDetails extends Fragment implements View.OnClickListener, AfterTask {
    private View view;
    private TextView textView;
    private Button uploadChallenge;
    private Button signOut;
    private Preferences preferences;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private LocationManager manager;
    private String mCurrentPhotoPath;
    private UserCoordinates coords;
    private ProgressDialog mDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profiledetails, container, false);
        preferences = new Preferences(getActivity());
        Credentials creds = preferences.getCreds();
        textView = view.findViewById(R.id.profileDetailsUserNameTextView);
        textView.setText("User name: " + creds.getUsername());
        textView = view.findViewById(R.id.profileDetailsDiscoveriesTextView);
        textView.setText("Discoveries: " + creds.getDiscoveries());
        textView = view.findViewById(R.id.profileDetailsUploadsTextView);
        textView.setText("Uploads: " + creds.getUploads());
        uploadChallenge = view.findViewById(R.id.profileDetailsUploadChallengeButton);
        uploadChallenge.setOnClickListener(this);
        signOut = view.findViewById(R.id.profileDetailsSignOutButton);
        signOut.setOnClickListener(this);
        return view;
    }
    public void notConnected() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getResources().getString(R.string.app_name) + " requires an internet connection to work, please turn on data or wifi to use this app.");
        builder.setCancelable(false);
        builder.setTitle("Network error!");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do things
            }
        });
        builder.setNegativeButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                (getActivity()).startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean connectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profileDetailsUploadChallengeButton){
            if(((MainActivity)getActivity()).hasPermissions()) {
                if(locationIsOn()) {
                    if(connectedToInternet())
                        getLocation();
                    else
                        notConnected();
                }
            } else{
                showPermissionsAlert();
            }
        }
        else if (v.getId() == R.id.profileDetailsSignOutButton) {
            signOut();
        }
    }
    public void signOut(){
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        preferences.signOut();
        Toast.makeText(getActivity(), "User signed out.", Toast.LENGTH_SHORT).show();
        getFragmentManager().beginTransaction().
                replace(R.id.fragment, new NotSignedIn(), "").
                setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                commit();
    }
    public void getLocation() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            GetLocation location = new GetLocation(manager, this);
            if (lastKnownLocation != null && lastKnownLocation.getTime() > Calendar.getInstance().getTimeInMillis() - 15 * 1000) {
                update(lastKnownLocation);
            } else {
                mDialog = new ProgressDialog(getActivity());
                mDialog.setMessage("Please wait...");
                mDialog.setCancelable(false);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, location);
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, location);
            }
        }
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
    public boolean locationIsOn(){
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("Enable location?");
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            });
            dialog.show();
        }
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = false;
        network_enabled = false;
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            return false;
        } else{
            return true;
        }
    }
    @Override
    public void update(NetworkConnection conn, int index){
        String result = conn.getReturnData();
        System.out.println(result);
        SignInSuccess success =  new GsonBuilder().create().fromJson(conn.getReturnData(), SignInSuccess.class);
        if(success!=null) {
            if (conn.status() == HttpsURLConnection.HTTP_FORBIDDEN)
                signOut();
            if (success.getSuccess()) {
                int uploads = preferences.uploads();
                uploads++;
                preferences.setUploads(uploads);
                textView = view.findViewById(R.id.profileDetailsUploadsTextView);
                textView.setText("Uploads: " + preferences.uploads());
            }
        } else {

            Toast.makeText(this.getActivity(),"Unknown error occurred!",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void update(Location location){
        coords = new UserCoordinates();
        coords.setpLatitude(location.getLatitude());
        coords.setpLongitude(location.getLongitude());
        coords.setUsername(preferences.getCreds().getUsername());
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),"com.afinal.webapi.picfinder.filepath", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(mDialog!=null)
            mDialog.dismiss();
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            File f = new File(mCurrentPhotoPath);
            galleryAddPic(f);
            NetworkConnection connection = new NetworkConnection(
                    getActivity().getResources().getString(R.string.main_url)
                            + NetworkConstants.PHOTO_UPLOAD+"?username="+coords.getUsername()+"&pLongitude="+coords.getpLongitude()+"&pLatitude="+coords.getpLatitude()
                    , "",true,getActivity(), this, 0);
            connection.setFilename(mCurrentPhotoPath);
            try{
                connection.execute();
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void galleryAddPic(File f) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }
}
