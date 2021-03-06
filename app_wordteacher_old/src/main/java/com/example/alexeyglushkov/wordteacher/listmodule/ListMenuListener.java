package com.example.alexeyglushkov.wordteacher.listmodule;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import android.view.View;

import com.example.alexeyglushkov.wordteacher.listmodule.view.SimpleListFragment;

/**
 * Created by alexeyglushkov on 24.07.16.
 */
public abstract class ListMenuListener<T> implements SimpleListFragment.Listener<T> {
    protected @NonNull Context context;
    protected @NonNull Listener<T> listener;

    public ListMenuListener(@NonNull Context context, @NonNull Listener<T> listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onRowMenuClicked(T data, View view) {
        onCardMenuClicked(data, view);
    }

    private void onCardMenuClicked(final T data, View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        fillMenu(data, popupMenu);

        popupMenu.show();
    }

    public void onRowClicked(T data) {
        listener.onRowClicked(data);
    }

    @NonNull
    public Listener<T> getListener() {
        return listener;
    }

    public void setListener(@NonNull Listener<T> listener) {
        this.listener = listener;
    }

    protected abstract void fillMenu(T data, PopupMenu menu);

    public interface Listener<T> {
        void onRowClicked(T data);

        //TODO: push these up
        void onDataDeletionCancelled(T data);
        void onDataDeleted(T data, Exception exception);
    }
}
