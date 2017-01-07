package coursefragments.courses;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.wordteacher.R;

import listmodule.view.BaseListAdaptor;
import listmodule.view.BaseListFragment;
import model.Course;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseListFragment extends BaseListFragment<Course> {

    //// Creation, initialization, restoration

    public static @NonNull CourseListFragment create() {
        CourseListFragment fragment = new CourseListFragment();
        fragment.initialize();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    //// Creation Methods

    @Override
    protected BaseListAdaptor createAdapter() {
        return createCourseAdapter();
    }

    private CourseListAdapter createCourseAdapter() {
        CourseListAdapter adapter = new CourseListAdapter(new CourseListAdapter.Listener() {
            @Override
            public void onCourseClicked(View view, Course course) {
                CourseListFragment.this.getPresenter().onRowClicked(course);
            }

            @Override
            public void onCourseMenuClicked(View view, Course course) {
                CourseListFragment.this.getPresenter().onRowMenuClicked(course, view);
            }

            @Override
            public void onCourseViewDeleted(View view, Course course) {
                CourseListFragment.this.getPresenter().onRowViewDeleted(course);
            }
        });

        return adapter;
    }
}
