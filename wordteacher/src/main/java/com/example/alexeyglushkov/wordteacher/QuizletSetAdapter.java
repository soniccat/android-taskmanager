package com.example.alexeyglushkov.wordteacher;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by alexeyglushkov on 02.05.16.
 */
public class QuizletSetAdapter extends RecyclerView.Adapter<QuizletSetAdapter.Holder>{
    ArrayList<QuizletSet> sets = new ArrayList<>();

    public List<QuizletSet> getSets() {
        return sets;
    }

    public void updateSets(List<QuizletSet> aSet) {
        sets = new ArrayList<QuizletSet>();
        sets.addAll(aSet);
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_quizlet_set_card, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        QuizletSet set = sets.get(position);
        holder.nameTextview.setText(set.getTitle());

        String format = holder.itemView.getContext().getResources().getString(R.string.set_word_count_formant);
        String description = String.format(Locale.US, format, set.getTerms().size());
        holder.wordCountTextView.setText(description);
    }

    @Override
    public int getItemCount() {
        return sets.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public TextView nameTextview;
        public TextView wordCountTextView;

        public Holder(View itemView) {
            super(itemView);
            nameTextview = (TextView)itemView.findViewById(R.id.name);
            wordCountTextView = (TextView)itemView.findViewById(R.id.wordCount);
        }
    }
}
