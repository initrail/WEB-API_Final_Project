package com.afinal.webapi.picfinder.UserInterface;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afinal.webapi.picfinder.DataRepresentations.Credentials;
import com.afinal.webapi.picfinder.DataRepresentations.GuessCoords;
import com.afinal.webapi.picfinder.DataRepresentations.SignInSuccess;
import com.afinal.webapi.picfinder.DataRepresentations.WorldGalleryImage;
import com.afinal.webapi.picfinder.MainActivity;
import com.afinal.webapi.picfinder.PrimaryTasks.GetLocation;
import com.afinal.webapi.picfinder.PrimaryTasks.NetworkConnection;
import com.afinal.webapi.picfinder.PrimaryTasks.NetworkConstants;
import com.afinal.webapi.picfinder.PrimaryTasks.Preferences;
import com.afinal.webapi.picfinder.R;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;

public class GuessImage extends Fragment implements View.OnClickListener, AfterTask {
    private View view;
    private WorldGalleryImage image;
    private ImageView guessImage;
    private GuessCoords coords;
    private Button guessButton;
    private boolean success;
    private LocationManager manager;
    private Preferences preferences;
    private TextView message;
    private ProgressDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.guessimage, container, false);
        coords = new GuessCoords();
        coords.setLatA(image.getLatitude());
        coords.setLongA(image.getLongitude());
        guessImage = (ImageView) view.findViewById(R.id.guessImageImage);
        preferences = new Preferences(getActivity());
        message = (TextView) view.findViewById(R.id.guessImageTextView);
        Glide.with(this)
                .load(image.getImg_url())
                .fitCenter()
                .dontAnimate()
                .into(guessImage);
        guessButton = (Button) view.findViewById(R.id.guessImageGuessButton);
        guessButton.setOnClickListener(this);
        success = false;
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
        if (!success) {
            if (((MainActivity) getActivity()).hasPermissions()) {
                if (locationIsOn()) {
                    if(connectedToInternet())
                        getLocation();
                    else
                        notConnected();
                }
            } else {
                showPermissionsAlert();
            }
        } else {
            NetworkConnection connection = new NetworkConnection(getResources().getString(R.string.main_url) + NetworkConstants.LEADER_BOARDS, "", true, getActivity(), this, 1);
            connection.setMethod(NetworkConstants.GET);
            try {
                connection.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setImage(WorldGalleryImage image) {
        this.image = image;
    }

    @Override
    public void update(NetworkConnection connection, int index) {
        if (index == 0) {
            SignInSuccess creds = new GsonBuilder().create().fromJson(connection.getReturnData(), SignInSuccess.class);
            if (creds.getSuccess()) {
                success = true;
                guessButton.setText("View leader board.");
                int dis = preferences.getCreds().getDiscoveries();
                dis++;
                preferences.setDiscoveries(dis);
            } else {
                guessButton.setText("Guess again?");
            }
            message.setText(creds.getMessage());
        } else if (index == 1) {
            Credentials[] ranks = new GsonBuilder().create().fromJson(connection.getReturnData(), Credentials[].class);
            LeaderBoard boards = new LeaderBoard();
            boards.winnersA(ranks);
            getFragmentManager().beginTransaction().
                    replace(R.id.fragment, boards, "").
                    addToBackStack("").
                    setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                    commit();
        }
    }

    @Override
    public void update(Location location) {
        coords.setLatB(location.getLatitude());
        coords.setLongB(location.getLongitude());
        coords.setToken(preferences.token());
        String coordsJson = new Gson().toJson(coords, GuessCoords.class);
        if (mDialog != null)
            mDialog.dismiss();
        NetworkConnection connection = new NetworkConnection(getActivity().getResources().getString(R.string.main_url) + NetworkConstants.GPS_COMPARE, coordsJson, true, getActivity(), this, 0);
        try {
            connection.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getLocation() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            GetLocation location = new GetLocation(manager, this);
            mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage("Please wait...");
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
            if (lastKnownLocation != null && lastKnownLocation.getTime() > Calendar.getInstance().getTimeInMillis() - 15 * 1000) {
                update(lastKnownLocation);
            } else {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, location);
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, location);
            }
        }
    }

    public boolean locationIsOn() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("Enable location?");
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = false;
        network_enabled = false;
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            return false;
        } else {
            return true;
        }
    }

    public void showPermissionsAlert() {
        ((MainActivity) getActivity()).requestPermissions();
        if (!((MainActivity) getActivity()).hasPermissions()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getActivity().getResources().getString(R.string.app_name) + " requires storage, location and camera permissions to be used. If you have not granted " + getActivity().getResources().getString(R.string.app_name) + " these permissions please go to settings and grant them.");
            builder.setCancelable(false);
            builder.setTitle("Permissions needed!");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
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
    }
}
