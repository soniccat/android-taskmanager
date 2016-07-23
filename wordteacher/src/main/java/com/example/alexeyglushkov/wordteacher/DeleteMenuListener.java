package com.example.alexeyglushkov.wordteacher;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import model.Course;
import model.CourseHolder;

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

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    protected abstract Error deleteData(T data);

    public interface Listener<T> {
        void onDataClicked(T data);
        void onDataDeletionCancelled(T data);
        void onDataDeleted(T data);

        View getSnackBarViewContainer();
    }
}
