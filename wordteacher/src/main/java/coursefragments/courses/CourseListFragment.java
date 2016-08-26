package coursefragments.courses;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import listfragment.BaseListAdaptor;
import listfragment.BaseListFragment;
import main.MainApplication;
import main.Preferences;
import model.Course;
import model.CourseHolder;
import tools.LongTools;
import tools.Sortable;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseListFragment extends BaseListFragment<Course> implements Sortable {
    private Preferences.SortOrder sortOrder = Preferences.SortOrder.BY_CREATE_DATE_INV;

    //// Creation, initialization, restoration

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        getMainApplication().addCourseHolderListener(new MainApplication.ReadyListener() {
            @Override
            public void onReady() {
                onHolderLoaded(savedInstanceState);
            }
        });
        reload();
    }

    //// Events

    private void onHolderLoaded(Bundle savedInstanceState) {
        factory = createFactory();
        restoreProviderIfNeeded(savedInstanceState);
        reload();
    }

    //// Actions

    protected List<Course> getSortedItems(List<Course> courses) {
        List<Course> copy = new ArrayList<>(courses);
        Collections.sort(copy, new Comparator<Course>() {
            @Override
            public int compare(Course lhs, Course rhs) {
                return compareCourses(lhs, rhs);
            }
        });

        return copy;
    }

    private int compareCourses(Course lhs, Course rhs) {
        switch (sortOrder) {
            case BY_NAME: return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
            case BY_NAME_INV: return rhs.getTitle().compareToIgnoreCase(lhs.getTitle());
            case BY_CREATE_DATE: return lhs.getCreateDate().compareTo(rhs.getCreateDate());
            case BY_CREATE_DATE_INV: return rhs.getCreateDate().compareTo(lhs.getCreateDate());
        }

        return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
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

    @NonNull
    private CourseListFactory createFactory() {
        return new CourseListFactory(getCourseHolder());
    }

    //// Interfaces

    // Sortable

    @Override
    public Preferences.SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Preferences.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        reload();
    }

    //// Setters

    // Data Setters

    public void setCourses(List<Course> courses) {
        provider = factory.createFromList(courses);
    }

    //// Getters

    // App Getters

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    // Cast Getters

    private CourseListAdapter getCourseAdapter() {
        return (CourseListAdapter)adapter;
    }
}
