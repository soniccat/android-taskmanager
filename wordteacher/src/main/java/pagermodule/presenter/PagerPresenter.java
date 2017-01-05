package pagermodule.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.util.List;

import pagermodule.PagerModule;
import pagermodule.PagerModuleFactory;
import pagermodule.PagerModuleItem;
import pagermodule.PagerModuleItemView;
import pagermodule.PagerModuleListener;
import pagermodule.view.PagerView;
import stackmodule.view.StackView;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public interface PagerPresenter extends PagerModule {
    void onViewCreated(Bundle savedInstanceState);
    void onSaveInstanceState(Bundle outState);
    void onViewStateRestored(PagerView view, int currentIndex, SparseArray<Object> childs, Bundle savedInstanceState);

    void setView(PagerView view);
    PagerView getView();
    void setListener(PagerModuleListener listener);
    void setFactory(PagerModuleFactory factory);

    void onPageChanged(int i);

    // to request by view
    PagerModuleItemView getViewAtIndex(int i);
    @Nullable String getViewTitleAtIndex(int i);


}
