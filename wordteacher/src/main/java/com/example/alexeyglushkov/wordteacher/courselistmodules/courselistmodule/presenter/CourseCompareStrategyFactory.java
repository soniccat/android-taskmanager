package com.example.alexeyglushkov.wordteacher.courselistmodules.courselistmodule.presenter;

import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.wordteacher.model.Course;
import tools.SortOrderCompareStrategyFactory;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public class CourseCompareStrategyFactory extends SortOrderCompareStrategyFactory<Course> {
    @Override
    protected int compare(Course lhs, Course rhs, Preferences.SortOrder sortOrder) {
        switch (sortOrder) {
            case BY_NAME_INV: return rhs.getTitle().compareToIgnoreCase(lhs.getTitle());
            case BY_CREATE_DATE: return lhs.getCreateDate().compareTo(rhs.getCreateDate());
            case BY_CREATE_DATE_INV: return rhs.getCreateDate().compareTo(lhs.getCreateDate());
            default:
                //BY_NAME
                return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
        }
    }
}
