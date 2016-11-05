package quizletfragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import stackfragment.view.StackFragment;

import main.MainApplication;
import main.Preferences;
import model.Course;
import model.CourseHolder;
import quizletfragments.sets.QuizletSetFragmentMenuListener;
import quizletfragments.sets.QuizletSetListFragment;
import quizletfragments.terms.QuizletTermFragmentMenuListener;
import quizletfragments.terms.QuizletTermListFragment;
import tools.Sortable;

/**
 * Created by alexeyglushkov on 11.06.16.
 */
public class QuizletStackFragment extends StackFragment implements Sortable {

    public static final String DEFAULT_TITLE = "Sets";

    //// Creation, initialization, restoration

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null) {
            showSetFragment();
        } else {
            restoreListeners();
        }
    }

    private void restoreListeners() {
        QuizletSetListFragment setFragment = getSetFragment();
        if (setFragment != null) {
            setFragment.setListener(createSetMenuListener());
        }

        QuizletTermListFragment cardsFragment = getCardsFragment();
        if (cardsFragment != null) {
            cardsFragment.setListener(createTermMenuListener());
        }
    }

    //// Events

    private void onSortOrderChanged(Preferences.SortOrder sortOrder, Sortable fragment) {
        if (fragment instanceof QuizletTermListFragment) {
            Preferences.setQuizletTermSortOrder(sortOrder);

        } else if (fragment instanceof QuizletSetListFragment) {
            Preferences.setQuizletSetSortOrder(sortOrder);
        }
    }

    //// Actions

    // Show UI Actions

    private void showSetFragment() {
        /*QuizletSetListFragment setFragment = QuizletSetListFragment.create();
        setFragment.setSortOrder(Preferences.getQuizletSetSortOrder());
        setFragment.setListener(createSetMenuListener());

        addFragment(setFragment, null);
        */
    }

    private void showWordFragment(QuizletSet set) {
        /*
        QuizletTermListFragment fragment = QuizletTermListFragment.create();
        fragment.setSortOrder(Preferences.getQuizletTermSortOrder());
        fragment.setListener(createTermMenuListener());
        fragment.setTermSet(set);

        addFragment(fragment, new TransactionCallback() {
            @Override
            public void onFinished() {
            }
        });
        */
    }

    // Update Data Actions

    public void reloadSets() {
        /*
        QuizletSetListFragment setFragment = getSetFragment();
        if (setFragment != null) {
            setFragment.reload();
        }
        */
    }

    //// Creation methods

    @NonNull
    private QuizletSetFragmentMenuListener createSetMenuListener() {
        return new QuizletSetFragmentMenuListener(getContext(), getCourseHolder(), new QuizletSetFragmentMenuListener.Listener<QuizletSet>() {
            @Override
            public void onRowClicked(QuizletSet set) {
                showWordFragment(set);
            }

            @Override
            public void onDataDeletionCancelled(QuizletSet data) {

            }

            @Override
            public void onDataDeleted(QuizletSet data, Exception exception) {

            }

            @Override
            public void onCourseCreated(Course course, Exception exception) {
                getQuizletListener().onCourseChanged(course, exception);
            }

            @Override
            public void onCardsAdded(Course course, Exception exception) {
                getQuizletListener().onCourseChanged(course, exception);
            }

            @Override
            public ViewGroup getDialogContainer() {
                return (ViewGroup) getView();
            }

            @Override
            public void onCourseChanged(Course course) {

            }
        });
    }

    private QuizletTermFragmentMenuListener createTermMenuListener() {
        return new QuizletTermFragmentMenuListener(getContext(), getCourseHolder(), new QuizletTermFragmentMenuListener.Listener<QuizletTerm>() {
            @Override
            public void onCourseCreated(Course course, Exception exception) {
                getQuizletListener().onCourseChanged(course, exception);
            }

            @Override
            public void onCourseChanged(Course course) {

            }

            @Override
            public void onCardsAdded(Course course, Exception exception) {
                getQuizletListener().onCourseChanged(course, exception);
            }

            @Override
            public ViewGroup getDialogContainer() {
                return (ViewGroup) getView();
            }

            @Override
            public void onRowClicked(QuizletTerm data) {

            }

            @Override
            public void onDataDeletionCancelled(QuizletTerm data) {

            }

            @Override
            public void onDataDeleted(QuizletTerm data, Exception exception) {

            }
        });
    }

    //// Setters

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    // Set UI

    public void setSortOrder(Preferences.SortOrder sortOrder) {
        Sortable fragment = (Sortable) getTopFragment();
        if (fragment != null) {
            fragment.setSortOrder(sortOrder);
        }

        onSortOrderChanged(sortOrder, fragment);
    }

    //// Getters

    // App Data Getters

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    // Data Getters

    public Preferences.SortOrder getSortOrder() {
        Sortable fragment = (Sortable) getTopFragment();
        return fragment != null ? fragment.getSortOrder() : null;
    }

    public String getTitle() {
        String title = null;
        /*if (getBackStackSize() > 0) {
            title = getCardsFragment().getParentSet().getTitle();
        } else*/ {
            title = DEFAULT_TITLE;
        }

        return title;
    }

    // UI Getters

    private QuizletSetListFragment getSetFragment() {
        return (QuizletSetListFragment)getFragment(0);
    }

    private QuizletTermListFragment getCardsFragment() {
        return (QuizletTermListFragment)getFragment(1);
    }

    // Cast Getters

    private QuizletStackFragment.Listener getQuizletListener() {
        return (QuizletStackFragment.Listener)this.listener;
    }

    // Statuses

    public boolean hasData() {
        //QuizletSetListFragment setFragment = getSetFragment();
        //return setFragment.hasItems();

        return false;
    }

    //// Interfaces

    public interface Listener extends StackFragment.Listener {
        void onCourseChanged(Course course, Exception exception);
    }
}
