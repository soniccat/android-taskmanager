package com.example.alexeyglushkov.wordteacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.service.Service;
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.service.SimpleService;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import learning.LearnActivity;
import main.BaseActivity;
import main.MainApplication;
import model.Card;
import model.Course;
import model.CourseHolder;

// TODO: consider moving content to fragment
public class MainActivity extends BaseActivity implements QuizletCardsFragment.Listener, CourseFragment.Listener {
    private Toolbar toolbar;
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        } else {
            setOnViewReadyCallback();
        }
    }

    private void restoreListeners() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            updateFragmentListener(fragment);
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
        pagerAdapter.setListener(new MainPageAdapter.Listener() {
            @Override
            public void onStackFragmentReady(Fragment fragment, int position) {
                onPageFragmentReady(fragment);
            }
        });
        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
    }

    private void onPagerPageChanged() {
        CourseFragment course = getCourseFragment();
        if (course != null) {
            ArrayList<Course> courses = getCourseHolder().getCourses();
            course.setCourses(courses);
        }

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

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        updateFragmentListener(fragment);
    }

    public void onPageFragmentReady(Fragment fragment) {
        updateFragmentListener(fragment);
        updateToolbarBackButton();
    }

    private void updateFragmentListener(Fragment fragment) {
        // while restoration pagerAdapter could be null
        if (fragment instanceof StackContainer && pagerAdapter != null) {
            StackContainer container = (StackContainer)fragment;
            pagerAdapter.updateStackContainerListener(container);

            Fragment innerFragment = container.getFragment();
            if (innerFragment != null) {
                updateFragmentListener(innerFragment);
            }

        } else if (fragment instanceof QuizletCardsFragment) {
            QuizletCardsFragment quizletFragment = (QuizletCardsFragment)fragment;
            quizletFragment.setListener(this);

        } else if (fragment instanceof CourseFragment) {
            CourseFragment quizletFragment = (CourseFragment)fragment;
            quizletFragment.setListener(this);
        }
    }

    private void onViewReady() {
        if (getQuizletService() != null && getQuizletService().getAccount() != null && getQuizletService().getAccount().isAuthorized()) {
            Log.d("load","start load quizlet sets");
            loadQuizletSets();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Fragment frag = getCurrentFragment();
        if (frag.isVisible() && frag instanceof StackContainer) {
            StackContainer stackContainer = (StackContainer)frag;
            if (stackContainer.getBackStackSize() > 0) {
                stackContainer.popFragment(new StackContainer.TransactionCallback() {
                    @Override
                    public void onFinished(boolean isCompleted) {
                        if (isCompleted) {
                            onQuizletSetFragmentBackStackChanged();
                        }
                    }
                });
                return;
            }
        }

        super.onBackPressed();
    }

    private void onQuizletSetFragmentBackStackChanged() {
        updateToolbarBackButton();
    }

    private void updateToolbarBackButton() {
        StackContainer stackContainer = getStackContainer(pager.getCurrentItem());

        boolean needShowBackButton = false;
        if (stackContainer != null) {
            needShowBackButton = pager.getCurrentItem() == 0 && stackContainer.getBackStackSize() > 0;
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

    private Fragment getCurrentFragment() {
        return getFragment(pager.getCurrentItem());
    }

    //// Other

    private void onFabPressed() {
        loadQuizletSets();
    }

    private void loadQuizletSets() {
        getQuizletService().loadSets(new SimpleService.CommandCallback() {
            @Override
            public void onCompleted(Error error) {
                onQuizletSetsLoaded(error);
            }
        });
    }

    private void onQuizletSetsLoaded(Error error) {
        if (error != null) {
            showErrorSnackBar(error);
        } else {
            handleLoadedQuizletSets();
        }
    }

    private void handleLoadedQuizletSets() {
        List<QuizletSet> sets = getQuizletService().getSets();
        Log.d("load", "handleLoadedQuizletSets " + sets.size());

        getSetQuizletFragment().updateSets(sets);
        getCardQuizletFragment().updateSets(sets);
    }

    private QuizletCardsFragment getSetQuizletFragment() {
        QuizletCardsFragment result = null;
        StackContainer container = getStackContainer(0);
        if (container != null) {
            result = (QuizletCardsFragment)container.getFragment();
        }
        return result;
    }

    private CourseFragment getCourseFragment() {
        CourseFragment result = null;
        StackContainer container = getStackContainer(2);
        if (container != null) {
            result = (CourseFragment)container.getFragment();
        }
        return result;
    }

    private StackContainer getStackContainer(int position) {
        StackContainer result = null;

        Fragment fragment = getFragment(position);
        if (fragment instanceof StackContainer) {
            result = (StackContainer)fragment;
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
        if (error instanceof Service.AuthError) {
            errorString = getString(R.string.error_auth_error);
        } else {
            errorString = getString(R.string.error_load_error);
        }

        Snackbar.make(pager.getChildAt(pager.getCurrentItem()), errorString, Snackbar.LENGTH_LONG).show();
    }

    private void showWordFragment(QuizletSet set) {
        QuizletCardsFragment fragment = new QuizletCardsFragment();
        fragment.setListener(this);
        fragment.setViewType(QuizletCardsFragment.ViewType.Cards);

        ArrayList<QuizletSet> list = new ArrayList<>();
        list.add(set);

        fragment.setParentSet(set);
        fragment.updateSets(list);

        getStackContainer(0).showFragment(fragment, new StackContainer.TransactionCallback() {
            @Override
            public void onFinished(boolean isCompleted) {
                if (isCompleted) {
                    onQuizletSetFragmentBackStackChanged();
                }
            }
        });
    }

    private void startLearnActivity(Course course) {
        Intent activityIntent = new Intent(this, LearnActivity.class);

        String courseId = course.getId().toString();
        activityIntent.putExtra(LearnActivity.EXTRA_COURSE_ID, courseId);
        activityIntent.putExtra(LearnActivity.EXTRA_DEFINITION_TO_TERM, true);

        startActivityForResult(activityIntent, LearnActivity.ACTIVITY_RESULT);
    }

    private void startLearnReadyWords(Course course) {

    }

    private void startLearnCourse(Course course) {

    }

    private void createCourseFromCard(QuizletTerm quizletTerm, String name) {
        Card card = createCard(quizletTerm);
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(card);

        createCourse(name, cards);
    }

    @NonNull
    private Card createCard(QuizletTerm term) {
        Card card = new Card();
        card.setTerm(term.getTerm());
        card.setDefinition(term.getDefinition());
        card.setQuizletTerm(term);
        return card;
    }

    private void createCourse(String title, ArrayList<Card> cards) {
        Course course = new Course();
        course.setTitle(title);
        course.addCards(cards);

        Error error = getCourseHolder().addCourse(course);
        if (error != null) {
            CourseFragment courseFragment = getCourseFragment();
            if (courseFragment != null) {
                courseFragment.setCourses(getCourseHolder().getCourses());
            }
        }
    }

    // Callbacks

    @Override
    public void onSetClicked(QuizletSet set) {
        showWordFragment(set);
    }

    @Override
    public void onSetMenuClicked(final QuizletSet set, View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenu().add(Menu.NONE, R.id.create_set, 0, R.string.menu_create_course);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.create_set) {
                    onCreateCourseFromSet(set);
                }

                return false;
            }
        });

        popupMenu.show();
    }

    @Override
    public void onTermClicked(QuizletTerm card) {

    }

    @Override
    public void onTermMenuClicked(final QuizletTerm card, View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenu().add(Menu.NONE, R.id.create_set, 0, R.string.menu_create_course);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.create_set) {
                    onCreateCourseFromCard(card);
                }

                return false;
            }
        });

        popupMenu.show();
    }

    private void onCreateCourseFromCard(final QuizletTerm card) {
        final RenameAlert renameAlert = new RenameAlert();
        renameAlert.setPositiveButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCourseFromCard(card, renameAlert.getName());
            }
        });
        renameAlert.show(this, (ViewGroup) getWindow().getDecorView());
    }

    private void onCreateCourseFromSet(QuizletSet set) {
        ArrayList<Card> cards = new ArrayList<>();
        for (QuizletTerm term : set.getTerms()) {
            Card card = createCard(term);
            cards.add(card);
        }

        createCourse(set.getTitle(), cards);
    }

    @Override
    public void onCourseClicked(Course course) {
        startLearnActivity(course);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LearnActivity.ACTIVITY_RESULT) {
            getCourseFragment().reloadData();
        }
    }

    @Override
    public void onCourseMenuClicked(final Course course, View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenu().add(Menu.NONE, R.id.learn_ready_words, 0, R.string.menu_course_learn_only_ready_words);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.learn_ready_words) {
                    startLearnReadyWords(course);
                }

                return false;
            }
        });

        popupMenu.show();
    }

    @Override
    public void onCardClicked(Card card) {

    }

    @Override
    public void onCardMenuClicked(Card card, View view) {

    }
}
