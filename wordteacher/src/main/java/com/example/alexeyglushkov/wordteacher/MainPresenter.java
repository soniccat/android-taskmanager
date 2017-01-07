package com.example.alexeyglushkov.wordteacher;

import android.content.Intent;
import android.os.Bundle;

import learning.LearnActivity;
import main.Preferences;
import pagermodule.view.PagerView;
import pagermodule.view.PagerViewImp;

/**
 * Created by alexeyglushkov on 07.01.17.
 */

public interface MainPresenter extends MainModule {
    void onCreate(Bundle savedInstanceState);
    void onDestroy();

    void onSaveInstanceState(Bundle outState);
    void onRestoreInstanceState(Bundle savedInstanceState);

    boolean onBackPressed();
    void onFabPressed();

    boolean isLearnButtonEnabled();
    Preferences.SortOrder getCurrentSortOrder();
}
