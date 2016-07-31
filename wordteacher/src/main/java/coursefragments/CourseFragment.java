package coursefragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import listfragment.BaseListAdaptor;
import listfragment.BaseListFragment;
import model.Course;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseFragment extends BaseListFragment<Course> {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    /*
    public void deleteCardView(Card card) {
        CardAdapter adapter = getCardAdapter();
        int index = adapter.getCardIndex(card);
        if (index != -1) {
            View view = getCourseView(index);
            if (view != null) {
                int position = recyclerView.getChildLayoutPosition(view);
                adapter.deleteCardAtIndex(index);
                adapter.notifyItemRemoved(position);
            }
        }
    }*/

    /*
    public void deleteCourseView(Course course) {
        CourseAdapter adapter = getCourseAdapter();
        int index = adapter.getCourseIndex(course);
        if (index != -1) {
            View view = getCourseView(index);
            if (view != null) {
                int position = recyclerView.getChildLayoutPosition(view);
                adapter.deleteCourseAtIndex(index);
                adapter.notifyItemRemoved(position);
            }
        }
    }*/

    /*
    protected void restoreAdapter(@Nullable Bundle savedInstanceState) {
        Parcelable parcelable = savedInstanceState.getParcelable("adapter");
        getCourseAdapter().onRestoreInstanceState(parcelable);
    }
    */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saveAdapterState(outState);
    }

    /*
    private void saveAdapterState(Bundle outState) {
        Parcelable parcelable = getCourseAdapter().onSaveInstanceState();
        outState.putParcelable("adapter", parcelable);
    }*/

    public void setCourses(ArrayList<Course> courses) {
        getCourseAdapter().setCourses(sortCourses(courses));
    }

    public boolean hasCourses() {
        List<Course> courses = getCourses();
        int count = courses != null ? courses.size() : 0;
        return count > 0;
    }

    public List<Course> getCourses() {
        return getCourseAdapter().getCourses();
    }

    private ArrayList<Course> sortCourses(ArrayList<Course> courses) {
        Collections.sort(courses, new Comparator<Course>() {
            @Override
            public int compare(Course lhs, Course rhs) {
                return rhs.getCreateDate().compareTo(lhs.getCreateDate());
            }
        });

        return courses;
    }

    private CourseAdapter getCourseAdapter() {
        return (CourseAdapter)adapter;
    }

    @Override
    protected BaseListAdaptor createAdapter() {
        return createCourseAdapter();
    }

    private CourseAdapter createCourseAdapter() {
        CourseAdapter adapter = new CourseAdapter(new CourseAdapter.Listener() {
            @Override
            public void onCourseClicked(View view, Course course) {
                CourseFragment.this.getListener().onRowClicked(course);
            }

            @Override
            public void onCourseMenuClicked(View view, Course course) {
                CourseFragment.this.getListener().onRowMenuClicked(course, view);
            }

            @Override
            public void onCourseViewDeleted(View view, Course course) {
                CourseFragment.this.getListener().onRowViewDeleted(course);
            }
        });

        return adapter;
    }

    /*
    private void onCourseClicked(View v, Course course) {
        listener.onCourseClicked(course);
    }

    private void onCourseMenuClicked(View v, Course course) {
        listener.onCourseMenuClicked(course, v);
    }

    private void onCourseViewDeleted(Course course) {
        listener.onCourseViewDeleted(course);
    }

    private void onCardClicked(View v, Card card) {
        listener.onCardClicked(card);
    }

    private void onCardMenuClicked(View v, Card card) {
        listener.onCardMenuClicked(card, v);
    }

    private void onCardViewDeleted(Card card) {
        listener.onCardViewDeleted(card);
    }*/
}
