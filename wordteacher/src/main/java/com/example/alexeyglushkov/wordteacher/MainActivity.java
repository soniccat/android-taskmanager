package com.example.alexeyglushkov.wordteacher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
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
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.service.CachableHttpLoadTask;
import com.example.alexeyglushkov.streamlib.CancelError;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import java.util.ArrayList;
import java.util.List;

import coursefragments.CourseListStackFragment;
import learning.LearnActivity;
import listmodule.ListModuleInterface;
import main.BaseActivity;
import main.MainApplication;
import main.Preferences;
import model.Card;
import model.Course;
import model.CourseHolder;
import pagermodule.PagerModule;
import pagermodule.PagerModuleListener;
import pagermodule.presenter.StatePagerPresenter;
import pagermodule.view.PagerView;
import pagermodule.view.PagerViewImp;
import stackmodule.StackModule;
import tools.Sortable;
import quizletfragments.terms.QuizletTermFragmentMenuListener;
import quizletfragments.terms.QuizletTermListFragment;
import quizletfragments.stack.QuizletStackFragment;
import tools.UITools;
import ui.LoadingButton;

// TODO: consider moving content to fragment
public class MainActivity extends BaseActivity implements
        PagerModuleListener,
        QuizletStackFragment.Listener,
        CourseListStackFragment.Listener,
        QuizletService.QuizletServiceListener,
        CourseHolder.CourseHolderListener {

    @NonNull
    private static String ERROR_TAG = "Exception";

    private @NonNull Toolbar toolbar;
    private @NonNull TabLayout tabLayout;
    private @NonNull LoadingButton loadingButton;

    private PagerModule pagerModule;

    //// Creation, initialization, restoration

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setToolbar();
        initPager();
        initFloatingButton();

        if (savedInstanceState != null) {
            restoreListeners();
            setOnViewRestoredCallback();
        }

        getCourseHolder().addListener(this);
        getQuizletService().addListener(this);
        forceLoadSetsIfNeeded();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getCourseHolder().removeListener(this);
        getQuizletService().removeListener(this);
    }

    private void restoreListeners() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            restoreFragmentListener(fragment);
        }
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
        if (fragment instanceof QuizletStackFragment) {
            QuizletStackFragment cardsFragment = (QuizletStackFragment)fragment;
            cardsFragment.setListener(this);

        } else if (fragment instanceof QuizletTermListFragment) {
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
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        createPagerModule(pager);
        pagerModule.reload();
    }

    private void createPagerModule(ViewPager pager) {
        MainPagerFactory factory = new MainPagerFactory();

        StatePagerPresenter pagerPresenter = new StatePagerPresenter();
        pagerPresenter.setListener(this);
        pagerPresenter.setFactory(factory);

        PagerViewImp pagerView = new PagerViewImp(pager, getSupportFragmentManager());
        pagerView.setPresenter(pagerPresenter);
        pagerPresenter.setView(pagerView);

        pagerModule = pagerPresenter;
    }

    private void initFloatingButton() {
        loadingButton = (LoadingButton) findViewById(R.id.fab);
        loadingButton.setStartListener(new LoadingButton.StartListener() {
            @Override
            public void onStart() {
                onFabPressed();
            }
        });
    }

    //// Events

    private void onPagerPageChanged() {
        updateToolbarBackButton();
    }

    private void onFabPressed() {
        loadQuizletSets();
    }

    private void onSortOrderChanged(Preferences.SortOrder sortOrder, Sortable fragment) {
        if (fragment instanceof QuizletTermListFragment) {
            Preferences.setQuizletTermSortOrder(sortOrder);
        }
    }

    private void onCourseHolderChanged() {
        supportInvalidateOptionsMenu();
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
        List<Card> cards = getReadyCards();
        learnMenuItem.setEnabled(cards.size() > 0);

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
    public void onBackStackChanged() {
        updateToolbarBackButton();
        updateTabs();
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        final Object module = getCurrentModule();
        if (module != null && module instanceof StackModule) {
            StackModule stackModule = (StackModule)module;
            if (stackModule.getSize() > 0) {
                stackModule.pop(null);
                return;
            }
        }

        super.onBackPressed();
    }

    //// Actions

    private void loadQuizletSets() {
        CachableHttpLoadTask.CacheMode cacheMode = CachableHttpLoadTask.CacheMode.ONLY_STORE_TO_CACHE;
        final ServiceCommandProxy commandProxy = getQuizletService().loadSets(cacheMode, loadingButton.startLoading());

        loadingButton.setCancelListener(new LoadingButton.CancelListener() {
            @Override
            public void onCancel() {
                // while authorization it could be null
                if (!commandProxy.isEmpty()) {
                    getQuizletService().cancel(commandProxy.getServiceCommand());
                }
            }
        });
    }

    private void handleLoadedQuizletSets() {
        forceLoadSetsIfNeeded();
    }

    private void forceLoadSetsIfNeeded() {
        if (getQuizletService().getState() == QuizletService.State.Restored) {
            loadQuizletSets();
        }
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

    private void startLearnNewWords(@NonNull Course course) {
        startLearnActivity(course.getNotStartedCards());
    }

    private void startLearnActivity(@NonNull List<Card> cards) {
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

    private void updateToolbarBackButton() {
        /*StackFragment stackFragment = getStackContainer(pager.getCurrentItem());

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
        }*/
    }

    private void updateTabs() {
        //pagerAdapter.notifyDataSetChanged();
    }

    //// Interface

    // PagerModuleListener

    @Override
    public int getPageCount() {
        return 1;
    }

    @Override
    public void onCurrentPageChanged() {
    }

    // QuizletService.QuizletServiceListener

    @Override
    public void onStateChanged(QuizletService service, QuizletService.State oldState) {
        handleLoadedQuizletSets();
    }

    @Override
    public void onLoadError(QuizletService service, Error error) {
        boolean isCancelled = error instanceof CancelError;
        if (error instanceof Authorizer.AuthError) {
            isCancelled = ((Authorizer.AuthError)error).getReason() == Authorizer.AuthError.Reason.Cancelled;
        }

        if (!isCancelled) {
            showLoadErrorSnackBar(error);
        }

        loadingButton.stopLoading();
    }

    // CourseHolder.CourseHolderListener

    @Override
    public void onLoaded(CourseHolder holder) {
        onCourseHolderChanged();
    }

    @Override
    public void onCoursesAdded(@NonNull CourseHolder holder, @NonNull List<Course> courses) {
        onCourseHolderChanged();
    }

    @Override
    public void onCoursesRemoved(@NonNull CourseHolder holder, @NonNull List<Course> courses) {
        onCourseHolderChanged();
    }

    @Override
    public void onCourseUpdated(@NonNull CourseHolder holder, @NonNull Course course, @NonNull CourseHolder.UpdateBatch batch) {
        onCourseHolderChanged();
    }

    // QuizletStackCardsFragment.Listener

    /*
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

    @Nullable
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
    */

    // QuizletStackFragment.Listener

    @Override
    public void onCourseChanged(Course course, @Nullable Exception exception) {
        if (exception != null) {
            showAppExceptionSnackBar(exception);
        }

        updateCoursesIfNeeded();
        onCourseHolderChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LearnActivity.ACTIVITY_RESULT) {
            updateCoursesIfNeeded();
            supportInvalidateOptionsMenu();
        }
    }

    // CourseStackFragment.Listener

    @Override
    public void onCourseClicked(@NonNull Course course) {
        List<Card> cards = course.getReadyToLearnCards();
        if (cards.size() > 0) {
            startLearnActivity(cards);
        } else if (course.getCards().size() > 0){
            startLearnActivity(course.getCards());
        }
    }

    @Override
    public void onLearnNewWordsClick(@NonNull Course course) {
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
            public void onDataDeleted(QuizletTerm data, Exception exception) {
            }

            @Override
            public void onCourseCreated(@NonNull Course course, Exception exception) {
                MainActivity.this.onCourseChanged(course, exception);
            }

            @Override
            public void onCardsAdded(@NonNull Course course, Exception exception) {
                MainActivity.this.onCourseChanged(course, exception);
            }

            @Nullable
            @Override
            public ViewGroup getDialogContainer() {
                return null;//(ViewGroup) getCurrentFragmentView();
            }

            @Override
            public void onCourseChanged(@NonNull Course course) {
                MainActivity.this.onCourseChanged(course, null);
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
        /*
        final QuizletTermListFragment fragment = QuizletTermListFragment.create();
        fragment.setSortOrder(Preferences.getQuizletTermSortOrder());
        fragment.setListener(createMenuListener());

        return fragment;
        */

        return null;
    }

    @NonNull
    private CourseListStackFragment createCourseStackFragment() {
        CourseListStackFragment fragment = new CourseListStackFragment();
        fragment.setListener(this);

        return fragment;
    }

    //// Setters

    private void setSortOrder(Preferences.SortOrder sortOrder) {
        Object module = getCurrentModule();

        if (module instanceof Sortable) {
            Sortable sortable = (Sortable)module;
            sortable.setSortOrder(sortOrder);
            onSortOrderChanged(sortOrder, sortable);
        }
    }

    //// Getters

    // App getters

    @NonNull
    private MainApplication getMainApplication() {
        return (MainApplication)getApplication();
    }

    @NonNull
    public TaskManager getTaskManager() {
        return getMainApplication().getTaskManager();
    }

    @NonNull
    public AccountStore getAccountStore() {
        return getMainApplication().getAccountStore();
    }

    @NonNull
    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    @NonNull
    public QuizletService getQuizletService() {
        return getMainApplication().getQuizletService();
    }

    // Data getters

    @NonNull
    private List<Card> getReadyCards() {
        ArrayList<Card> cards = new ArrayList<>();
        for (Course course : getCourseHolder().getCourses()) {
            cards.addAll(course.getReadyToLearnCards());
        }

        return cards;
    }

    private Preferences.SortOrder getCurrentSortOrder() {
        Preferences.SortOrder sortOrder = Preferences.SortOrder.BY_NAME;
        Object module = getCurrentModule();

        if (module instanceof Sortable) {
            Sortable sortableModule = (Sortable)module;
            sortOrder = sortableModule.getSortOrder();
        }

        return sortOrder;
    }

    // UI getters

    @Nullable
    private Object getCurrentModule() {
        return getModule(pagerModule.getCurrentIndex());
    }

    /*
    @Nullable
    private View getCurrentFragmentView() {
        Fragment fragment = pagerAdapter.getFragment(pager.getCurrentItem());
        return fragment != null ? fragment.getView() : null;
    }
    */

    @Nullable
    private StackModule getQuizletStackModule() {
        return getStackModule(0);
    }

    @Nullable
    private StackModule getCourseListStackModule() {
        return getStackModule(2);
    }

    @Nullable
    private StackModule getStackModule(int position) {
        StackModule result = null;

        Object module = getModule(position);
        if (module instanceof StackModule) {
            result = (StackModule)module;
        }

        return result;
    }

    @Nullable
    private ListModuleInterface getTermListQuizletModule() {
        return (ListModuleInterface)getModule(1);
    }

    @Nullable
    private Object getModule(int i) {
        return pagerModule.getModuleAtIndex( i);
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

    class Behavior extends CoordinatorLayout.Behavior {
        public Behavior() {
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    }
}
