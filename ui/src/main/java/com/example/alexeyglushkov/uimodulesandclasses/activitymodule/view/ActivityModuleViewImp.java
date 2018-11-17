package com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alexeyglushkov.uimodulesandclasses.R;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter.ActivityPresenter;

import org.junit.Assert;

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
        setContentView(getLayoutId());

        Bundle extras = getIntent().getExtras();
        initialize(savedInstanceState, extras);

        itemView = (ActivityModuleItemView) getSupportFragmentManager().findFragmentByTag(ITEM_TAG);

        presenter.setView(this);
        presenter.onCreate(savedInstanceState, extras, itemView);
    }

    protected int getLayoutId() {
        return R.layout.activity_layout;
    }

    private void initialize(Bundle savedInstance, Bundle extra) {
        Bundle bundle = savedInstance != null ? savedInstance : extra;
        initializePresenter(bundle);
    }

    private void initializePresenter(Bundle bundle) {
        String className = bundle.getString(PRESENTER_CLASS_KEY);
        try {
            presenter = (ActivityPresenter) Class.forName(className).newInstance();
        } catch (Exception e) {
        }

        Assert.assertNotNull(presenter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PRESENTER_CLASS_KEY, presenter.getClass().getName());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        presenter.onActivityResult(requestCode, resultCode, data);
    }
}
