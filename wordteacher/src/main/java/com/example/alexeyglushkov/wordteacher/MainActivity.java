package com.example.alexeyglushkov.wordteacher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProxy;
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.service.CachableHttpLoadTask;
import com.example.alexeyglushkov.streamlib.CancelError;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import java.util.ArrayList;
import java.util.List;

import coursefragments.cards.CardListPresenter;
import coursefragments.courses.CourseListPresenterMenuListener;
import coursefragments.courses.CourseListPresenter;
import learning.LearnActivity;
import listmodule.ListModuleInterface;
import main.BaseActivity;
import main.MainApplication;
import main.Preferences;
import model.Card;
import model.Course;
import model.CourseHolder;
import pagermodule.PagerModuleListener;
import pagermodule.view.PagerView;
import pagermodule.view.PagerViewImp;
import quizletfragments.sets.QuizletSetFragmentMenuListener;
import quizletfragments.sets.QuizletSetListPresenter;
import quizletfragments.terms.QuizletTermListPresenter;
import stackmodule.StackModule;
import stackmodule.StackModuleListener;
import tools.Sortable;
import quizletfragments.terms.QuizletTermFragmentMenuListener;
import ui.LoadingButton;

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

    private void onSortOrderChanged(Preferences.SortOrder sortOrder, Sortable module) {
        if (module instanceof QuizletTermListPresenter) {
            Preferences.setQuizletTermSortOrder(sortOrder);

        } else if (module instanceof QuizletSetListPresenter) {
            Preferences.setQuizletSetSortOrder(sortOrder);

        } else if (module instanceof CourseListPresenter) {
            Preferences.setCourseListSortOrder(sortOrder);

        } else if (module instanceof CardListPresenter) {
            Preferences.setCardListSortOrder(sortOrder);
        }

        supportInvalidateOptionsMenu();
    }

    private void onCourseHolderChanged() {
        supportInvalidateOptionsMenu();
    }

    public void onCourseChanged(Course course, @Nullable Exception exception) {
        if (exception != null) {
            showAppExceptionSnackBar(exception);
        }

        updateCoursesIfNeeded();
        onCourseHolderChanged();
    }

    public void onCourseClicked(@NonNull Course course) {
        List<Card> cards = course.getReadyToLearnCards();
        if (cards.size() > 0) {
            startLearnActivity(cards);
        } else if (course.getCards().size() > 0){
            startLearnActivity(course.getCards());
        }
    }

    public void onLearnNewWordsClick(@NonNull Course course) {
        startLearnNewWords(course);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LearnActivity.ACTIVITY_RESULT) {
            updateCoursesIfNeeded();
            supportInvalidateOptionsMenu();
        }
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
            syncWithDropbox();

        } else if (id == R.id.learn_ready_words) {
            startLearnActivity(getReadyCards());
            return true;

        } else if (id == R.id.sort_by_name) {
            applySortOrder(Preferences.SortOrder.BY_NAME);
        } else if (id == R.id.sort_by_create_date) {
            applySortOrder(Preferences.SortOrder.BY_CREATE_DATE_INV);
        } /*else if (id == R.id.sort_by_publish_date) {
            applySortOrder(Preferences.SortOrder.BY_PUBLISH_DATE);
        } else if (id == R.id.sort_by_modify_date) {
            applySortOrder(Preferences.SortOrder.BY_MODIFY_DATE);
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

    private void handleLoadedQuizletSets() {
        forceLoadSetsIfNeeded();
    }

    private void showLoadErrorSnackBar(Error error) {
        String errorString;
        if (error instanceof Authorizer.AuthError) {
            errorString = getString(R.string.error_auth_error);
        } else {
            errorString = getString(R.string.error_load_error);
        }

        Snackbar.make(getRootView(), errorString, Snackbar.LENGTH_LONG).show();
        Log.e(ERROR_TAG, error.getMessage());
    }

    private void showAppExceptionSnackBar(@NonNull Exception ex) {
        String errorString = ex.getMessage();
        Snackbar.make(getRootView(), errorString, Snackbar.LENGTH_LONG).show();
        Log.e(ERROR_TAG, ex.getMessage());
    }

    private View getRootView() {
        return findViewById(R.id.root);
    }

    private void applySortOrder(Preferences.SortOrder order) {
        if (getCurrentSortOrder() == order) {
            order = order.getInverse();
        }

        setSortOrder(order);
    }

    private void syncWithDropbox() {
        getMainApplication().getDropboxService().sync(getCourseHolder().getDirectory().getPath(), "/CoursesTest/", new ServiceCommand.CommandCallback() {
            @Override
            public void onCompleted(Error error) {
                getTaskManager().addTask(getCourseHolder().getLoadCourseListTask());
            }
        });
    }

    // Update data actions

    private void updateSets() {
        StackModule stackModule = getQuizletStackModule();
        if (stackModule != null) {
            //stackModule.reloadSets();
        }

        ListModuleInterface listModule = getTermListQuizletModule();
        if (listModule != null) {
            listModule.reload();
        }
    }

    private void updateCoursesIfNeeded() {
        if (getCourseListStackModule() != null) {
            updateCourses();
        }
    }

    private void updateCourses() {
        StackModule stackModule = getCourseListStackModule();
        if (stackModule != null) {
            //stackModule.reloadCourses();
        }
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

    //// Setters

    private void setSortOrder(Preferences.SortOrder sortOrder) {
        Object module = getCurrentModule();

        if (module instanceof StackModule) {
            StackModule stackModule = (StackModule)module;
            module = stackModule.getModuleAtIndex(stackModule.getSize()-1);
        }

        if (module instanceof Sortable) {
            Sortable sortable = (Sortable)module;
            sortable.setSortOrder(sortOrder);
            onSortOrderChanged(sortOrder, sortable);
        }
    }

    public void setPresenter(MainPresenter presenter) {
        this.presenter = presenter;
    }

    //// Getters

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

    @Override
    public Context getContext() {
        return this;
    }
}
