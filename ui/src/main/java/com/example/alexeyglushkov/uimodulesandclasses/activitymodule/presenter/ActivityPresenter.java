package com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.ActivityModule;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view.ActivityModuleItemView;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view.ActivityModuleView;

/**
 * Created by alexeyglushkov on 02.04.17.
 */

public interface ActivityPresenter extends ActivityModule {
    String FACTORY_CLASS_KEY = "FACTORY_CLASS_KEY";

    void onCreate(Bundle savedInstanceState, Bundle extras, ActivityModuleItemView restoredItemView);
    void onSaveInstanceState(Bundle outState);
    void onViewStateRestored(ActivityModuleView view, ActivityModuleItemView child, Bundle savedInstanceState);

    void setView(ActivityModuleView view);
    void setFactory(ActivityPresenterFactory factory);
}
