package com.example.alexeyglushkov.wordteacher.tools;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by alexeyglushkov on 20.08.16.
 */
public class UITools {
    public static void runAfterRender(Activity activity, final @NonNull PreDrawRunnable runnable) {
        runAfterRender(getActivityView(activity), runnable);
    }

    public static void runAfterRender(final View view, final @NonNull PreDrawRunnable runnable) {
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return runnable.run();
            }
        });
    }

    public static View getActivityView(Activity activity) {
        return activity.getWindow().getDecorView().getRootView();
    }

    public interface PreDrawRunnable {
        boolean run();
    }
}
