package com.example.alexeyglushkov.wordteacher;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 13.06.16.
 */
public class CourseFragmentMenuListener implements CourseFragment.Listener {
    private Context context;
    private CourseHolder courseHolder;
    private Listener listener;

    private boolean snackBarNeedDeleteCourse;
    private boolean snackBarNeedDeleteCard;

    public CourseFragmentMenuListener(Context context, CourseHolder courseHolder, Listener listener) {
        this.context = context;
        this.courseHolder = courseHolder;
        this.listener = listener;
    }

    private Context getContext() {
        return context;
    }

    private CourseHolder getCourseHolder() {
        return courseHolder;
    }

    private void deleteCourseWithSnackbar(final Course course) {
        snackBarNeedDeleteCourse = true;

        String undoString = getContext().getString(R.string.snackbar_undo_deletion);
        Snackbar snackbar = Snackbar.make(listener.getSnackBarViewContainer(), undoString, Snackbar.LENGTH_LONG);
        snackbar.setAction(android.R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBarNeedDeleteCourse = false;
                listener.onCourseDeletionCancelled(course);
            }
        });
        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (snackBarNeedDeleteCourse) {
                    Error error = getCourseHolder().removeCourse(course);
                    if (error != null) {
                        listener.onCourseDeletionCancelled(course);
                    } else {
                        listener.onCourseDeleted(course);
                    }
                }
            }
        });
        snackbar.show();
    }

    private void deleteCardWithConfirmation(final Card card) {
        snackBarNeedDeleteCard = true;

        String undoString = getContext().getString(R.string.snackbar_undo_deletion);
        Snackbar snackbar = Snackbar.make(listener.getSnackBarViewContainer(), undoString, Snackbar.LENGTH_LONG);
        snackbar.setAction(android.R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBarNeedDeleteCard = false;
                listener.onCardDeletionCancelled(card);
            }
        });
        snackbar.setCallback(new Snackbar.Callback() {
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
            }
        });
        snackbar.show();
    }

    // CourseFragment.Listener

    @Override
    public void onCourseMenuClicked(final Course course, View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        if (course.getReadyToLearnCards().size() > 0) {
            popupMenu.getMenu().add(Menu.NONE, R.id.learn_ready_words, 0, R.string.menu_course_learn_only_ready_words);
        }
        if (course.getNotStartedCards().size() > 0) {
            popupMenu.getMenu().add(Menu.NONE, R.id.learn_new_words, 0, R.string.menu_course_learn_only_new_words);
        }
        popupMenu.getMenu().add(Menu.NONE, R.id.edit_course, 0, R.string.menu_course_show_cards);
        popupMenu.getMenu().add(Menu.NONE, R.id.delete_course, 0, R.string.menu_course_delete);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.learn_ready_words) {
                    listener.onLearnReadyWordsClick(course);

                } else if (item.getItemId() == R.id.learn_new_words) {
                    listener.onLearnNewWordsClick(course);

                } else if (item.getItemId() == R.id.edit_course) {
                    listener.onShowCourseContentClicked(course);

                } else if (item.getItemId() == R.id.delete_course) {
                    //getCourseFragment().deleteCourse(course);
                    listener.onCourseDeleteClicked(course);
                    onCourseDeleted(course);
                }

                return false;
            }
        });

        popupMenu.show();
    }

    @Override
    public boolean onCourseDeleted(final Course course) {
        deleteCourseWithSnackbar(course);
        return true;
    }

    @Override
    public void onCourseClicked(Course course) {
        listener.onCourseClicked(course);
    }

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
                    //getCourseFragment().deleteCard(card);
                    listener.onCardDeleteClicked(card);
                    onCardDeleted(card);
                }

                return false;
            }
        });

        popupMenu.show();
    }

    public boolean onCardDeleted(Card card) {
        deleteCardWithConfirmation(card);
        return true;
    }

    public interface Listener {
        void onCourseDeleteClicked(Course course);
        void onCardDeleteClicked(Card card);
        void onShowCourseContentClicked(Course course);

        void onCourseClicked(Course course);
        void onLearnReadyWordsClick(Course course);
        void onLearnNewWordsClick(Course course);

        void onCourseDeletionCancelled(Course course);
        void onCourseDeleted(Course course);
        void onCardDeletionCancelled(Card card);
        void onCardDeleted(Card card);

        View getSnackBarViewContainer();
    }
}
