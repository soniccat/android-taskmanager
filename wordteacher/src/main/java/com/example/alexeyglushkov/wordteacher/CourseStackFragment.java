package com.example.alexeyglushkov.wordteacher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import model.Card;
import model.Course;

/**
 * Created by alexeyglushkov on 12.06.16.
 */
public class CourseStackFragment extends StackFragment implements CourseFragment.Listener {

    public static final String DEFAULT_TITLE = "Courses";

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
            //onBackStackChanged();
        }
    }

    public void updateCourses(ArrayList<Course> courses) {
        CourseFragment courseFragment = getCourseFragment();
        courseFragment.setCourses(courses);
    }

    private void showCourseFragment() {
        CourseFragment courseFragment = new CourseFragment();
        courseFragment.setViewType(CourseFragment.ViewType.Courses);
        courseFragment.setListener(this);

        addFragment(courseFragment, new TransactionCallback() {
            @Override
            public void onFinished() {
            }
        });
    }

    public void showCardsFragment(Course course) {
        CourseFragment fragment = new CourseFragment();
        fragment.setListener(this);
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
            setFragment.setListener(this);
        }

        CourseFragment cardsFragment = getCardsFragment();
        if (cardsFragment != null) {
            cardsFragment.setListener(this);
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

    private Listener getStackListener() {
        return (Listener)this.listener;
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

    // CourseFragment.Listener


    @Override
    public void onCourseClicked(Course course) {
        getStackListener().onCourseClicked(course);
    }

    @Override
    public void onCourseMenuClicked(Course set, View view) {
        getStackListener().onCourseMenuClicked(set, view);
    }

    @Override
    public void onCardClicked(Card card) {
        getStackListener().onCardClicked(card);
    }

    @Override
    public void onCardMenuClicked(Card card, View view) {
        getStackListener().onCardMenuClicked(card, view);
    }

    public interface Listener extends CourseFragment.Listener, StackFragment.Listener {
    }
}
