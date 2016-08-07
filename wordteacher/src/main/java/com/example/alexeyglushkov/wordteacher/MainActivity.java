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
import android.view.ViewTreeObserver;

import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
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
import quizletfragments.QuizletCardsFragment;
import quizletfragments.QuizletFragmentMenuListener;
import quizletfragments.QuizletStackFragment;

// TODO: consider moving content to fragment
public class MainActivity extends BaseActivity implements MainPageAdapter.Listener, QuizletStackFragment.Listener, CourseListStackFragment.Listener {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager pager;
    private MainPageAdapter pagerAdapter;

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

    //// Lifecycle

    public QuizletService getQuizletService() {
        return getMainApplication().getQuizletService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        initPager();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabPressed();
            }
        });

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void initPager() {
        pager = (ViewPager)findViewById(R.id.pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        });

        pagerAdapter = new MainPageAdapter(getSupportFragmentManager());
        pagerAdapter.setListener(this);
        pager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
    }

    private void onPagerPageChanged() {
        updateSets();
        updateCourses();
        updateToolbarBackButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setOnViewReadyCallback() {
        final View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                rootView.getViewTreeObserver().removeOnPreDrawListener(this);
                onViewReady();
                return true;
            }
        });
    }

    private void setOnViewRestoredCallback() {
        final View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                rootView.getViewTreeObserver().removeOnPreDrawListener(this);

                // here pagerAdapter will be restored
                updateTabs();
                return true;
            }
        });
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    private void restoreFragmentListener(Fragment fragment) {
        // while restoration pagerAdapter could be null
        if (fragment instanceof QuizletStackFragment && pagerAdapter != null) {
            QuizletStackFragment cardsFragment = (QuizletStackFragment)fragment;
            cardsFragment.setListener(this);

        } else if (fragment instanceof QuizletCardsFragment) {
            QuizletCardsFragment quizletFragment = (QuizletCardsFragment) fragment;
            quizletFragment.setListener(getMenuListener());

        } else if (fragment instanceof CourseListStackFragment) {
            CourseListStackFragment courseFragment = (CourseListStackFragment) fragment;
            courseFragment.setListener(this);
        }
    }

    @NonNull
    private QuizletFragmentMenuListener getMenuListener() {
        return new QuizletFragmentMenuListener(this, getCourseHolder(), new QuizletFragmentMenuListener.Listener() {
            @Override
            public void onSetClicked(QuizletSet set) {
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

    private void onViewReady() {
        if (getQuizletService() != null && getQuizletService().getAccount() != null && getQuizletService().getAccount().isAuthorized()) {
            Log.d("load","start load quizlet sets");
            loadQuizletSets(false);
        } else {
            Log.d("quizlet status", "");
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        return super.onPrepareOptionsPanel(view, menu);
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

    private Preferences.SortOrder getCurrentSortOrder() {
        Preferences.SortOrder sortOrder = Preferences.SortOrder.BY_NAME;
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof QuizletCardsFragment) {
            QuizletCardsFragment quizletFragment = (QuizletCardsFragment)fragment;
            sortOrder = quizletFragment.getSortOrder();

        } else if (fragment instanceof QuizletStackFragment) {
            QuizletStackFragment stackFragment = (QuizletStackFragment)fragment;
            sortOrder = stackFragment.getSortOrder();
        }

        return sortOrder;
    }

    private void setSortOrder(Preferences.SortOrder sortOrder) {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof QuizletCardsFragment) {
            QuizletCardsFragment quizletFragment = (QuizletCardsFragment)fragment;
            quizletFragment.setSortOrder(sortOrder);

        } else if (fragment instanceof QuizletStackFragment) {
            QuizletStackFragment stackFragment = (QuizletStackFragment)fragment;
            stackFragment.setSortOrder(sortOrder);
        }
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

    private void applySortOrder(Preferences.SortOrder order) {
        if (getCurrentSortOrder() == order) {
            order = order.getInverse();
        }

        setSortOrder(order);
        supportInvalidateOptionsMenu();
    }

    private List<Card> getReadyCards() {
        ArrayList<Card> cards = new ArrayList<>();
        if (getCourseHolder() != null) {
            for (Course course : getCourseHolder().getCourses()) {
                cards.addAll(course.getReadyToLearnCards());
            }
        }

        return cards;
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

    private Fragment getCurrentFragment() {
        return getFragment(pager.getCurrentItem());
    }

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
            fragment = createQuzletCardsFragment();
        } else {
            fragment = createCourseStackFragment();
        }

        return fragment;
    }

    @NonNull
    private QuizletStackFragment createQuizletStackFragment() {
        QuizletStackFragment fragment = new QuizletStackFragment();
        fragment.setListener(this);

        return fragment;
    }

    @NonNull
    private QuizletCardsFragment createQuzletCardsFragment() {
        QuizletCardsFragment fragment = new QuizletCardsFragment();
        fragment.setViewType(QuizletCardsFragment.ViewType.Cards);
        fragment.setListener(getMenuListener());
        return fragment;
    }

    @NonNull
    private CourseListStackFragment createCourseStackFragment() {
        CourseListStackFragment fragment = new CourseListStackFragment();
        fragment.setListener(this);

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

    @Override
    public void onBackStackChanged() {
        updateToolbarBackButton();
        updateTabs();
        supportInvalidateOptionsMenu();
    }

    //// Other

    private void onFabPressed() {
        loadQuizletSets(true);
    }

    private void loadQuizletSets(boolean forceLoad) {
        CachableHttpLoadTask.CacheMode cacheMode = forceLoad ? CachableHttpLoadTask.CacheMode.LOAD_IF_ERROR_THEN_CHECK_CACHE : CachableHttpLoadTask.CacheMode.CHECK_CACHE_IF_ERROR_THEN_LOAD;

        getQuizletService().loadSets(new ServiceCommand.CommandCallback() {
            @Override
            public void onCompleted(Error error) {
                onQuizletSetsLoaded(error);
            }
        }, cacheMode);
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

    private void handleLoadedQuizletSets() {
        updateSets();
    }

    /*
    private CourseListFragment getCourseListFragment() {
        CourseListFragment result = null;
        CourseListStackFragment container = getCourseListStackFragment();
        if (container != null) {
            result = container.getCour;
        }
        return result;
    }
    */

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

    private QuizletCardsFragment getCardQuizletFragment() {
        return (QuizletCardsFragment)getFragment(1);
    }

    private Fragment getFragment(int i) {
        return (Fragment)pagerAdapter.getFragment(i);
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

    private void startLearnActivity(Course course) {
        startLearnActivity(course.getCards());
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

    private void updateSets() {
        List<QuizletSet> sets = getQuizletService().getSets();
        boolean hasSets = sets != null && sets.size() > 0;

        QuizletStackFragment stackFragment = getQuizletStackFragment();
        if (stackFragment != null && stackFragment.hasData() != hasSets) {
            stackFragment.updateSets(sets);
        }

        QuizletCardsFragment cardFragment = getCardQuizletFragment();
        if (cardFragment != null && cardFragment.hasCards() != hasSets) {
            cardFragment.updateSets(sets);
        }
    }

    private void updateCourses() {
        CourseListStackFragment stackFragment = getCourseListStackFragment();
        List<Course> courses = getCourseHolder().getCourses();
        boolean hasCourses = courses != null && courses.size() > 0;

        if (stackFragment != null && stackFragment.hasCourses() != hasCourses) {
            stackFragment.updateCourses();
        }
    }

    private View getCurrentFragmentView() {
        return pagerAdapter.getFragment(pager.getCurrentItem()).getView();
    }

    // QuizletStackFragment.Listener

    @Override
    public void onCourseChanged(Course course) {
        updateCourses();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LearnActivity.ACTIVITY_RESULT) {
            updateCourses();
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
}
