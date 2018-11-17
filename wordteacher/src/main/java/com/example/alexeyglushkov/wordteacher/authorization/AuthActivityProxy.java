package com.example.alexeyglushkov.wordteacher.authorization;

import android.app.Activity;
import android.content.Intent;

import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient;

import org.junit.Assert;

import java.lang.ref.WeakReference;

/**
 * Created by alexeyglushkov on 25.11.15.
 */
public class AuthActivityProxy implements OAuthWebClient {
    private static WeakReference<Activity> currentActivity;
    private static Callback currentCallback;

    public static Activity getCurrentActivity() {
        return currentActivity != null ? currentActivity.get() : null;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        AuthActivityProxy.currentActivity = new WeakReference<Activity>(currentActivity);
    }

    public static Callback getCurrentCallback() {
        return currentCallback;
    }

    public static void setCurrentCallback(Callback currentCallback) {
        AuthActivityProxy.currentCallback = currentCallback;
    }

    @Override
    public void loadUrl(String url, Callback callback) {
        Assert.assertTrue(currentActivity != null);

        Intent intent = new Intent(getCurrentActivity(), AuthorizationActivity.class);
        intent.putExtra(AuthorizationActivity.LOAD_URL, url);

        setCurrentCallback(callback);
        getCurrentActivity().startActivity(intent);
    }
}
