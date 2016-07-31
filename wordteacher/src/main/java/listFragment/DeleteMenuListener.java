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
public abstract class DeleteMenuListener<T> implements BaseListFragment.Listener<T> {
    protected Context context;
    protected Listener listener;
    protected Snackbar currentSnackbar;

    private boolean snackBarNeedDeleteData;

    public DeleteMenuListener(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    protected void deleteDataWithSnackbar(final T data) {
        snackBarNeedDeleteData = true;

        String undoString = context.getString(R.string.snackbar_undo_deletion);
        currentSnackbar = Snackbar.make(listener.getSnackBarViewContainer(), undoString, Snackbar.LENGTH_LONG);
        currentSnackbar.setAction(android.R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBarNeedDeleteData = false;
                listener.onDataDeletionCancelled(data);
            }
        });
        currentSnackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (snackBarNeedDeleteData) {
                    Error error = deleteData(data);
                    if (error != null) {
                        listener.onDataDeletionCancelled(data);
                    } else {
                        listener.onDataDeleted(data);
                    }
                }

                currentSnackbar = null;
            }
        });
        currentSnackbar.show();
    }

    public void applyPendingOperation() {
        if (currentSnackbar != null) {
            currentSnackbar.dismiss();
        }
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
