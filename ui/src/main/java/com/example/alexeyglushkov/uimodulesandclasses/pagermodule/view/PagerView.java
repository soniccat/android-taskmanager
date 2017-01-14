package com.example.alexeyglushkov.uimodulesandclasses.pagermodule.view;

import android.os.Bundle;

import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.presenter.PagerPresenter;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public interface PagerView {
    void setPresenter(PagerPresenter module);
    PagerPresenter getPresenter();

    void setItemCount(int itemCount);
    void updateView(int index);

    void onViewCreated(Bundle savedInstanceState);
    void onSaveInstanceState(Bundle outState);
    void onRestoreInstanceState(Bundle savedInstanceState);
}
