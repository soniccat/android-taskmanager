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

import listfragment.BaseListAdaptor;
import listfragment.BaseListFragment;
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
public class CourseListFragment extends BaseListFragment<Course> implements Sortable, CourseHolder.CourseHolderListener {
    static final int MSG_REFRESH = 0;
    static final int REFRESH_INTERVAL = 60 * 1000;

    private @NonNull Handler refreshHandler;

    //// Creation, initialization, restoration
    private @Nullable Bundle savedInstanceState;

    @Override
    protected void initialize() {
        super.initialize();
        refreshHandler = new Handler(Looper.myLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == MSG_REFRESH) {
                    refresh();
                    return true;
                }

                return false;
            }
        });
    }

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

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        getCourseHolder().addListener(this);
        if (getCourseHolder().getState() != CourseHolder.State.Unitialized) {
            handleLoadedCourses();
            reload();
        }
    }

    private void restoreIfNeeded() {
        if (this.savedInstanceState != null || provider instanceof NullStorableListProvider) {
            provider = providerFactory.restore(this.savedInstanceState);
            this.savedInstanceState = null;
        }
    }

    //// Events

    @Override
    public void onResume() {
        super.onResume();
        scheduleRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        invalidateRefreshSchedule();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getCourseHolder().removeListener(this);
        invalidateRefreshSchedule();
        refreshHandler = null;
    }

    private void onHolderLoaded() {
        handleLoadedCourses();
    }

    private void handleLoadedCourses() {
        restoreIfNeeded();
        reload();
    }

    //// Actions
    
    // Refreshing

    public void scheduleRefresh() {
        refreshHandler.sendEmptyMessageDelayed(MSG_REFRESH, REFRESH_INTERVAL);
    }

    public void invalidateRefreshSchedule() {
        refreshHandler.removeMessages(MSG_REFRESH);
    }

    public void refresh() {
        reloadCells();
        scheduleRefresh();
    }

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

    @NonNull
    protected CourseListFactory createProviderFactory() {
        return new CourseListFactory(getCourseHolder());
    }

    @Override
    public CompareStrategyFactory<Course> createCompareStrategyFactory() {
        return new CourseCompareStrategyFactory();
    }

    //// Interfaces

    // Sortable

    @Override
    public Preferences.SortOrder getSortOrder() {
        return getCompareStrategy().getSortOrder();
    }

    public void setSortOrder(Preferences.SortOrder sortOrder) {
        setCompareStrategy(getCompareStrategyFactory().createStrategy(sortOrder));
        reload();
    }

    // CourseHolder.CourseHolderListener

    @Override
    public void onLoaded(@NonNull CourseHolder holder) {
        onHolderLoaded();
    }

    @Override
    public void onCoursesAdded(@NonNull CourseHolder holder, @NonNull List<Course> courses) {
        reload();
    }

    @Override
    public void onCoursesRemoved(@NonNull CourseHolder holder, @NonNull List<Course> courses) {
        reload();
    }

    @Override
    public void onCourseUpdated(@NonNull CourseHolder holder, @NonNull Course course, @NonNull CourseHolder.UpdateBatch batch) {
        int index = getCourseAdapter().getItems().indexOf(course);
        if (index != -1) {
            getCourseAdapter().notifyItemRangeChanged(index, 1);
        }
    }

    //// Setters

    // Data Setters

    public void setCourses(List<Course> courses) {
        provider = providerFactory.createFromList(courses);
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

    private CourseCompareStrategyFactory getCompareStrategyFactory() {
        return (CourseCompareStrategyFactory)compareStrategyFactory;
    }

    private SortOrderCompareStrategy getCompareStrategy() {
        return (SortOrderCompareStrategy)compareStrategy;
    }
}
