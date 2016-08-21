package coursefragments.courses;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexeyglushkov.wordteacher.R;

import listfragment.DeleteMenuListener;
import listfragment.ListMenuListener;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 13.06.16.
 */
public class CourseListFragmentMenuListener extends DeleteMenuListener<Course> {
    private CourseHolder courseHolder;

    public CourseListFragmentMenuListener(Context context, CourseHolder courseHolder, Listener listener) {
        super(context, listener);
        this.courseHolder = courseHolder;
    }

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

    public void onCourseViewDeleted(final Course course) {
        deleteDataWithSnackbar(course);
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
}
