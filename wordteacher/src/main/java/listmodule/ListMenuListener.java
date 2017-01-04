package listmodule;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import listmodule.view.BaseListFragment;
import listmodule.view.SimpleListFragment;

/**
 * Created by alexeyglushkov on 24.07.16.
 */
public abstract class ListMenuListener<T> implements SimpleListFragment.Listener<T> {
    protected @NonNull Context context;
    protected @NonNull Listener listener;

    public ListMenuListener(@NonNull Context context, @NonNull Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onRowMenuClicked(T data, View view) {
        onCardMenuClicked(data, view);
    }

    public void onCardMenuClicked(final T data, View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        fillMenu(data, popupMenu);

        popupMenu.show();
    }

    public void onRowClicked(T data) {
        listener.onRowClicked(data);
    }

    @NonNull
    public Listener getListener() {
        return listener;
    }

    public void setListener(@NonNull Listener listener) {
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
