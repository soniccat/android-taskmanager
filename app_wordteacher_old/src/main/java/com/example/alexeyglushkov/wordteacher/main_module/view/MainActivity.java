package com.example.alexeyglushkov.wordteacher.main_module.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;

import com.example.alexeyglushkov.wordteacher.R;
import com.example.alexeyglushkov.wordteacher.main.BaseActivity;
import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.view.PagerView;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.view.PagerViewImp;
import com.example.alexeyglushkov.wordteacher.main_module.presenter.MainPresenter;
import com.example.alexeyglushkov.wordteacher.ui.LoadingButton;

// TODO: consider moving content to fragment
public class MainActivity extends BaseActivity implements
        MainView {

    @NonNull
    private static String ERROR_TAG = "Exception";

    private MainPresenter presenter;

    private @NonNull Toolbar toolbar;
    private @NonNull TabLayout tabLayout;
    private @NonNull ViewPager pager;
    private @NonNull LoadingButton loadingButton;

    //// Creation, initialization, restoration

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setToolbar();
        initPager(savedInstanceState);
        initFloatingButton();

        presenter = createPresenter();
        presenter.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        presenter.onRestoreInstanceState(savedInstanceState);
    }

    // UI creation and initialization

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initPager(Bundle savedInstanceState) {
        pager = (ViewPager)findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
    }

    private void initFloatingButton() {
        loadingButton = (LoadingButton) findViewById(R.id.fab);
        loadingButton.setStartListener(new LoadingButton.StartListener() {
            @Override
            public void onStart() {
                presenter.onFabPressed();
            }
        });
    }

    //// Events

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateToolbar();
    }

    // Menu Event

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem learnMenuItem = menu.findItem(R.id.learn_ready_words);
        learnMenuItem.setEnabled(presenter.isLearnButtonEnabled());

        MenuItem sortByCreateName = menu.findItem(R.id.sort_by_name);
        MenuItem sortByCreateDate = menu.findItem(R.id.sort_by_create_date);
        //MenuItem sortByModifyDate = menu.findItem(R.id.sort_by_modify_date);
        //MenuItem sortByPublishDate = menu.findItem(R.id.sort_by_publish_date);

        Preferences.SortOrder sortOrder = presenter.getCurrentSortOrder();
        if (isSortByName(sortOrder)) {
            sortByCreateName.setChecked(true);
        }

        if (isSortByCreateDate(sortOrder)) {
            sortByCreateDate.setChecked(true);
        }

        /*
        if (isSortByModifyDate(sortOrder)) {
            sortByModifyDate.setChecked(true);
        }

        if (isSortByPublishDate(sortOrder)) {
            sortByPublishDate.setChecked(true);
        }*/

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_sync_with_dropbox) {
            presenter.onDropboxPressed();

        } else if (id == R.id.learn_ready_words) {
            presenter.onStartPressed();
            return true;

        } else if (id == R.id.sort_by_name) {
            presenter.onSortOrderSelected(Preferences.SortOrder.BY_NAME);
        } else if (id == R.id.sort_by_create_date) {
            presenter.onSortOrderSelected(Preferences.SortOrder.BY_CREATE_DATE_INV);
        } /*else if (id == R.id.sort_by_publish_date) {
            onSortOrderSelected(Preferences.SortOrder.BY_PUBLISH_DATE);
        } else if (id == R.id.sort_by_modify_date) {
            onSortOrderSelected(Preferences.SortOrder.BY_MODIFY_DATE);
        }*/

        return super.onOptionsItemSelected(item);
    }

    // Backstack

    @Override
    public void onBackPressed() {
        if (!presenter.onBackPressed()) {
            super.onBackPressed();
        }
    }

    //// Actions

    @Override
    public ProgressListener startProgress(final ProgressCallback callback) {
        loadingButton.setCancelListener(new LoadingButton.CancelListener() {
            @Override
            public void onCancel() {
                callback.onCancelled();
            }
        });
        return loadingButton.startLoading();
    }

    @Override
    public void stopProgress() {
        loadingButton.stopLoading();
    }

    private void showLoadErrorSnackBar(Throwable error) {
        String errorString;
        if (error instanceof Authorizer.AuthError) {
            errorString = getString(R.string.error_auth_error);
        } else {
            errorString = getString(R.string.error_load_error);
        }

        Snackbar.make(getRootView(), errorString, Snackbar.LENGTH_LONG).show();
        Log.e(ERROR_TAG, error.getMessage());
        error.printStackTrace();
    }

    @Override
    public void showLoadError(@NonNull Throwable error) {
        showLoadErrorSnackBar(error);
    }

    public void showException(@NonNull Exception ex) {
        String errorString = ex.getMessage();
        Snackbar.make(getRootView(), errorString, Snackbar.LENGTH_LONG).show();
        Log.e(ERROR_TAG, ex.getMessage());
    }

    // Update UI actions

    public void showToolbarBackButton() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setNavigationOnClickListener(null);
                MainActivity.this.onBackPressed();
            }
        });
    }

    public void hideToolbarBackButton() {
        toolbar.setNavigationIcon(null);
    }

    @Override
    public void invalidateToolbar() {
        supportInvalidateOptionsMenu();
    }

    //// Creation methods

    public PagerView createPagerView() {
        return new PagerViewImp(pager, getSupportFragmentManager());
    }

    private MainPresenter createPresenter() {
        MainPresenter presenter = null;
        String presenterName = this.getResources().getString(R.string.main_presenter_class);
        try {
            presenter = (MainPresenter) getClassLoader().loadClass(presenterName).newInstance();
            presenter.setView(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return presenter;
    }

    //// Setters

    public void setPresenter(MainPresenter presenter) {
        this.presenter = presenter;
    }

    //// Getters

    @Override
    public Context getContext() {
        return this;
    }

    public View getRootView() {
        return findViewById(R.id.root);
    }

    // Statuses

    private boolean isSortByName(Preferences.SortOrder sortOrder) {
        return sortOrder == Preferences.SortOrder.BY_NAME || sortOrder == Preferences.SortOrder.BY_NAME_INV;
    }

    private boolean isSortByCreateDate(Preferences.SortOrder sortOrder) {
        return sortOrder == Preferences.SortOrder.BY_CREATE_DATE || sortOrder == Preferences.SortOrder.BY_CREATE_DATE_INV;
    }

    private boolean isSortByModifyDate(Preferences.SortOrder sortOrder) {
        return sortOrder == Preferences.SortOrder.BY_MODIFY_DATE || sortOrder == Preferences.SortOrder.BY_MODIFY_DATE_INV;
    }

    private boolean isSortByPublishDate(Preferences.SortOrder sortOrder) {
        return sortOrder == Preferences.SortOrder.BY_PUBLISH_DATE || sortOrder == Preferences.SortOrder.BY_PUBLISH_DATE_INV;
    }
}
