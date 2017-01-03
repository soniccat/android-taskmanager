package listmodule.view;

import java.util.List;

import pagermodule.PagerModuleItemView;
import stackmodule.StackModuleItemView;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public interface ListViewInterface<T> extends StackModuleItemView, PagerModuleItemView {
    void reload(List<T> items);
    void updateRow(int index);
    void updateRows();

    void showLoading();
    void hideLoading();
}
