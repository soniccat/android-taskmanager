package com.example.alexeyglushkov.wordteacher.sessionresultmodule;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;

import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view.ActivityModuleViewImp;
import com.example.alexeyglushkov.wordteacher.R;

import com.example.alexeyglushkov.wordteacher.sessionresultmodule.view.SessionResultView;

/**
 * Created by alexeyglushkov on 15.05.16.
 */
public class SessionResultActivity extends ActivityModuleViewImp implements SessionResultView {
    public static final int ACTIVITY_RESULT = 10001;

    private Toolbar toolbar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_session_result;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }
}
