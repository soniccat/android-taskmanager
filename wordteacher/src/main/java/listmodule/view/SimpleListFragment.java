package listmodule.view;

import android.view.View;

import listmodule.presenter.SimpleListPresenter;

/**
 * Created by alexeyglushkov on 04.01.17.
 */

public abstract class SimpleListFragment<T> extends BaseListFragment<T> {

    //// Getters

    public SimpleListPresenter<T> getPresenter() {
        return (SimpleListPresenter<T>)super.getPresenter();
    }

    //// Inner Interfaces

    public interface Listener<T> {
        void onRowClicked(T data);
        void onRowMenuClicked(T data, View view);
        void onRowViewDeleted(T data);
    }
}
