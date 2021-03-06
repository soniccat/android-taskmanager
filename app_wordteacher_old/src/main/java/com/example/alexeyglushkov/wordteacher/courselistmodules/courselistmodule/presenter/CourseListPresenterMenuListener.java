package com.example.alexeyglushkov.wordteacher.courselistmodules.courselistmodule.presenter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexeyglushkov.wordteacher.R;

import com.example.alexeyglushkov.wordteacher.listmodule.DeleteMenuListener;
import com.example.alexeyglushkov.wordteacher.model.Course;
import com.example.alexeyglushkov.wordteacher.model.CourseHolder;

/**
 * Created by alexeyglushkov on 13.06.16.
 */
public class CourseListPresenterMenuListener extends DeleteMenuListener<Course> {
    private CourseHolder courseHolder;

    public CourseListPresenterMenuListener(Context context, CourseHolder courseHolder, Listener listener) {
        super(context, listener);
        this.courseHolder = courseHolder;
    }

    //// Events

    public void onCourseViewDeleted(final Course course) {
        deleteDataWithSnackbar(course);
    }

    @Override
    public void onRowViewDeleted(Course data) {
        onCourseViewDeleted(data);
    }

    //// Actions

    protected void fillMenu(final Course course, PopupMenu popupMenu) {
        if (course.getNotStartedCards().size() > 0) {
            popupMenu.getMenu().add(Menu.NONE, R.id.learn_new_words, 0, R.string.menu_course_learn_only_new_words);
        }
        popupMenu.getMenu().add(Menu.NONE, R.id.edit_course, 0, R.string.menu_course_show_cards);
        popupMenu.getMenu().add(Menu.NONE, R.id.delete_course, 0, R.string.menu_course_delete);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.learn_new_words) {
                    getListener().onLearnNewWordsClick(course);

                } else if (item.getItemId() == R.id.edit_course) {
                    getListener().onShowCourseContentClicked(course);

                } else if (item.getItemId() == R.id.delete_course) {
                    getListener().onCourseDeleteClicked(course);
                    onCourseViewDeleted(course);
                }

                return false;
            }
        });
    }

    @Override
    protected void deleteData(Course data) throws Exception {
        courseHolder.removeCourse(data);
    }

    //// Getters

    @NonNull
    public Listener getListener() {
        return (Listener)this.listener;
    }

    //// Interfaces

    public interface Listener extends DeleteMenuListener.Listener<Course> {
        void onCourseDeleteClicked(Course data); // expect row deletion from com.example.alexeyglushkov.wordteacher.ui

        void onLearnNewWordsClick(Course course);
        void onShowCourseContentClicked(Course course);
    }
}
