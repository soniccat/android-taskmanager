package com.example.alexeyglushkov.wordteacher.tools;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

/**
 * Created by alexeyglushkov on 13.06.16.
 */
public class DeleteTouchHelper extends ItemTouchHelper.Callback {
    private RecyclerView recyclerView;
    private Listener listener;

    public DeleteTouchHelper(Listener listener) {
        this.listener = listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        this.recyclerView = recyclerView;
        return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int index = recyclerView.getChildAdapterPosition(viewHolder.itemView);
        int position = recyclerView.getChildLayoutPosition(viewHolder.itemView);
        listener.onItemDeleted(viewHolder, index, position);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    public interface Listener {
        void onItemDeleted(RecyclerView.ViewHolder holder, int index, int position);
    }
}
