package com.example.alexeyglushkov.uimodulesandclasses.stackmodule.presenter;

import android.os.Bundle;

import java.util.List;

import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.StackModule;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.StackModuleFactory;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.StackModuleListener;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.view.StackView;

/**
 * Created by alexeyglushkov on 04.11.16.
 */

public interface StackPresenterInterface extends StackModule {
    void onBackStackChanged();
    void onViewCreated(Bundle savedInstanceState);
    void onSaveInstanceState(Bundle outState);
    void onViewStateRestored(StackView view, List<Object> childs, Bundle savedInstanceState);

    void setListener(StackModuleListener listener);
    void setFactory(StackModuleFactory factory);
}
