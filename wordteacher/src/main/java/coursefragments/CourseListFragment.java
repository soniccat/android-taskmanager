package coursefragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.wordteacher.R;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import listfragment.BaseListAdaptor;
import listfragment.BaseListFragment;
import main.MainApplication;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseListFragment extends BaseListFragment<Course> {
    //TODO: add type to get all courses from the course holder
    public static final String STORE_COURSE_IDS = "STORE_COURSE_IDS";

    private CourseListProvider provider;

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(STORE_COURSE_IDS, getCourseIds(provider.getCourses()));
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            getMainApplication().addCourseHolderListener(new MainApplication.ReadyListener() {
                @Override
                public void onReady() {
                    onHolderLoaded(savedInstanceState);
                }
            });
        }
    }

    private void onHolderLoaded(Bundle savedInstanceState) {
        List<String> courseIds = savedInstanceState.getStringArrayList(STORE_COURSE_IDS);
        Assert.assertNotNull("now only STORE_COURSE_IDS is supported", courseIds);

        provider = createCourseProviderFromIds(courseIds);
        reload();
    }

    private CourseListProvider createCourseProviderFromIds(final List<String> courseIds) {
        return createCourseProvider(getCourses(courseIds));
    }

    private CourseListProvider createCourseProvider(final List<Course> courses) {
        return new CourseListProvider() {
            @Override
            public List<Course> getCourses() {
                return courses;
            }
        };
    }

    private List<Course> getCourses(List<String> courseIds) {
        final List<Course> courses = new ArrayList<>();
        for (String udid : courseIds) {
            Course course = getCourseHolder().getCourse(UUID.fromString(udid));
            if (course != null) {
                courses.add(course);
            }
        }

        return courses;
    }

    private ArrayList<String> getCourseIds(List<Course> courses) {
        final ArrayList<String> courseIds = new ArrayList<>();
        for (Course course : courses) {
            courseIds.add(course.getId().toString());
        }

        return courseIds;
    }

    public void reload() {
        setAdapterCourses(provider.getCourses());
    }

    public boolean hasCourses() {
        List<Course> courses = provider != null ? provider.getCourses() : null;
        int count = courses != null ? courses.size() : 0;
        return count > 0;
    }

    public void setCourses(List<Course> courses) {
        provider = createCourseProvider(courses);
        setAdapterCourses(provider.getCourses());
    }

    private void setAdapterCourses(List<Course> inCourses) {
        List<Course> courses = new ArrayList<>();
        courses.addAll(inCourses);

        sortCourses(courses);
        getCourseAdapter().setCourses(courses);
    }

    private List<Course> sortCourses(List<Course> courses) {
        Collections.sort(courses, new Comparator<Course>() {
            @Override
            public int compare(Course lhs, Course rhs) {
                return rhs.getCreateDate().compareTo(lhs.getCreateDate());
            }
        });

        return courses;
    }

    private CourseListAdapter getCourseAdapter() {
        return (CourseListAdapter)adapter;
    }

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
}
