package com.mad41.tripreminder;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HistoryNotes extends Fragment {


    public HistoryNotes() {
        // Required empty public constructor
    }

    public static HistoryNotes newInstance() {
        HistoryNotes fragment = new HistoryNotes();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_notes, container, false);
    }
}