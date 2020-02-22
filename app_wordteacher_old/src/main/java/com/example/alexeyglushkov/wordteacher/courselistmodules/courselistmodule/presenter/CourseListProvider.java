package com.example.alexeyglushkov.wordteacher.courselistmodules.courselistmodule.presenter;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.alexeyglushkov.wordteacher.listmodule.SimpleStorableListProvider;
import com.example.alexeyglushkov.wordteacher.model.Course;
import com.example.alexeyglushkov.wordteacher.model.CourseHolder;

/**
 * Created by alexeyglushkov on 06.08.16.
 */
public class CourseListProvider extends SimpleStorableListProvider<Course> {
    public static final String STORE_COURSE_IDS = "STORE_COURSE_IDS";

    //// Initialization
    public CourseListProvider(List<Course> items) {
        super(items);
    }

    public CourseListProvider(Bundle bundle, Object context) {
        super(null);
        restore(bundle, context);
    }

    //// Interface methods

    // StorableListProvider

    @Override
    public void store(Bundle bundle) {
        bundle.putStringArrayList(STORE_COURSE_IDS, getCourseIds(items));
    }

    static boolean canRestore(Bundle bundle) {
        return bundle != null && bundle.containsKey(STORE_COURSE_IDS);
    }

    @Override
    public void restore(Bundle bundle, Object context) {
        List<String> courseIds = bundle.getStringArrayList(STORE_COURSE_IDS);
        items = getCourses(courseIds, (CourseHolder)context);
    }

    //// Getters

    private ArrayList<String> getCourseIds(List<Course> courses) {
        final ArrayList<String> courseIds = new ArrayList<>();
        for (Course course : courses) {
            courseIds.add(course.getId().toString());
        }

        return courseIds;
    }

    private List<Course> getCourses(List<String> courseIds, CourseHolder courseHolder) {
        final List<Course> courses = new ArrayList<>();
        for (String udid : courseIds) {
            Course course = courseHolder.getCourse(UUID.fromString(udid));
            if (course != null) {
                courses.add(course);
            }
        }

        return courses;
    }
}
