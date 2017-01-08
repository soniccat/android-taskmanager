package com.example.alexeyglushkov.wordteacher;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.example.alexeyglushkov.streamlib.progress.ProgressListener;

import pagermodule.view.PagerView;

/**
 * Created by alexeyglushkov on 07.01.17.
 */

public interface MainView {
    void showLoadError(@NonNull Error error);
    void showException(@NonNull Exception ex);
    void showToolbarBackButton();
    void hideToolbarBackButton();
    void invalidateToolbar();

    Application getApplication();
    PagerView createPagerView();

    Context getContext();
    View getRootView();
    void startActivityForResult(Intent intent, int code);

    ProgressListener startProgress(ProgressCallback callback);
    void stopProgress();

    interface ProgressCallback {
        void onCancelled();
    }
}
