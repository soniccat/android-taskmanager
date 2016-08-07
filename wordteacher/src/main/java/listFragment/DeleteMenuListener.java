package listfragment;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.example.alexeyglushkov.wordteacher.R;

/**
 * Created by alexeyglushkov on 07.08.16.
 */
public abstract class DeleteMenuListener<T> extends ListMenuListener<T> {
    protected Snackbar currentSnackbar;
    private boolean snackBarNeedDeleteData;

    public DeleteMenuListener(Context context, Listener listener) {
        super(context, listener);
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
}
