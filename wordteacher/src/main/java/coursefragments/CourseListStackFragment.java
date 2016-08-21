package coursefragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.alexeyglushkov.wordteacher.StackFragment;

import coursefragments.cards.CardListFragment;
import coursefragments.cards.CardListFragmentMenuListener;
import coursefragments.courses.CourseListFragment;
import coursefragments.courses.CourseListFragmentMenuListener;
import main.MainApplication;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 12.06.16.
 */
public class CourseListStackFragment extends StackFragment {

    public static final String DEFAULT_TITLE = "Courses";

    //// Creation, initialization, restoration

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            showCourseFragment();
        } else {
            restoreListeners();
        }
    }

    private void restoreListeners() {
        restoreCourseListListener();
        restoreCardListListener();
    }

    private void restoreCardListListener() {
        CardListFragment cardFragment = getCardListFragment();
        if (cardFragment != null) {
            cardFragment.setListener(getMenuCourseListener());
        }
    }

    private void restoreCourseListListener() {
        CourseListFragment setFragment = getCourseFragment();
        if (setFragment != null) {
            setFragment.setListener(getMenuCourseListener());
        }
    }

    //// Actions

    public void reloadCourses() {
        CourseListFragment courseListFragment = getCourseFragment();
        if (courseListFragment != null) {
            applyPendingOperation(courseListFragment);
            courseListFragment.reload();
        }
    }

    public void reloadCards() {
        CardListFragment cardFragment = getCardListFragment();
        if (cardFragment != null) {
            cardFragment.reload();
        }
    }

    public void applyPendingOperation(CourseListFragment fragment) {
        CourseListFragmentMenuListener listener = (CourseListFragmentMenuListener)fragment.getListener();
        listener.applyPendingOperation();
    }

    // Show UI Actions

    private void showCourseFragment() {
        CourseListFragment courseListFragment = new CourseListFragment();
        courseListFragment.setListener(getMenuCourseListener());

        addFragment(courseListFragment, null);
    }

    public void showCardListFragment(Course course) {
        CardListFragment fragment = new CardListFragment();
        fragment.setListener(getMenuCardsListener());
        fragment.setParentCourse(course);

        addFragment(fragment, null);
    }

    //// Creation Methods

    private CourseListFragmentMenuListener getMenuCourseListener() {
        return new CourseListFragmentMenuListener(getContext(), getCourseHolder(), new CourseListFragmentMenuListener.Listener() {
            @Override
            public void onCourseDeleteClicked(Course course) {
                getCourseFragment().deleteView(course);
            }

            @Override
            public void onShowCourseContentClicked(Course course) {
                showCardListFragment(course);
            }

            @Override
            public void onRowClicked(Course course) {
                CourseListStackFragment.this.getCourseListener().onCourseClicked(course);
            }

            @Override
            public void onLearnNewWordsClick(Course course) {
                CourseListStackFragment.this.getCourseListener().onLearnNewWordsClick(course);
            }

            @Override
            public void onDataDeletionCancelled(Course course) {
                reloadCourses();
            }

            @Override
            public void onDataDeleted(Course course) {
            }

            @Override
            public View getSnackBarViewContainer() {
                return getView();
            }
        });
    }

    private CardListFragmentMenuListener getMenuCardsListener() {
        return new CardListFragmentMenuListener(getContext(), getCourseHolder(), new CardListFragmentMenuListener.Listener() {
            @Override
            public void onCardDeleteClicked(Card data) {
                getCardListFragment().deleteView(data);
            }

            @Override
            public void onRowClicked(Card data) {

            }

            @Override
            public void onDataDeletionCancelled(Card data) {
                reloadCards();
            }

            @Override
            public void onDataDeleted(Card data) {
                if (getBackStackSize() == 0) {
                    reloadCourses();
                }
            }

            @Override
            public View getSnackBarViewContainer() {
                return getView();
            }
        });
    }

    //// Getters

    // App Getters

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    // Data Getters

    public String getTitle() {
        String title = null;
        if (getBackStackSize() > 0) {
            Course course = getCardListFragment().getParentCourse();
            if (course != null) {
                title = course.getTitle();
            }
        } else {
            title = DEFAULT_TITLE;
        }

        return title;
    }

    // UI Getters

    public CourseListFragment getCourseFragment() {
        return (CourseListFragment)getFragment(0);
    }

    public CardListFragment getCardListFragment() {
        return (CardListFragment)getFragment(1);
    }


    // Statuses

    public boolean hasCourses() {
        boolean result = false;
        CourseListFragment courseListFragment = getCourseFragment();
        if (courseListFragment != null) {
            result = courseListFragment.hasItems();
        }

        return result;
    }

    // Cast Getters

    private CourseListStackFragment.Listener getCourseListener() {
        return (CourseListStackFragment.Listener)listener;
    }

    //// Inner Interfaces

    public interface Listener extends StackFragment.Listener {
        void onCourseClicked(Course course);
        void onLearnNewWordsClick(Course course);
    }
}
