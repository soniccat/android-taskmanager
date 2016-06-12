package com.example.alexeyglushkov.wordteacher;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.alexeyglushkov.service.CachableHttpLoadTask;
import com.example.alexeyglushkov.service.SimpleService;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import learning.LearnActivity;
import main.BaseActivity;
import main.MainApplication;
import model.Card;
import model.Course;
import model.CourseHolder;

// TODO: consider moving content to fragment
public class MainActivity extends BaseActivity implements MainPageAdapter.Listener, QuizletStackFragment.Listener, CourseStackFragment.Listener {
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
        CourseFragment course = getCourseFragment();
        if (course != null) {
            updateCourses(course);
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
            quizletFragment.setListener(this);

        } else if (fragment instanceof CourseStackFragment) {
            CourseStackFragment courseFragment = (CourseStackFragment) fragment;
            courseFragment.setListener(this);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.getItem(0);
        List<Card> cards = getReadyCards();
        item.setVisible(cards.size() > 0);

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

        } else if (id == R.id.learn_ready_words) {
            startLearnActivity(getReadyCards());
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        fragment.setListener(this);
        return fragment;
    }

    @NonNull
    private CourseStackFragment createCourseStackFragment() {
        CourseStackFragment fragment = new CourseStackFragment();
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
            CourseStackFragment courseStackFragment = getCourseStackFragment();
            if (courseStackFragment != null) {
                title = courseStackFragment.getTitle();
            } else if (isDefault) {
                title = CourseStackFragment.DEFAULT_TITLE;
            }
        }

        return title;
    }

    @Override
    public void onBackStackChanged() {
        updateToolbarBackButton();
        updateTabs();
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
        }, CachableHttpLoadTask.CacheMode.LOAD_IF_ERROR_THEN_CHECK_CACHE);
    }

    private void onQuizletSetsLoaded(Error error) {
        if (error != null) {
            showErrorSnackBar(error);
        } else {
            handleLoadedQuizletSets();
        }
    }

    private void handleLoadedQuizletSets() {
        List<QuizletSet> sets = getSortedSets();
        Log.d("load", "handleLoadedQuizletSets " + sets.size());

        getQuizletStackFragment().updateSets(sets);
        getCardQuizletFragment().updateSets(sets);
    }

    private List<QuizletSet> getSortedSets() {
        List<QuizletSet> sets = getQuizletService().getSets();
        Collections.sort(sets, new Comparator<QuizletSet>() {
            @Override
            public int compare(QuizletSet lhs, QuizletSet rhs) {
                return reverseLongCompare(lhs.getCreateDate(), rhs.getCreateDate());
            }
        });

        return sets;
    }

    public static int reverseLongCompare(long lhs, long rhs) {
        return lhs < rhs ? 1 : (lhs == rhs ? 0 : -1);
    }

    private CourseFragment getCourseFragment() {
        CourseFragment result = null;
        StackFragment container = getCourseStackFragment();
        if (container != null) {
            result = (CourseFragment)container.getFragment();
        }
        return result;
    }

    private QuizletStackFragment getQuizletStackFragment() {
        return (QuizletStackFragment)getStackContainer(0);
    }

    private CourseStackFragment getCourseStackFragment() {
        return (CourseStackFragment)getStackContainer(2);
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
        if (error instanceof Service.AuthError) {
            errorString = getString(R.string.error_auth_error);
        } else {
            errorString = getString(R.string.error_load_error);
        }

        Snackbar.make(pager.getChildAt(pager.getCurrentItem()), errorString, Snackbar.LENGTH_LONG).show();
    }

    private void startLearnActivity(Course course) {
        startLearnActivity(course.getCards());
    }

    private void startLearnReadyWords(Course course) {
        startLearnActivity(course.getReadyToLearnCards());
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
                updateCourses(courseFragment);
            }
        }
    }

    private void updateCourses(CourseFragment courseFragment) {
        ArrayList<Course> courses = getCourseHolder().getCourses();
        Collections.sort(courses, new Comparator<Course>() {
            @Override
            public int compare(Course lhs, Course rhs) {
                return rhs.getCreateDate().compareTo(lhs.getCreateDate());
            }
        });

        courseFragment.setCourses(courses);
    }

    private void showCourseContent(Course course) {
        getCourseStackFragment().showCardsFragment(course);
    }

    private void deleteCourseWithConfirmation(final Course course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_delete_confirmation);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCourse(course);
            }
        });

        builder.show();
    }

    private void deleteCardWithConfirmation(final Card card) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_delete_confirmation);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCard(card);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }

    private void deleteCourse(Course course) {
        CourseFragment courseFragment = getCourseFragment();
        if (getCourseHolder().removeCourse(course) == null) {
            if (courseFragment != null) {
                courseFragment.deleteCourse(course);
            }
        }
    }

    private void deleteCard(Card card) {
        CourseFragment courseFragment = getCourseFragment();
        if (getCourseHolder().removeCard(card)) {
            if (courseFragment != null) {
                courseFragment.deleteCard(card);
            }
        }
    }

    // Callbacks

    @Override
    public void onSetClicked(QuizletSet set) {
        // implemented in QuizletStackCardsFragment
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
        if (course.getCards().size() > 0) {
            startLearnActivity(course);
        }
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
        if (course.getReadyToLearnCards().size() > 0) {
            popupMenu.getMenu().add(Menu.NONE, R.id.learn_ready_words, 0, R.string.menu_course_learn_only_ready_words);
        }
        if (course.getNotStartedCards().size() > 0) {
            popupMenu.getMenu().add(Menu.NONE, R.id.learn_new_words, 0, R.string.menu_course_learn_only_new_words);
        }
        popupMenu.getMenu().add(Menu.NONE, R.id.edit_course, 0, R.string.menu_course_show_cards);
        popupMenu.getMenu().add(Menu.NONE, R.id.delete_course, 0, R.string.menu_course_delete);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.learn_ready_words) {
                    startLearnReadyWords(course);

                } else if (item.getItemId() == R.id.learn_new_words) {
                    startLearnNewWords(course);

                } else if (item.getItemId() == R.id.edit_course) {
                    showCourseContent(course);

                } else if (item.getItemId() == R.id.delete_course) {
                    deleteCourseWithConfirmation(course);

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
    public void onCardMenuClicked(final Card card, View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add(Menu.NONE, R.id.delete_card, 0, R.string.menu_card_delete);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.delete_card) {
                    deleteCardWithConfirmation(card);
                }

                return false;
            }
        });

        popupMenu.show();
    }
}
