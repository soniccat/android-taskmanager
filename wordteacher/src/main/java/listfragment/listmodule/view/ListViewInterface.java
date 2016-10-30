package listfragment.listmodule.view;

import java.util.List;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public interface ListViewInterface<T> {
    void reload(List<T> items);
    void showLoading();
    void hideLoading();
}
