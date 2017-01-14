package com.example.alexeyglushkov.wordteacher.courselistmodules.cardlistmodule.view;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexeyglushkov.wordteacher.R;

import com.example.alexeyglushkov.wordteacher.listmodule.view.BaseListAdaptor;
import tools.DeleteTouchHelper;
import com.example.alexeyglushkov.wordteacher.model.Card;

/**
 * Created by alexeyglushkov on 03.05.16.
 */
public class CardListAdapter extends BaseListAdaptor<CardListAdapter.Holder, Card> implements DeleteTouchHelper.Listener {
    private Listener listener;
    private ItemTouchHelper deleteTouchHelper;

    //// Initialization

    public CardListAdapter(Listener listener) {
        this.listener = listener;
        this.deleteTouchHelper = new ItemTouchHelper(new DeleteTouchHelper(this));
    }

    //// Events

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        deleteTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_quizlet_set_card, parent, false);

        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final Card term = getItems().get(position);
        holder.nameTextview.setText(term.getTerm());
        holder.wordCountTextView.setText(term.getDefinition());

        bindListener(holder, term);
    }

    //// Actions

    @Override
    public void cleanup() {
        super.cleanup();
        listener = null;
        deleteTouchHelper = null;
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

    public void onItemDeleted(RecyclerView.ViewHolder holder, int index, int position) {
        Card course = getItems().get(index);
        listener.onCardViewDeleted(holder.itemView, course);
        deleteDataAtIndex(index);
        notifyItemRemoved(position);
    }

    class Holder extends RecyclerView.ViewHolder {
        private View cardView;
        private TextView nameTextview;
        private TextView wordCountTextView;
        private ImageView menuButton;

        private Holder(View itemView) {
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
        void onCardViewDeleted(View view, Card course);
    }
}
