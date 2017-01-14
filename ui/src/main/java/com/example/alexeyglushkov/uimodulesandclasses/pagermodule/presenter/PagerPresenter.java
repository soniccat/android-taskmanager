package com.example.alexeyglushkov.uimodulesandclasses.pagermodule.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.PagerModule;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.PagerModuleFactory;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.PagerModuleItemView;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.PagerModuleListener;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.view.PagerView;

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
