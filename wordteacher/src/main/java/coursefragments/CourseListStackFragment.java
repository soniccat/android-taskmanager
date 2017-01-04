package coursefragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import stackmodule.view.StackFragment;

import coursefragments.cards.CardListFragment;
import coursefragments.cards.CardListFragmentMenuListener;
import coursefragments.courses.CourseListFragment;
import coursefragments.courses.CourseListFragmentMenuListener;
import main.MainApplication;
import main.Preferences;
import model.Card;
import model.Course;
import model.CourseHolder;
import tools.Sortable;

/**
 * Created by alexeyglushkov on 12.06.16.
 */
public class CourseListStackFragment extends StackFragment implements Sortable {

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
        /*CardListFragment cardFragment = getCardListFragment();
        if (cardFragment != null) {
            cardFragment.setListener(getMenuCardsListener());
        }*/
    }

    private void restoreCourseListListener() {
        /*CourseListFragment setFragment = getCourseFragment();
        if (setFragment != null) {
            setFragment.setListener(getMenuCourseListener());
        }*/
    }

    //// Events

    private void onSortOrderChanged(Preferences.SortOrder sortOrder, Sortable fragment) {
        if (fragment instanceof CardListFragment) {
            Preferences.setCardListSortOrder(sortOrder);

        } else if (fragment instanceof CourseListFragment) {
            Preferences.setCourseListSortOrder(sortOrder);
        }
    }

    //// Actions

    public void reloadCourses() {
        /*CourseListFragment courseListFragment = getCourseFragment();
        if (courseListFragment != null) {
            applyPendingOperation(courseListFragment);
            courseListFragment.reload();
        }*/
    }

    public void reloadCards() {
        /*CardListFragment cardFragment = getCardListFragment();
        if (cardFragment != null) {
            cardFragment.reload();
        }*/
    }

    public void applyPendingOperation(CourseListFragment fragment) {
        //CourseListFragmentMenuListener listener = (CourseListFragmentMenuListener)fragment.getListener();
        //listener.applyPendingOperation();
    }

    // Show UI Actions

    private void showCourseFragment() {
        /*
        CourseListFragment courseListFragment = CourseListFragment.create();
        courseListFragment.setSortOrder(Preferences.getCourseListSortOrder());
        courseListFragment.setListener(getMenuCourseListener());

        addFragment(courseListFragment, null);
        */
    }

    public void showCardListFragment(Course course) {
        /*CardListFragment fragment = CardListFragment.create();
        fragment.setSortOrder(Preferences.getCardListSortOrder());
        fragment.setListener(getMenuCardsListener());
        fragment.setParentCourse(course);

        addFragment(fragment, null);*/
    }

    //// Creation Methods

    /*
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
            public void onDataDeleted(Course course, Exception exception) {
            }

            @Override
            public View getSnackBarViewContainer() {
                return getStackModuleItemView();
            }
        });
    }
    */

    /*
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
            public void onDataDeleted(Card data, Exception exception) {
                if (getBackStackSize() == 0) {
                    reloadCourses();
                }
            }

            @Override
            public View getSnackBarViewContainer() {
                return getStackModuleItemView();
            }
        });
    }*/

    //// Interfaces

    // Sortable

    @Override
    public Preferences.SortOrder getSortOrder() {
        Sortable fragment = (Sortable) getTopFragment();
        return fragment != null ? fragment.getSortOrder() : null;
    }

    public void setSortOrder(Preferences.SortOrder sortOrder) {
        Sortable fragment = (Sortable) getTopFragment();
        if (fragment != null) {
            fragment.setSortOrder(sortOrder);
        }

        onSortOrderChanged(sortOrder, fragment);
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

    public @NonNull String getTitle() {
        String title = null;
        /*if (getBackStackSize() > 0) {
            Course course = getCardListFragment().getParentCourse();
            if (course != null) {
                title = course.getTitle();
            }
        } else*/ {
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
        /*CourseListFragment courseListFragment = getCourseFragment();
        if (courseListFragment != null) {
            result = courseListFragment.hasItems();
        }*/

        return result;
    }

    // Cast Getters

    /*private CourseListStackFragment.Listener getCourseListener() {
        return (CourseListStackFragment.Listener)listener;
    }*/

    //// Inner Interfaces

    public interface Listener extends StackFragment.Listener {
        void onCourseClicked(Course course);
        void onLearnNewWordsClick(Course course);
    }
}
