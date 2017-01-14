package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.wordteacher.R;

import junit.framework.Assert;

import java.util.Locale;

import com.example.alexeyglushkov.wordteacher.listmodule.view.BaseListAdaptor;

/**
 * Created by alexeyglushkov on 02.05.16.
 */

// TODO: move methods to the base class
public class QuizletSetAdapter extends BaseListAdaptor<QuizletSetAdapter.Holder, QuizletSet> {
    Listener listener;

    public QuizletSetAdapter(Listener listener) {
        this.listener = listener;
    }

    //// Events

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_quizlet_set_card, parent, false);

        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final QuizletSet set = getItems().get(position);
        holder.nameTextview.setText(set.getTitle());

        String format = holder.itemView.getContext().getResources().getString(R.string.set_word_count_formant);
        String description = String.format(Locale.US, format, set.getTerms().size());
        holder.wordCountTextView.setText(description);

        bindListener(holder, set);
    }

    //// Actions

    @Override
    public void cleanup() {
        super.cleanup();
        listener = null;
    }

    private void bindListener(Holder holder, final QuizletSet set) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSetClicked(v, set);
            }
        });

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMenuClicked(v, set);
            }
        });
    }

    @Override
    public void deleteDataAtIndex(int index) {
        Assert.fail("Not supported");
    }

    public class Holder extends RecyclerView.ViewHolder {
        public View cardView;
        public TextView nameTextview;
        public TextView wordCountTextView;
        public ImageView menuButton;

        public Holder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            nameTextview = (TextView)itemView.findViewById(R.id.name);
            wordCountTextView = (TextView)itemView.findViewById(R.id.wordCount);
            menuButton = (ImageView)itemView.findViewById(R.id.menuButton);
        }
    }

    public interface Listener {
        void onSetClicked(View view, QuizletSet set);
        void onMenuClicked(View view, QuizletSet set);
    }
}
