package com.example.alexeyglushkov.wordteacher;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by alexeyglushkov on 02.05.16.
 */
public class QuizletSetAdapter extends RecyclerView.Adapter<QuizletSetAdapter.Holder>{
    //ArrayList<QuizletSet> sets;

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;//sets.size();
    }

    public class Holder extends RecyclerView.ViewHolder {


        public Holder(View itemView) {
            super(itemView);
        }
    }
}
