package com.afinal.webapi.picfinder.UserInterface;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afinal.webapi.picfinder.DataRepresentations.Credentials;
import com.afinal.webapi.picfinder.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeaderBoard extends Fragment {
    private View view;
    private List<Credentials> winners;
    private Credentials[] winnersA;
    private RecyclerView mRecyclerView;
    private LeaderBoardAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.leaderboards, container, false);
        winners = new ArrayList<>(Arrays.asList(winnersA));
        mRecyclerView = (RecyclerView) view.findViewById(R.id.leaderBoardRecyclerView);
        adapter = new LeaderBoardAdapter(winners);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
    public void winnersA(Credentials[] rank){
        winnersA = rank;
    }
}
