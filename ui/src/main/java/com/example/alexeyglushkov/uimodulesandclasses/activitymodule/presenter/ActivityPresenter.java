package com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter;

import android.content.Intent;
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
    void onViewStateRestored(Bundle savedInstanceState, ActivityModuleItemView child);
    void onActivityResult(int requestCode, int resultCode, Intent data);

    void setListener(Listener listener);

    void setView(ActivityModuleView view);
    void setFactory(ActivityPresenterFactory factory);

    interface Listener {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
