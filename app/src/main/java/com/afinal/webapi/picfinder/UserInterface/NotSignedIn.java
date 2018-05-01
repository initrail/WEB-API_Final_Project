package com.afinal.webapi.picfinder.UserInterface;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afinal.webapi.picfinder.R;

public class NotSignedIn extends Fragment implements View.OnClickListener{
    private View view;
    private Button signIn;
    private Button signUp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.notsignedin, container, false);
        signIn = view.findViewById(R.id.notSignedInSignInButton);
        signUp = view.findViewById(R.id.notSignedInSignUpButton);
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.notSignedInSignInButton:
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment, new SignIn(), "").
                        addToBackStack("").
                        setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                        commit();
                break;
            case R.id.notSignedInSignUpButton:
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment, new SignUp(), "").
                        addToBackStack("").
                        setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                        commit();
        }
    }
}
