package com.example.alexeyglushkov.wordteacher;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import model.Card;

/**
 * Created by alexeyglushkov on 03.05.16.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.Holder> {
    ArrayList<Card> cards = new ArrayList<>();
    Listener listener;

    public CardAdapter(Listener listener) {
        this.listener = listener;
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("cards", cards);
        return bundle;
    }

    public void onRestoreInstanceState (Parcelable state) {
        Bundle bundle = (Bundle)state;
        cards = bundle.getParcelableArrayList("cards");
        notifyDataSetChanged();
    }

    public void updateCards(List<Card> newCards) {
        cards.clear();
        cards.addAll(newCards);
        notifyDataSetChanged();
    }

    public int getCardIndex(Card card) {
        return cards.indexOf(card);
    }

    public void deleteCardAtIndex(int index) {
        cards.remove(index);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_quizlet_set_card, parent, false);

        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final Card term = cards.get(position);
        holder.nameTextview.setText(term.getTerm());
        holder.wordCountTextView.setText(term.getDefinition());

        bindListener(holder, term);
    }

    private void bindListener(Holder holder, final Card term) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCardClicked(v, term);
            }
        });

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMenuClicked(v, term);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
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
        void onCardClicked(View view, Card card);
        void onMenuClicked(View view, Card card);
    }
}
