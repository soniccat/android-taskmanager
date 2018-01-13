package com.main;

import android.support.v7.app.AppCompatActivity;

import com.authorization.AuthActivityProxy;

/**
 * Created by alexeyglushkov on 12.12.15.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        AuthActivityProxy.setCurrentActivity(this);
    }
}
