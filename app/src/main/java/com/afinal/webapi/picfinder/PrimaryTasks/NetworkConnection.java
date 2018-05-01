package com.afinal.webapi.picfinder.PrimaryTasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.afinal.webapi.picfinder.PrimaryTasks.Preferences;
import com.afinal.webapi.picfinder.R;
import com.afinal.webapi.picfinder.UserInterface.AfterTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by integrailwork on 5/19/17.
 */


public class  NetworkConnection extends AsyncTask<Void, String, Void> {
    private String url;
    private String data;
    private boolean returns;
    private Context activity;
    private String token;
    private String returnData;
    private AfterTask fragment;
    private int index;
    private int status;
    private String method;
    private String filename;
    private String attachmentName = "avatar";
    private String crlf = "\r\n";
    private String twoHyphens = "--";
    private String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
    private boolean uploadPhoto;
    private ProgressDialog mDialog;
    private int size;
    public final static int TIMEOUT = 30000;
    public final static String NOT_CONNECTED_ERROR = "Not connected to the internet";
    private String error;
    public NetworkConnection(String url, String data, boolean returns, Context activity, AfterTask fragment, int index){
        method = NetworkConstants.POST;
        this.url = url;
        this.data = data;
        this.returns = returns;
        this.activity = activity;
        Preferences pref = new Preferences(activity);
        token = pref.token();
        this.index = index;
        this.fragment = fragment;
        mDialog = null;
        error = "";
    }
    public void setFilename(String filename){
        this.filename = filename;
        uploadPhoto = true;
    }
    public int status(){
        return status;
    }
    public String getReturnData(){
        return returnData;
    }
    @Override
    protected Void doInBackground(Void... args){
        if (connectedToInternet()) {
            connectToUrl();
        } else {
            error = NOT_CONNECTED_ERROR;
        }
        return null;
    }
    private boolean connectedToInternet() {
        if (activity != null) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return true;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(activity!=null) {
            mDialog = new ProgressDialog(activity);
            mDialog.setMessage("Please wait...");
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }
    }
    @Override
    protected void onPostExecute(Void res){
        if(mDialog!=null)
            mDialog.dismiss();
        if(returns)
            if(activity!=null) {
                if (!error.equals(NOT_CONNECTED_ERROR)) {
                    fragment.update(this, index);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage(activity.getResources().getString(R.string.app_name) + " requires an internet connection to work, please turn on data or wifi to use this app.");
                    builder.setCancelable(false);
                    builder.setTitle("Network error!");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
                    if (error == NOT_CONNECTED_ERROR) {
                        builder.setNegativeButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((Activity) activity).startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                            }
                        });
                    }
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
    }
    public void setMethod(String method){
        this.method = method;
    }
    private void connectToUrl(){
        HttpsURLConnection conn = null;
        URL url;
        DataOutputStream writer = null;
        BufferedReader br = null;
        byte[] buffer;
        FileInputStream fileInputStream = null;
        try {
            url = new URL(this.url);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(TIMEOUT);
            conn.setRequestProperty(NetworkConstants.TOKEN_ACCESS, token);
            conn.setRequestMethod(method);
            if(!method.equals(NetworkConstants.GET)) {
                conn.setDoOutput(true);
                if(uploadPhoto){
                    conn.setUseCaches(false);
                    conn.setReadTimeout(TIMEOUT);
                    conn.setDoInput(true);
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
                    writer = new DataOutputStream(conn.getOutputStream());
                    writer.writeBytes(twoHyphens + boundary + crlf);
                    String [] pathDivy = filename.split("/");
                    writer.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" + pathDivy[pathDivy.length-1] + "\"" + crlf);
                    writer.writeBytes(crlf);
                    fileInputStream = new FileInputStream(filename);
                    size = fileInputStream.available();
                    int bufferSize = Math.min(size, 5*1024*1024);
                    buffer = new byte[bufferSize];
                    // Read from FileInputStream and write to OutputStream
                    if (filename != null) {
                        int res = 1;
                        while ((res = fileInputStream.read(buffer)) > 0) {
                            writer.write(buffer, 0, res);
                        }
                    }
                    writer.writeBytes("\r\n");
                    writer.writeBytes("--" + boundary + "--\r\n");
                } else {
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-Type", "application/json");
                    writer = new DataOutputStream(conn.getOutputStream());
                    writer.writeBytes(data);
                }
            }
            status = conn.getResponseCode();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            returnData = br.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
            //error = UNKNOWN_ERROR;
        } finally {
            System.out.println(returnData);
            try {
                if(fileInputStream!=null)
                    fileInputStream.close();
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
                if (br != null)
                    br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            conn.disconnect();
        }
    }
}
