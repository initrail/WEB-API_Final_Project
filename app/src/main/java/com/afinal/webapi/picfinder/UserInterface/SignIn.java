package com.afinal.webapi.picfinder.UserInterface;

import android.app.Fragment;
import android.app.FragmentManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afinal.webapi.picfinder.DataRepresentations.Login;
import com.afinal.webapi.picfinder.DataRepresentations.SignInSuccess;
import com.afinal.webapi.picfinder.PrimaryTasks.NetworkConstants;
import com.afinal.webapi.picfinder.PrimaryTasks.Preferences;
import com.afinal.webapi.picfinder.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.afinal.webapi.picfinder.PrimaryTasks.NetworkConnection;
public class SignIn extends Fragment implements View.OnClickListener, AfterTask{
    private View view;
    private EditText editText;
    private Button signIn;
    private TextView error;
    private Preferences preferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.signin, container, false);
        signIn = view.findViewById(R.id.signInSignInButton);
        signIn.setOnClickListener(this);
        error = view.findViewById(R.id.signInErrorTextView);
        preferences = new Preferences(getActivity());
        return view;
    }

    @Override
    public void onClick(View v) {
        eraseErrors();
        Login login = new Login();
        editText = view.findViewById(R.id.signInEmailEditText);
        login.setUsername(editText.getText().toString());
        editText = view.findViewById(R.id.signInPasswordEditText);
        login.setPassword(editText.getText().toString());
        String loginJson = new Gson().toJson(login, Login.class);
        NetworkConnection connection = new NetworkConnection(getResources().getString(R.string.main_url)+ NetworkConstants.SIGN_IN, loginJson, true, getActivity(), this, 0);
        try {
            connection.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void eraseErrors() {
        error.getLayoutParams().height = 0;
        error.setLayoutParams(error.getLayoutParams());
    }
    @Override
    public void update(NetworkConnection conn, int index){
        SignInSuccess creds = new GsonBuilder().create().fromJson(conn.getReturnData(), SignInSuccess.class);
        if(creds.getSuccess()){
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            preferences.signedIn(creds);
            getFragmentManager().beginTransaction().
                    replace(R.id.fragment, new GuessWhere(), "").
                    setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                    commit();
        } else {
            error.setText(creds.getMessage());
            error.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            error.setLayoutParams(error.getLayoutParams());
        }
    }
    @Override
    public void update(Location loc){
    }
}
