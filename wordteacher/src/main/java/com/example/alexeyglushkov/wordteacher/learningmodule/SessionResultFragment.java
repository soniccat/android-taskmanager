package com.example.alexeyglushkov.wordteacher.learningmodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.wordteacher.R;

import com.example.alexeyglushkov.wordteacher.model.LearnSession;

/**
 * Created by alexeyglushkov on 15.05.16.
 */
public class SessionResultFragment extends Fragment {
    private LearnSession session;
    private RecyclerView recyclerView;
    private SessionResultAdapter adapter;

    public void setSession(LearnSession session) {
        this.session = session;
        updateAdapter();
    }

    private void updateAdapter() {
        adapter = new SessionResultAdapter();
        adapter.setSession(session);
        recyclerView.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
    }
}
