package pagermodule;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public interface PagerModuleListener {
    int getPageCount();
    void onCurrentPageChanged();
}
