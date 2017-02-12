package com.example.alexeyglushkov.wordteacher.main_module.presenter;

import android.content.Intent;
import android.os.Bundle;

import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.wordteacher.main_module.MainModule;
import com.example.alexeyglushkov.wordteacher.main_module.view.MainView;

/**
 * Created by alexeyglushkov on 07.01.17.
 */

public interface MainPresenter extends MainModule {
    void setView(MainView view);

    void onCreate(Bundle savedInstanceState);
    void onDestroy();

    void onSaveInstanceState(Bundle outState);
    void onRestoreInstanceState(Bundle savedInstanceState);
    void onActivityResult(int requestCode, int resultCode, Intent data);

    boolean onBackPressed();
    void onStartPressed();
    void onDropboxPressed();
    void onFabPressed();
    void onSortOrderSelected(Preferences.SortOrder order);

    boolean isLearnButtonEnabled();
    Preferences.SortOrder getCurrentSortOrder();
}
