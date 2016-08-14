package quizletfragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
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
    private QuizletSetFragmentMenuListener getMenuListener() {
        return new QuizletSetFragmentMenuListener(getContext(), getCourseHolder(), new QuizletSetFragmentMenuListener.Listener() {
            @Override
            public void onRowClicked(QuizletSet data) {
                showWordFragment(set);
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            showSetFragment();
        } else {
            restoreListeners();
        }
    }

    public void setSets(List<QuizletSet> sets) {
        QuizletSetListFragment setFragment = getSetFragment();
        setFragment.setSets(sets);
    }

    public boolean hasData() {
        QuizletSetListFragment setFragment = getSetFragment();
        return setFragment.hasSets();
    }

    private void showSetFragment() {
        QuizletTermListFragment setFragment = new QuizletTermListFragment();
        setFragment.setListener(getMenuListener());

        addFragment(setFragment, null);
    }

    private void showWordFragment(QuizletSet set) {
        QuizletTermListFragment fragment = new QuizletTermListFragment();
        fragment.setListener(getMenuListener());
        fragment.setParentSet(set);

        addFragment(fragment, new TransactionCallback() {
            @Override
            public void onFinished() {
            }
        });
    }

    private void restoreListeners() {
        QuizletSetListFragment setFragment = getSetFragment();
        if (setFragment != null) {
            setFragment.setListener(getMenuListener());
        }

        QuizletTermListFragment cardsFragment = getCardsFragment();
        if (cardsFragment != null) {
            cardsFragment.setListener(getMenuListener());
        }
    }

    public void setSortOrder(Preferences.SortOrder sortOrder) {
        QuizletTermListFragment fragment = (QuizletTermListFragment) getTopFragment();
        if (fragment != null) {
            fragment.setSortOrder(sortOrder);
        }
    }

    public Preferences.SortOrder getSortOrder() {
        QuizletTermListFragment fragment = (QuizletTermListFragment) getTopFragment();
        Preferences.SortOrder sortOrder = Preferences.SortOrder.BY_NAME;
        if (fragment != null) {
            sortOrder = fragment.getSortOrder();
        }

        return sortOrder;
    }

    private QuizletSetListFragment getSetFragment() {
        List<Fragment> list = getChildFragmentManager().getFragments();
        return list != null && list.size() > 0 ? (QuizletSetListFragment)list.get(0) : null;
    }

    private QuizletTermListFragment getCardsFragment() {
        List<Fragment> list = getChildFragmentManager().getFragments();
        return list != null && list.size() > 1 ? (QuizletTermListFragment)list.get(1) : null;
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
