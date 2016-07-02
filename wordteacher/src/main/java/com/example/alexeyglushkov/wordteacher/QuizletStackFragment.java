package com.example.alexeyglushkov.wordteacher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.ArrayList;
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
    private QuizletFragmentMenuListener getMenuListener() {
        return new QuizletFragmentMenuListener(getContext(), getCourseHolder(), new QuizletFragmentMenuListener.Listener() {
            @Override
            public void onSetClicked(QuizletSet set) {
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
                return (ViewGroup) getFragment().getView();
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

    public void updateSets(List<QuizletSet> sets) {
        QuizletCardsFragment setFragment = getSetFragment();
        setFragment.updateSets(sets);
    }

    private void showSetFragment() {
        QuizletCardsFragment setFragment = new QuizletCardsFragment();
        setFragment.setViewType(QuizletCardsFragment.ViewType.Sets);
        setFragment.setListener(getMenuListener());

        addFragment(setFragment, null);
    }

    private void showWordFragment(QuizletSet set) {
        QuizletCardsFragment fragment = new QuizletCardsFragment();
        fragment.setListener(getMenuListener());
        fragment.setViewType(QuizletCardsFragment.ViewType.Cards);

        ArrayList<QuizletSet> list = new ArrayList<>();
        list.add(set);

        fragment.setParentSet(set);
        fragment.updateSets(list);

        addFragment(fragment, new TransactionCallback() {
            @Override
            public void onFinished() {
            }
        });
    }

    private void restoreListeners() {
        QuizletCardsFragment setFragment = getSetFragment();
        if (setFragment != null) {
            setFragment.setListener(getMenuListener());
        }

        QuizletCardsFragment cardsFragment = getCardsFragment();
        if (cardsFragment != null) {
            cardsFragment.setListener(getMenuListener());
        }
    }

    public void setSortOrder(Preferences.SortOrder sortOrder) {
        QuizletCardsFragment fragment = (QuizletCardsFragment)getFragment();
        if (fragment != null) {
            fragment.setSortOrder(sortOrder);
        }
    }

    public Preferences.SortOrder getSortOrder() {
        QuizletCardsFragment fragment = (QuizletCardsFragment)getFragment();
        Preferences.SortOrder sortOrder = Preferences.SortOrder.BY_NAME;
        if (fragment != null) {
            sortOrder = fragment.getSortOrder();
        }

        return sortOrder;
    }

    private QuizletCardsFragment getSetFragment() {
        List<Fragment> list = getChildFragmentManager().getFragments();
        return list != null && list.size() > 0 ? (QuizletCardsFragment)list.get(0) : null;
    }

    private QuizletCardsFragment getCardsFragment() {
        List<Fragment> list = getChildFragmentManager().getFragments();
        return list != null && list.size() > 1 ? (QuizletCardsFragment)list.get(1) : null;
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
