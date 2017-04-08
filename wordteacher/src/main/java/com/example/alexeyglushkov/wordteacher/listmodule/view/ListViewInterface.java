package com.example.alexeyglushkov.wordteacher.listmodule.view;

import java.util.List;

import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view.ActivityModuleItemView;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.PagerModuleItemView;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.StackModuleItemView;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public interface ListViewInterface<T> extends StackModuleItemView, PagerModuleItemView, ActivityModuleItemView {
    void reload(List<T> items);
    void updateRow(int index);
    void deleteRow(int index);
    void updateRows();

    void showLoading();
    void hideLoading();
}
