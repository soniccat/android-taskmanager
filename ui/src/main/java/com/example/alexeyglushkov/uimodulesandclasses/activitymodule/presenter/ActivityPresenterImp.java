package com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view.ActivityModuleItemView;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view.ActivityModuleView;

import junit.framework.Assert;

/**
 * Created by alexeyglushkov on 02.04.17.
 */

public class ActivityPresenterImp implements ActivityPresenter {
    private ActivityPresenterFactory factory;
    private ActivityModuleView view;
    private ActivityModuleItem item;

    @Override
    public void onCreate(Bundle savedInstanceState, Bundle extras, ActivityModuleItemView restoredItemView) {
        initialize(savedInstanceState, extras);

        if (restoredItemView == null) {
            item = factory.createModule();
            view.setItemView(item.getActivityModuleItemView());

        } else {
            item = factory.restore(restoredItemView);
        }
    }

    private void initialize(Bundle savedInstance, Bundle extra) {
        Bundle bundle = savedInstance != null ? savedInstance : extra;
        initializeFactory(bundle);
    }

    private void initializeFactory(Bundle bundle) {
        String className = bundle.getString(FACTORY_CLASS_KEY);
        try {
            factory = (ActivityPresenterFactory) Class.forName(className).newInstance();
        } catch (Exception e) {
        }

        Assert.assertNotNull(factory);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(FACTORY_CLASS_KEY, factory.getClass().getName());
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState, ActivityModuleItemView child) {
    }

    @Override
    public void setFactory(ActivityPresenterFactory factory) {
        this.factory = factory;
    }

    public void setView(ActivityModuleView view) {
        this.view = view;
    }
}
