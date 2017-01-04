package listmodule.presenter;

import android.view.View;

import listmodule.view.BaseListFragment;
import listmodule.view.SimpleListFragment;

/**
 * Created by alexeyglushkov on 04.01.17.
 */

public abstract class SimpleListPresenter<T> extends BaseListPresenter<T> implements SimpleListFragment.Listener<T> {
    private SimpleListFragment.Listener<T> listener;

    //// Setter

    public void setListener(SimpleListFragment.Listener<T> listener) {
        this.listener = listener;
    }

    //// Interfaces

    // SimpleListFragment.Listener

    @Override
    public void onRowClicked(T data) {
        if (listener != null) {
            listener.onRowClicked(data);
        }
    }

    @Override
    public void onRowMenuClicked(T data, View view) {
        if (listener != null) {
            listener.onRowMenuClicked(data, view);
        }
    }

    @Override
    public void onRowViewDeleted(T data) {
        if (listener != null) {
            listener.onRowViewDeleted(data);
        }
    }
}
