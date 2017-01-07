package com.example.alexeyglushkov.wordteacher;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.example.alexeyglushkov.streamlib.progress.ProgressListener;

import pagermodule.view.PagerView;

/**
 * Created by alexeyglushkov on 07.01.17.
 */

public interface MainView {
    void showToolbarBackButton();
    void hideToolbarBackButton();
    void invalidateToolbar();

    Application getApplication();
    PagerView createPagerView();

    Context getContext();
    void startActivityForResult(Intent intent, int code);

    ProgressListener startProgress(ProgressCallback callback);

    interface ProgressCallback {
        void onCancelled();
    }
}
