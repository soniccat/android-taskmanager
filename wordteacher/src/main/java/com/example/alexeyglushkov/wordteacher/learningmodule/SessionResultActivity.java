package com.example.alexeyglushkov.wordteacher.learningmodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.alexeyglushkov.wordteacher.R;

import com.example.alexeyglushkov.wordteacher.model.LearnSession;
import com.example.alexeyglushkov.wordteacher.main.BaseActivity;

/**
 * Created by alexeyglushkov on 15.05.16.
 */
public class SessionResultActivity extends BaseActivity {

    public static final String EXTERNAL_SESSION = "session";
    public static final int ACTIVITY_RESULT = 10001;

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_result);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        LearnSession session = getIntent().getParcelableExtra(EXTERNAL_SESSION);

        SessionResultFragment resultFragment = (SessionResultFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);
        resultFragment.setSession(session);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }
}
