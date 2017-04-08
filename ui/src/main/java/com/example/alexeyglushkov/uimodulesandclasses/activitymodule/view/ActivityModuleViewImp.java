package com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.alexeyglushkov.uimodulesandclasses.R;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter.ActivityPresenter;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter.ActivityPresenterFactory;

import junit.framework.Assert;

/**
 * Created by alexeyglushkov on 02.04.17.
 */

public class ActivityModuleViewImp extends AppCompatActivity implements ActivityModuleView {
    private String ITEM_TAG = "ITEM_TAG";

    ActivityPresenter presenter;
    ActivityModuleItemView itemView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int layoutId = getIntent().getIntExtra("layout_id", 0);
        setContentView(layoutId);

        Bundle extras = getIntent().getExtras();
        initialize(savedInstanceState, extras);

        itemView = (ActivityModuleItemView) getSupportFragmentManager().findFragmentByTag(ITEM_TAG);
        presenter.onCreate(savedInstanceState, extras, itemView);
    }

    private void initialize(Bundle savedInstance, Bundle extra) {
        Bundle bundle = savedInstance != null ? savedInstance : extra;
        initializePresenter(bundle);
    }

    private void initializePresenter(Bundle bundle) {
        String className = bundle.getString(PRESENTER_CLASS);
        try {
            presenter = (ActivityPresenter) Class.forName(className).newInstance();
        } catch (Exception e) {
        }

        Assert.assertNotNull(presenter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        presenter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        presenter.onViewStateRestored(savedInstanceState, itemView);
    }

    public void setItemView(ActivityModuleItemView view) {
        this.itemView = view;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, (Fragment) view, ITEM_TAG)
                .commitAllowingStateLoss();
    }
}
