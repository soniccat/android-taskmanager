package com.example.alexeyglushkov.wordteacher.courselistmodules.cardlistmodule.presenter;

import android.os.Bundle;

import java.util.List;
import java.util.UUID;

import listmodule.StorableListProvider;
import com.example.alexeyglushkov.wordteacher.model.Card;
import com.example.alexeyglushkov.wordteacher.model.Course;
import com.example.alexeyglushkov.wordteacher.model.CourseHolder;

/**
 * Created by alexeyglushkov on 21.08.16.
 */
public class CourseCardListProvider implements StorableListProvider<Card> {
    public final static String STORE_PARENT_COURSE_ID = "STORE_PARENT_COURSE_ID";

    private Course course;

    //// Initialization
    public CourseCardListProvider(Course course) {
        this.course = course;
    }

    public CourseCardListProvider(Bundle bundle, Object context) {
        restore(bundle, context);
    }

    //// Interface methods

    // StorableListProvider

    @Override
    public void store(Bundle bundle) {
        bundle.putString(STORE_PARENT_COURSE_ID, course.getId().toString());
    }

    static boolean canRestore(Bundle bundle) {
        return bundle != null && bundle.containsKey(STORE_PARENT_COURSE_ID);
    }

    @Override
    public void restore(Bundle bundle, Object context) {
        String strId = bundle.getString(STORE_PARENT_COURSE_ID);
        course = getCourse((CourseHolder) context, strId);
    }

    //// Getters

    private Course getCourse(CourseHolder context, String strId) {
        CourseHolder holder = context;
        return holder.getCourse(UUID.fromString(strId));
    }

    public Course getCourse() {
        return course;
    }

    @Override
    public List<Card> getList() {
        return course.getCards();
    }
}
