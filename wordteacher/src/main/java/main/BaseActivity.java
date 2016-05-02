package main;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import authorization.AuthActivityProxy;

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
