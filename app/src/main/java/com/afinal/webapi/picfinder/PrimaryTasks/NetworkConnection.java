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

/*import com.integrail.networkers.primary_operations.storage.LocalFileManager;
import com.integrail.networkers.primary_operations.storage.Preferences;
import com.integrail.networkers.user_interface.AfterTask;
import com.integrail.networkers.user_interface.loading.LoadingScreen;

public class NetworkConnection extends AsyncTask<Void, String, Void> {
    private static final String NO_IMAGE = "noUserImage";
    public static String NOT_CONNECTED_ERROR = "Net Workers requires an internet connection to work properly. Please connect your device to the internet.";
    public static String UNKNOWN_ERROR = "An unknown error has occured, please try again later.";
    private String url;
    private String postData;
    private String returnData;
    private String session;
    private boolean expectingReturn;
    private Context activity;
    private AfterTask fragment;
    private LoadingScreen loading;
    private String error;
    private int index;
    private boolean runningOnUIThread;
    private Preferences preferences;
    private boolean uploadImage;
    private String existingFileName;
    private FileInputStream fileInputStream;
    private boolean expectingImage;
    private String directory;
    private boolean expectingAfterTaskUpdate;
    public NetworkConnection(String url, String postData, boolean expectingReturn, Context activity, AfterTask fragment, int index, long pi) {
        this.url = url;
        error = null;
        this.postData = postData;
        this.expectingAfterTaskUpdate = false;
        this.expectingReturn = expectingReturn;
        this.activity = activity;
        this.fragment = fragment;
        if(activity != null) {
            runningOnUIThread = true;
            preferences = new Preferences(activity);
        } else {
            runningOnUIThread = false;
        }
        if(preferences!=null)
            this.session = preferences.session();
        this.index = index;
    }
    public void openFile(FileInputStream fileInputStream){
        this.fileInputStream = fileInputStream;
        uploadImage = true;
    }
    public void setExpectingImage(boolean expectingImage){
        this.expectingImage = expectingImage;
    }
    public void uploadImage(boolean uploadImage){
        this.uploadImage = uploadImage;
    }
    public void setExistingFileName(String existingFileName){
        this.existingFileName = existingFileName;
        uploadImage = true;
    }
    public void setIndex(int index){
        this.index = index;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public void setSession(String session){
        this.session = session;
    }
    public void setPostData(String postData){
        this.postData = postData;
    }
    public void setExpectingReturn(boolean expectingReturn){
        this.expectingReturn = true;
    }
    public void setFragment(AfterTask task){
        fragment = task;
    }
    public void setActivity(Activity activity){
        this.activity = activity;
    }
    public void setRunningOnUIThread(boolean ui){
        runningOnUIThread = ui;
    }
    @Override
    protected void onPreExecute() {
        if(runningOnUIThread){
            if(activity instanceof Activity) {
                ((Activity) activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FragmentManager fM = ((Activity) activity).getFragmentManager();
                        FragmentTransaction fT = fM.beginTransaction();
                        loading = new LoadingScreen();
                        fT.add(R.id.fragment, loading);
                        fT.commit();
                    }
                });
            }
        }
    }

    @Override
    protected Void doInBackground(Void... args) {
        if (connectedToInternet()) {
            connectToURL();
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

    public void connectToURL() {
        HttpURLConnection conn = null;
        URL url;
        DataOutputStream writer = null;
        BufferedReader br = null;
        byte[] buffer;
        try {
            url = new URL(this.url);
            conn = (HttpURLConnection) url.openConnection();
            if(uploadImage||expectingImage){
                conn.setReadTimeout(10000);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");
            }
            conn.setConnectTimeout(10000);
            conn.setRequestProperty("Cookie", session);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            if ((postData != null) && (!postData.equals(""))&&!uploadImage) {
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json");
                writer = new DataOutputStream(conn.getOutputStream());
                writer.writeBytes(postData);
            }
            if(uploadImage){
                writer = new DataOutputStream(conn.getOutputStream());
                writer.writeBytes("--*****\r\n");
                writer.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + existingFileName +"\"\r\n" );
                writer.writeBytes("\r\n");
                int bytesAvailable = fileInputStream.available();
                int bufferSize = Math.min(bytesAvailable, 5*1024*1024);
                buffer = new byte[bufferSize];
                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while(bytesRead>0){
                    writer.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, 5*1024*1024);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                writer.writeBytes("\r\n");
                writer.writeBytes("--*****--\r\n");
            }
            int response = conn.getResponseCode();
            if (response == 0) {
                error = UNKNOWN_ERROR;
            }
            if (expectingReturn) {
                if(expectingImage){
                    String disposition= conn.getHeaderField("Content-Disposition");
                    int contentLength = conn.getContentLength();
                    String fileName = null;
                    if(disposition!=null){
                        int index = disposition.indexOf("filename=");
                        if(index>0){
                            fileName = disposition.substring(index+9, disposition.length());
                        }
                    }
                    if(fileName!=null) {
                        if (!fileName.equals(NO_IMAGE)) {
                            new LocalFileManager(activity).writeFile(directory + "/" + fileName, conn.getInputStream(), contentLength);
                        }
                    }
                } else {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    returnData = br.readLine();
                }

            }
            session = conn.getHeaderField("Set-Cookie");
        } catch (IOException ex) {
            ex.printStackTrace();
            error = UNKNOWN_ERROR;
        } finally {
            try {
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
        if(returnData!=null){
            System.out.println(returnData);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        if(preferences!=null)
            preferences.setSession(session);
        if(runningOnUIThread)
            removeLoadingScreen();
        //if (fragment != null) {
        if (error == null||expectingAfterTaskUpdate) {
            if(fragment!=null&&activity!=null) {
                if(runningOnUIThread||expectingAfterTaskUpdate)
                    fragment.update(this, index);
            }
        } else {
            if(runningOnUIThread){
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(error);
                builder.setCancelable(false);
                builder.setTitle("Net Work Error!");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
                if (error == NOT_CONNECTED_ERROR) {
                    builder.setNegativeButton("Settings",   new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((Activity)activity).startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    });
                }
                AlertDialog alert = builder.create();
                alert.show();
            }
            //}
        }
    }
    public void setExpectingAfterTaskUpdate(boolean expectingAfterTaskUpdate){
        this.expectingAfterTaskUpdate = expectingAfterTaskUpdate;
    }
    public void setDirectory(String directory){
        this.directory = directory;
    }
    public void removeLoadingScreen() {
        FragmentManager fM = ((Activity)activity).getFragmentManager();
        FragmentTransaction fT = fM.beginTransaction();
        fT.remove(loading);
        fT.commit();
    }
    public String getError(){
        return error;
    }
    public String getReturnData() {
        return returnData;
    }

    public String getSession() {
        return session;
    }
}
*/
