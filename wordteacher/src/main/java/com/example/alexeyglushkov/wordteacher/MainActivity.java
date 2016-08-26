package com.example.alexeyglushkov.wordteacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.*;
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
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.service.CachableHttpLoadTask;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import java.util.ArrayList;
import java.util.List;

import coursefragments.CourseListStackFragment;
import learning.LearnActivity;
import main.BaseActivity;
import main.MainApplication;
import main.Preferences;
import model.Card;
import model.Course;
import model.CourseHolder;
import quizletfragments.QuizletSortable;
import quizletfragments.terms.QuizletTermFragmentMenuListener;
import quizletfragments.terms.QuizletTermListFragment;
import quizletfragments.QuizletStackFragment;
import tools.UITools;

// TODO: consider moving content to fragment
public class MainActivity extends BaseActivity implements MainPageAdapter.Listener, QuizletStackFragment.Listener, CourseListStackFragment.Listener {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager pager;
    private MainPageAdapter pagerAdapter;

    //// Creation, initialization, restoration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();
        initPager();
        initFloatingButton();

        if (savedInstanceState != null) {
            restoreListeners();
            setOnViewRestoredCallback();

        } else {
            setOnViewReadyCallback();
        }
    }

    private void restoreListeners() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            restoreFragmentListener(fragment);
        }
    }

    private void setOnViewReadyCallback() {
        UITools.runAfterRender(this, new UITools.PreDrawRunnable() {
            @Override
            public boolean run() {
                onViewReady();
                return true;
            }
        });
    }

    private void setOnViewRestoredCallback() {
        UITools.runAfterRender(this, new UITools.PreDrawRunnable() {
            @Override
            public boolean run() {
                // here pagerAdapter will be restored
                updateTabs();
                return true;
            }
        });
    }

    private void restoreFragmentListener(Fragment fragment) {
        // while restoration pagerAdapter could be null
        if (fragment instanceof QuizletStackFragment && pagerAdapter != null) {
            QuizletStackFragment cardsFragment = (QuizletStackFragment)fragment;
            cardsFragment.setListener(this);

        } else if (fragment instanceof QuizletSortable) {
            QuizletTermListFragment quizletFragment = (QuizletTermListFragment) fragment;
            quizletFragment.setListener(createMenuListener());

        } else if (fragment instanceof CourseListStackFragment) {
            CourseListStackFragment courseFragment = (CourseListStackFragment) fragment;
            courseFragment.setListener(this);
        }
    }

    // UI creation and initialization

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initPager() {
        pager = (ViewPager)findViewById(R.id.pager);
        pager.addOnPageChangeListener(createPageListener());

        pagerAdapter = new MainPageAdapter(getSupportFragmentManager());
        pagerAdapter.setListener(this);
        pager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
    }

    private void initFloatingButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabPressed();
            }
        });
    }

    //// Events

    private void onPagerPageChanged() {
        updateToolbarBackButton();
    }

    private void onViewReady() {
        if (getQuizletService() == null) {
            getMainApplication().addQuizletServiceListener(new MainApplication.ReadyListener() {
                @Override
                public void onReady() {
                    onQuizletServiceLoaded();
                }
            });
        } else {
            onQuizletServiceLoaded();
        }
    }

    private void onQuizletServiceLoaded() {
        if (getQuizletService() != null && getQuizletService().getAccount() != null && getQuizletService().getAccount().isAuthorized()) {
            Log.d("load","start load quizlet sets");
            loadQuizletSets(false);
        }
    }

    private void onQuizletSetsLoaded(Error error) {
        if (error != null) {
            boolean isCancelled = false;
            if (error instanceof Authorizer.AuthError) {
                isCancelled = ((Authorizer.AuthError)error).getReason() == Authorizer.AuthError.Reason.Cancelled;
            }

            if (!isCancelled) {
                showErrorSnackBar(error);
            }
        } else {
            handleLoadedQuizletSets();
        }
    }

    private void onFabPressed() {
        loadQuizletSets(true);
    }

    // Menu Event

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem learnMenuItem = menu.findItem(R.id.learn_ready_words);
        List<Card> cards = getReadyCards();
        learnMenuItem.setVisible(cards.size() > 0);

        MenuItem sortByCreateName = menu.findItem(R.id.sort_by_name);
        MenuItem sortByCreateDate = menu.findItem(R.id.sort_by_create_date);
        //MenuItem sortByModifyDate = menu.findItem(R.id.sort_by_modify_date);
        //MenuItem sortByPublishDate = menu.findItem(R.id.sort_by_publish_date);

        Preferences.SortOrder sortOrder = getCurrentSortOrder();
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
    public boolean onOptionsItemSelected(MenuItem item) {
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
    public void onBackStackChanged() {
        updateToolbarBackButton();
        updateTabs();
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        final Fragment frag = getCurrentFragment();
        if (frag.isVisible() && frag instanceof StackFragment) {
            StackFragment stackFragment = (StackFragment)frag;
            if (stackFragment.getBackStackSize() > 0) {
                stackFragment.popFragment(null);
                return;
            }
        }

        super.onBackPressed();
    }

    //// Actions

    private void loadQuizletSets(boolean forceLoad) {
        CachableHttpLoadTask.CacheMode cacheMode = forceLoad ? CachableHttpLoadTask.CacheMode.LOAD_IF_ERROR_THEN_CHECK_CACHE : CachableHttpLoadTask.CacheMode.CHECK_CACHE_IF_ERROR_THEN_LOAD;

        getQuizletService().loadSets(new ServiceCommand.CommandCallback() {
            @Override
            public void onCompleted(Error error) {
                onQuizletSetsLoaded(error);
            }
        }, cacheMode);
    }

    private void handleLoadedQuizletSets() {
        updateSets();
    }

    private void showErrorSnackBar(Error error) {
        String errorString = "";
        if (error instanceof Authorizer.AuthError) {
            errorString = getString(R.string.error_auth_error);
        } else {
            errorString = getString(R.string.error_load_error);
        }

        Snackbar.make(getCurrentFragmentView(), errorString, Snackbar.LENGTH_LONG).show();
    }

    private void startLearnNewWords(Course course) {
        startLearnActivity(course.getNotStartedCards());
    }

    private void startLearnActivity(List<Card> cards) {
        Intent activityIntent = new Intent(this, LearnActivity.class);
        String[] cardIds = new String[cards.size()];

        for (int i=0; i<cards.size(); ++i) {
            Card card = cards.get(i);
            cardIds[i] = card.getId().toString();
        }

        activityIntent.putExtra(LearnActivity.EXTRA_CARD_IDS, cardIds);
        activityIntent.putExtra(LearnActivity.EXTRA_DEFINITION_TO_TERM, true);

        startActivityForResult(activityIntent, LearnActivity.ACTIVITY_RESULT);
    }

    private void applySortOrder(Preferences.SortOrder order) {
        if (getCurrentSortOrder() == order) {
            order = order.getInverse();
        }

        setSortOrder(order);
        supportInvalidateOptionsMenu();
    }

    private void syncWithDropbox() {
        boolean hasCourses = getCourseHolder().getCourses().size() > 0;
        if (hasCourses) {
            getMainApplication().getDropboxService().upload(getCourseHolder().getDirectory().getPath(), "/Courses/", new ServiceCommand.CommandCallback() {
                @Override
                public void onCompleted(Error error) {
                    int i = 0;
                    ++i;
                }
            });
        } else {
            getMainApplication().getDropboxService().download("/Courses/", getCourseHolder().getDirectory().getPath(), new ServiceCommand.CommandCallback() {
                @Override
                public void onCompleted(Error error) {
                    int i = 0;
                    ++i;
                }
            });
        }
    }

    // Update data actions

    private void updateSets() {
        QuizletStackFragment stackFragment = getQuizletStackFragment();
        stackFragment.reloadSets();

        QuizletTermListFragment termFragment = getTermListQuizletFragment();
        termFragment.reload();
    }

    private void updateCoursesIfNeeded() {
        if (getCourseListStackFragment() != null) {
            updateCourses();
        }
    }

    private void updateCourses() {
        CourseListStackFragment stackFragment = getCourseListStackFragment();
        stackFragment.reloadCourses();
    }

    // Update UI actions

    private void updateToolbarBackButton() {
        StackFragment stackFragment = getStackContainer(pager.getCurrentItem());

        boolean needShowBackButton = false;
        if (stackFragment != null) {
            needShowBackButton = stackFragment.getBackStackSize() > 0;
        }

        if (needShowBackButton) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toolbar.setNavigationOnClickListener(null);
                    MainActivity.this.onBackPressed();
                }
            });
        } else {
            toolbar.setNavigationIcon(null);
        }
    }

    private void updateTabs() {
        pagerAdapter.notifyDataSetChanged();
    }

    //// Callbacks

    // QuizletStackCardsFragment.Listener

    @Override
    public int getFragmentCount() {
        return 3;
    }

    @Override
    public Fragment getFragmentAtIndex(int index) {
        Fragment fragment;
        if (index == 0) {
            fragment = createQuizletStackFragment();
        } else if (index == 1) {
            fragment = createQuzletTermListFragment();
        } else {
            fragment = createCourseStackFragment();
        }

        return fragment;
    }

    @Override
    public String getTitleAtIndex(int index, boolean isDefault) {
        String title = null;
        if (index == 0) {
            QuizletStackFragment quizletStackFragment = getQuizletStackFragment();
            if (quizletStackFragment != null) {
                title = quizletStackFragment.getTitle();
            } else if (isDefault) {
                title = QuizletStackFragment.DEFAULT_TITLE;
            }

        } else if (index == 1) {
            title = "Cards";
        } else {
            CourseListStackFragment courseListStackFragment = getCourseListStackFragment();
            if (courseListStackFragment != null) {
                title = courseListStackFragment.getTitle();
            } else if (isDefault) {
                title = CourseListStackFragment.DEFAULT_TITLE;
            }
        }

        return title;
    }

    // QuizletStackFragment.Listener

    @Override
    public void onCourseChanged(Course course) {
        updateCoursesIfNeeded();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LearnActivity.ACTIVITY_RESULT) {
            updateCoursesIfNeeded();
        }
    }

    // CourseStackFragment.Listener

    @Override
    public void onCourseClicked(Course course) {
        List<Card> cards = course.getReadyToLearnCards();
        if (cards.size() > 0) {
            startLearnActivity(cards);
        } else if (course.getCards().size() > 0){
            startLearnActivity(course.getCards());
        }
    }

    @Override
    public void onLearnNewWordsClick(Course course) {
        startLearnNewWords(course);
    }

    //// Creation methods

    @NonNull
    private QuizletTermFragmentMenuListener createMenuListener() {
        return new QuizletTermFragmentMenuListener(this, getCourseHolder(), new QuizletTermFragmentMenuListener.Listener<QuizletTerm>() {
            @Override
            public void onRowClicked(QuizletTerm data) {
            }

            @Override
            public void onDataDeletionCancelled(QuizletTerm data) {
            }

            @Override
            public void onDataDeleted(QuizletTerm data) {
            }

            @Override
            public void onCourseCreated(Course course) {
                MainActivity.this.onCourseChanged(course);
            }

            @Override
            public void onCardsAdded(Course course) {
                MainActivity.this.onCourseChanged(course);
            }

            @Override
            public ViewGroup getDialogContainer() {
                return (ViewGroup) getCourseListStackFragment().getView();
            }

            @Override
            public void onCourseChanged(Course course) {
            }
        });
    }

    @NonNull
    private ViewPager.OnPageChangeListener createPageListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                onPagerPageChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

    @NonNull
    private QuizletStackFragment createQuizletStackFragment() {
        QuizletStackFragment fragment = new QuizletStackFragment();
        fragment.setListener(this);

        return fragment;
    }

    @NonNull
    private QuizletTermListFragment createQuzletTermListFragment() {
        QuizletTermListFragment fragment = new QuizletTermListFragment();
        fragment.setListener(createMenuListener());
        return fragment;
    }

    @NonNull
    private CourseListStackFragment createCourseStackFragment() {
        CourseListStackFragment fragment = new CourseListStackFragment();
        fragment.setListener(this);

        return fragment;
    }

    //// Setters

    private void setSortOrder(Preferences.SortOrder sortOrder) {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof QuizletSortable) {
            QuizletSortable quizletFragment = (QuizletSortable)fragment;
            quizletFragment.setSortOrder(sortOrder);

        } else if (fragment instanceof QuizletStackFragment) {
            QuizletStackFragment stackFragment = (QuizletStackFragment)fragment;
            stackFragment.setSortOrder(sortOrder);
        }
    }

    //// Getters

    // App getters

    private MainApplication getMainApplication() {
        return (MainApplication)getApplication();
    }

    public TaskManager getTaskManager() {
        return getMainApplication().getTaskManager();
    }

    public AccountStore getAccountStore() {
        return getMainApplication().getAccountStore();
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    public QuizletService getQuizletService() {
        return getMainApplication().getQuizletService();
    }

    // Data getters

    private List<Card> getReadyCards() {
        ArrayList<Card> cards = new ArrayList<>();
        if (getCourseHolder() != null) {
            for (Course course : getCourseHolder().getCourses()) {
                cards.addAll(course.getReadyToLearnCards());
            }
        }

        return cards;
    }

    private Preferences.SortOrder getCurrentSortOrder() {
        Preferences.SortOrder sortOrder = Preferences.SortOrder.BY_NAME;
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof QuizletSortable) {
            QuizletSortable quizletFragment = (QuizletSortable)fragment;
            sortOrder = quizletFragment.getSortOrder();

        } else if (fragment instanceof QuizletStackFragment) {
            QuizletStackFragment stackFragment = (QuizletStackFragment)fragment;
            sortOrder = stackFragment.getSortOrder();
        }

        return sortOrder;
    }

    // UI getters

    private Fragment getCurrentFragment() {
        return getFragment(pager.getCurrentItem());
    }

    private View getCurrentFragmentView() {
        return pagerAdapter.getFragment(pager.getCurrentItem()).getView();
    }

    private QuizletStackFragment getQuizletStackFragment() {
        return (QuizletStackFragment)getStackContainer(0);
    }

    private CourseListStackFragment getCourseListStackFragment() {
        return (CourseListStackFragment)getStackContainer(2);
    }

    private StackFragment getStackContainer(int position) {
        StackFragment result = null;

        Fragment fragment = getFragment(position);
        if (fragment instanceof StackFragment) {
            result = (StackFragment)fragment;
        }

        return result;
    }

    private QuizletTermListFragment getTermListQuizletFragment() {
        return (QuizletTermListFragment)getFragment(1);
    }

    private Fragment getFragment(int i) {
        return (Fragment)pagerAdapter.getFragment(i);
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
