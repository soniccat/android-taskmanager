package quizletfragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.StackFragment;

import java.util.List;

import main.MainApplication;
import main.Preferences;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 11.06.16.
 */
public class QuizletStackFragment extends StackFragment {

    public static final String DEFAULT_TITLE = "Sets";

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    private QuizletStackFragment.Listener getQuizletListener() {
        return (QuizletStackFragment.Listener)this.listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    private QuizletSetFragmentMenuListener getSetMenuListener() {
        return new QuizletSetFragmentMenuListener(getContext(), getCourseHolder(), new QuizletSetFragmentMenuListener.Listener<QuizletSet>() {
            @Override
            public void onRowClicked(QuizletSet set) {
                showWordFragment(set);
            }

            @Override
            public void onDataDeletionCancelled(QuizletSet data) {

            }

            @Override
            public void onDataDeleted(QuizletSet data) {

            }

            @Override
            public void onCourseCreated(Course course) {
                getQuizletListener().onCourseChanged(course);
            }

            @Override
            public void onCardsAdded(Course course) {
                getQuizletListener().onCourseChanged(course);
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

    private QuizletTermFragmentMenuListener getTermMenuListener() {
        return new QuizletTermFragmentMenuListener(getContext(), getCourseHolder(), new QuizletTermFragmentMenuListener.Listener<QuizletTerm>() {
            @Override
            public void onCourseCreated(Course course) {
                getQuizletListener().onCourseChanged(course);
            }

            @Override
            public void onCourseChanged(Course course) {

            }

            @Override
            public void onCardsAdded(Course course) {
                getQuizletListener().onCourseChanged(course);
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
            public void onDataDeleted(QuizletTerm data) {

            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            showSetFragment();
        } else {
            restoreListeners();
        }
    }

    public void updateSets() {
        QuizletSetListFragment setFragment = getSetFragment();
        if (setFragment != null) {
            setFragment.reload();
        }
    }

    public boolean hasData() {
        QuizletSetListFragment setFragment = getSetFragment();
        return setFragment.hasSets();
    }

    private void showSetFragment() {
        QuizletSetListFragment setFragment = new QuizletSetListFragment();
        setFragment.setListener(getSetMenuListener());

        addFragment(setFragment, null);
    }

    private void showWordFragment(QuizletSet set) {
        QuizletTermListFragment fragment = new QuizletTermListFragment();
        fragment.setListener(getTermMenuListener());
        fragment.setTermSet(set);

        addFragment(fragment, new TransactionCallback() {
            @Override
            public void onFinished() {
            }
        });
    }

    private void restoreListeners() {
        QuizletSetListFragment setFragment = getSetFragment();
        if (setFragment != null) {
            setFragment.setListener(getSetMenuListener());
        }

        QuizletTermListFragment cardsFragment = getCardsFragment();
        if (cardsFragment != null) {
            cardsFragment.setListener(getTermMenuListener());
        }
    }

    public void setSortOrder(Preferences.SortOrder sortOrder) {
        QuizletSortable fragment = (QuizletSortable) getTopFragment();
        if (fragment != null) {
            fragment.setSortOrder(sortOrder);
        }
    }

    public Preferences.SortOrder getSortOrder() {
        QuizletSortable fragment = (QuizletSortable) getTopFragment();
        Preferences.SortOrder sortOrder = Preferences.SortOrder.BY_NAME;
        if (fragment != null) {
            sortOrder = fragment.getSortOrder();
        }

        return sortOrder;
    }

    private QuizletSetListFragment getSetFragment() {
        return (QuizletSetListFragment)getFragment(0);
    }

    private QuizletTermListFragment getCardsFragment() {
        return (QuizletTermListFragment)getFragment(1);
    }

    public String getTitle() {
        String title = null;
        if (getBackStackSize() > 0) {
            title = getCardsFragment().getParentSet().getTitle();
        } else {
            title = DEFAULT_TITLE;
        }

        return title;
    }

    public interface Listener extends StackFragment.Listener {
        void onCourseChanged(Course course);
    }
}
