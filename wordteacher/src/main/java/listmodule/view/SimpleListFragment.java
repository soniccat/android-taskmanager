package listmodule.view;

import android.view.View;

/**
 * Created by alexeyglushkov on 04.01.17.
 */

public abstract class SimpleListFragment<T> extends BaseListFragment<T> {

    //// Inner Interfaces

    public interface Listener<T> {
        void onRowClicked(T data);
        void onRowMenuClicked(T data, View view);
        void onRowViewDeleted(T data);
    }
}
