package com.example.alexeyglushkov.wordteacher;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 13.06.16.
 */
public class CourseFragmentMenuListener extends DeleteMenuListener<Course> {
    private CourseHolder courseHolder;
    //private boolean snackBarNeedDeleteCard;

    public CourseFragmentMenuListener(Context context, CourseHolder courseHolder, Listener listener) {
        super(context, listener);
        this.courseHolder = courseHolder;
    }

    /*
    private void deleteCardWithConfirmation(final Card card) {
        snackBarNeedDeleteCard = true;

        String undoString = getContext().getString(R.string.snackbar_undo_deletion);
        currentSnackbar = Snackbar.make(listener.getSnackBarViewContainer(), undoString, Snackbar.LENGTH_LONG);
        currentSnackbar.setAction(android.R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBarNeedDeleteCard = false;
                listener.onCardDeletionCancelled(card);
            }
        });
        currentSnackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (snackBarNeedDeleteCard) {
                    Error error = getCourseHolder().removeCard(card);
                    if (error != null) {
                        listener.onCardDeletionCancelled(card);
                    } else {
                        listener.onCardDeleted(card);
                    }
                }

                currentSnackbar = null;
            }
        });
        currentSnackbar.show();
    }
*/

    // CourseFragment.Listener

    public void onCourseMenuClicked(final Course course, View v) {
        PopupMenu popupMenu = new PopupMenu(context, v);
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

        popupMenu.show();
    }

    public void onCourseViewDeleted(final Course course) {
        deleteDataWithSnackbar(course);
    }

    public void onCourseClicked(Course course) {
        listener.onDataClicked(course);
    }

    /*
    @Override
    public void onCardClicked(Card card) {
    }

    @Override
    public void onCardMenuClicked(final Card card, View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenu().add(Menu.NONE, R.id.delete_card, 0, R.string.menu_card_delete);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.delete_card) {
                    listener.onCardDeleteClicked(card);
                    onCardViewDeleted(card);
                }

                return false;
            }
        });

        popupMenu.show();
    }

    public void onCardViewDeleted(Card card) {
        deleteCardWithConfirmation(card);
    }*/

    @Override
    public void onRowClicked(Course data) {
        onCourseClicked(data);
    }

    @Override
    public void onRowMenuClicked(Course data, View view) {
        onCourseMenuClicked(data, view);
    }

    @Override
    public void onRowViewDeleted(Course data) {
        onCourseViewDeleted(data);
    }

    @Override
    protected Error deleteData(Course data) {
        return courseHolder.removeCourse(data);
    }

    public Listener getListener() {
        return (Listener)this.listener;
    }

    public interface Listener extends DeleteMenuListener.Listener<Course> {
        void onCourseDeleteClicked(Course data); // expect row deletion from ui

        void onLearnNewWordsClick(Course course);
        void onShowCourseContentClicked(Course course);
    }

    /*
    public interface Listener {
        void onCourseClicked(Course course);
        void onLearnNewWordsClick(Course course);
        void onShowCourseContentClicked(Course course);

        // expect ui updating
        void onCourseDeleteClicked(Course course);
        //void onCardDeleteClicked(Card card);
        void onCourseDeletionCancelled(Course course);
        //void onCardDeletionCancelled(Card card);

        void onCourseDeleted(Course course);
        //void onCardDeleted(Card card);

        View getSnackBarViewContainer();
    }
    */
}
