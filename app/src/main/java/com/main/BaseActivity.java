package com.main;

import android.support.v7.app.ActionBarActivity;

import com.authorization.AuthActivityProxy;

/**
 * Created by alexeyglushkov on 12.12.15.
 */
public class BaseActivity extends ActionBarActivity {
    @Override
    protected void onResume() {
        super.onResume();
        AuthActivityProxy.setCurrentActivity(this);
    }
}
