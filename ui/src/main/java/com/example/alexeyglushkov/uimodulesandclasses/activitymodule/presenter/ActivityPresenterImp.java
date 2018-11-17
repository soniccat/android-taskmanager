package com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view.ActivityModuleItemView;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view.ActivityModuleView;

import org.junit.Assert;

/**
 * Created by alexeyglushkov on 02.04.17.
 */

public class ActivityPresenterImp implements ActivityPresenter {
    @NonNull private ActivityPresenterFactory factory;
    @NonNull private ActivityModuleView view;
    @NonNull private ActivityModuleItem item;
    @Nullable private Listener listener;

    //// Initialization, Restoration

    @Override
    public void onCreate(Bundle savedInstanceState, Bundle extras, ActivityModuleItemView restoredItemView) {
        initialize(savedInstanceState, extras);

        if (restoredItemView == null) {
            item = factory.createModule(this);
            view.setItemView(item.getActivityModuleItemView());
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
        if (child != null) {
            item = factory.restore(child, this);
        }
    }

    //// Events

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (listener != null) {
            listener.onActivityResult(requestCode, resultCode, data);
        }
    }

    //// Setters

    @Override
    public void setFactory(@NonNull ActivityPresenterFactory factory) {
        this.factory = factory;
    }

    public void setView(@NonNull ActivityModuleView view) {
        this.view = view;
    }

    @Override
    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }
}
