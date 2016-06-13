package com.example.alexeyglushkov.wordteacher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import main.MainApplication;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 12.06.16.
 */
public class CourseStackFragment extends StackFragment {

    public static final String DEFAULT_TITLE = "Courses";

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    private CourseStackFragment.Listener getCourseListener() {
        return (CourseStackFragment.Listener)listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            showCourseFragment();
        } else {
            restoreListeners();
        }
    }

    public void updateCourses(ArrayList<Course> courses) {
        CourseFragment courseFragment = getCourseFragment();
        courseFragment.setCourses(courses);
    }

    public void updateCards(ArrayList<Card> cards) {
        CourseFragment cardsFragment = getCardsFragment();
        cardsFragment.setCards(cards);
    }

    private void showCourseFragment() {
        CourseFragment courseFragment = new CourseFragment();
        courseFragment.setViewType(CourseFragment.ViewType.Courses);
        courseFragment.setListener(getMenuListener());

        addFragment(courseFragment, null);
    }

    public void showCardsFragment(Course course) {
        CourseFragment fragment = new CourseFragment();
        fragment.setListener(getMenuListener());
        fragment.setViewType(CourseFragment.ViewType.Cards);

        ArrayList<Course> list = new ArrayList<>();
        list.add(course);

        fragment.setParentCourse(course);
        fragment.setCourses(list);

        addFragment(fragment, new TransactionCallback() {
            @Override
            public void onFinished() {
            }
        });
    }

    private void restoreListeners() {
        CourseFragment setFragment = getCourseFragment();
        if (setFragment != null) {
            setFragment.setListener(getMenuListener());
        }

        CourseFragment cardsFragment = getCardsFragment();
        if (cardsFragment != null) {
            cardsFragment.setListener(getMenuListener());
        }
    }

    private CourseFragment getCourseFragment() {
        List<Fragment> list = getChildFragmentManager().getFragments();
        return list != null && list.size() > 0 ? (CourseFragment)list.get(0) : null;
    }

    private CourseFragment getCardsFragment() {
        List<Fragment> list = getChildFragmentManager().getFragments();
        return list != null && list.size() > 1 ? (CourseFragment)list.get(1) : null;
    }

    public void updateCourses() {
        CourseFragment courseFragment = getCourseFragment();
        if (courseFragment != null) {
            ArrayList<Course> courses = getCourseHolder().getCourses();
            Collections.sort(courses, new Comparator<Course>() {
                @Override
                public int compare(Course lhs, Course rhs) {
                    return rhs.getCreateDate().compareTo(lhs.getCreateDate());
                }
            });

            courseFragment.setCourses(courses);
        }
    }

    public String getTitle() {
        String title = null;
        if (getBackStackSize() > 0) {
            title = getCardsFragment().getParentCourse().getTitle();
        } else {
            title = DEFAULT_TITLE;
        }

        return title;
    }

    private CourseFragmentMenuListener getMenuListener() {
        return new CourseFragmentMenuListener(getContext(), getCourseHolder(), new CourseFragmentMenuListener.Listener() {
            @Override
            public void onCourseDeleteClicked(Course course) {
                getCourseFragment().deleteCourse(course);
            }

            @Override
            public void onCardDeleteClicked(Card card) {
                getCardsFragment().deleteCard(card);
            }

            @Override
            public void onShowCourseContentClicked(Course course) {
                showCardsFragment(course);
            }

            @Override
            public void onCourseClicked(Course course) {
                CourseStackFragment.this.getCourseListener().onCourseClicked(course);
            }

            @Override
            public void onLearnReadyWordsClick(Course course) {
                CourseStackFragment.this.getCourseListener().onLearnReadyWordsClick(course);
            }

            @Override
            public void onLearnNewWordsClick(Course course) {
                CourseStackFragment.this.getCourseListener().onLearnNewWordsClick(course);
            }

            @Override
            public void onCourseDeletionCancelled(Course course) {
                updateCourses();
            }

            @Override
            public void onCourseDeleted(Course course) {
                updateCourses();
            }

            @Override
            public View getSnackBarViewContainer() {
                return getView();
            }
        });
    }

    // CourseFragment.Listener

    public interface Listener extends StackFragment.Listener {
        void onCourseClicked(Course course);
        void onLearnReadyWordsClick(Course course);
        void onLearnNewWordsClick(Course course);
    }
}
