package com.playground;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rssclient.controllers.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeTasksFragment extends Fragment {


    public ChangeTasksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_tasks, container, false);
    }


}
