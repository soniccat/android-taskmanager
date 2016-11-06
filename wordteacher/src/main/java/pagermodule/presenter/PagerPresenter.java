package pagermodule.presenter;

import pagermodule.PagerModule;
import pagermodule.PagerModuleFactory;
import pagermodule.PagerModuleItem;
import pagermodule.PagerModuleItemView;
import pagermodule.PagerModuleListener;
import pagermodule.view.PagerView;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public interface PagerPresenter extends PagerModule {
    void setView(PagerView view);
    void setListener(PagerModuleListener listener);
    void setFactory(PagerModuleFactory factory);

    void onPageChanged(int i);

    PagerModuleItemView getViewAtIndex(int i);
}
