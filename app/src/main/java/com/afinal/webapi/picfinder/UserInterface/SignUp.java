package com.afinal.webapi.picfinder.UserInterface;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afinal.webapi.picfinder.DataRepresentations.SignInSuccess;
import com.afinal.webapi.picfinder.PrimaryTasks.NetworkConstants;
import com.google.gson.GsonBuilder;
import com.afinal.webapi.picfinder.PrimaryTasks.NetworkConnection;
import com.afinal.webapi.picfinder.DataRepresentations.Account;
import com.afinal.webapi.picfinder.R;
import com.google.gson.Gson;

public class SignUp extends Fragment implements View.OnClickListener, AfterTask{
    private View view;
    private EditText editText;
    private Button signUp;
    private TextView error;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.signup, container, false);
        signUp = view.findViewById(R.id.signUpSignUpButton);
        signUp.setOnClickListener(this);
        error = (TextView) view.findViewById(R.id.signUpErrorTextView);
        return view;
    }

    @Override
    public void onClick(View v) {
        eraseErrors();
        Account account = new Account();
        editText = view.findViewById(R.id.signUpEmailEditText);
        account.setEmail(editText.getText().toString());
        editText = view.findViewById(R.id.signUpNameEditText);
        account.setUserName(editText.getText().toString());
        editText = view.findViewById(R.id.signUpPasswordEditText);
        account.setPassword(editText.getText().toString());
        editText = view.findViewById(R.id.signUpPasswordReEnterEditText);
        account.setPasswordCheck(editText.getText().toString());
        String jsonAccount = new Gson().toJson(account, Account.class);
        NetworkConnection connection = new NetworkConnection(getResources().getString(R.string.main_url)+ NetworkConstants.SIGN_UP, jsonAccount, true, getActivity(), this, 0);
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
            Toast.makeText(this.getActivity(),creds.getMessage(),Toast.LENGTH_SHORT).show();
            getFragmentManager().beginTransaction().
                    replace(R.id.fragment, new SignIn(), "").
                    addToBackStack("").
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
