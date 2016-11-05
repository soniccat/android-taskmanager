package coursefragments.courses;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.wordteacher.R;

import java.util.List;

import listfragment.listmodule.view.BaseListAdaptor;
import listfragment.listmodule.view.BaseListFragment;
import listfragment.CompareStrategyFactory;
import listfragment.NullStorableListProvider;
import main.MainApplication;
import main.Preferences;
import model.Course;
import model.CourseHolder;
import tools.SortOrderCompareStrategy;
import tools.Sortable;

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

    //// Actions
    
    // Refreshing

    public void reloadCells() {
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
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
                CourseListFragment.this.getListener().onRowClicked(course);
            }

            @Override
            public void onCourseMenuClicked(View view, Course course) {
                CourseListFragment.this.getListener().onRowMenuClicked(course, view);
            }

            @Override
            public void onCourseViewDeleted(View view, Course course) {
                CourseListFragment.this.getListener().onRowViewDeleted(course);
            }
        });

        return adapter;
    }

    //// Getters

    private CourseListAdapter getCourseAdapter() {
        return (CourseListAdapter)adapter;
    }
}
