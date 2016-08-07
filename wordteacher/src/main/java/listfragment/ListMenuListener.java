package listfragment;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import com.example.alexeyglushkov.wordteacher.R;

import model.Card;
import model.Course;

/**
 * Created by alexeyglushkov on 24.07.16.
 */
public abstract class ListMenuListener<T> implements BaseListFragment.Listener<T> {
    protected Context context;
    protected Listener listener;

    public ListMenuListener(Context context, Listener listener) {
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

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    protected abstract Error deleteData(T data);
    protected abstract void fillMenu(T data, PopupMenu menu);

    public interface Listener<T> {
        void onRowClicked(T data);
        void onDataDeletionCancelled(T data);
        void onDataDeleted(T data);

        View getSnackBarViewContainer();
    }
}
