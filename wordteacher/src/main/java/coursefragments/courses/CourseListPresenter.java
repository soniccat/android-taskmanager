package coursefragments.courses;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import listmodule.CompareStrategyFactory;
import listmodule.NullStorableListProvider;
import listmodule.presenter.BaseListPresenter;
import listmodule.presenter.SimpleListPresenter;
import listmodule.view.ListViewInterface;
import main.MainApplication;
import main.Preferences;
import model.Course;
import model.CourseHolder;
import tools.SortOrderCompareStrategy;
import tools.Sortable;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public class CourseListPresenter extends SimpleListPresenter<Course> implements Sortable, CourseHolder.CourseHolderListener {
    public static String DEFAULT_TITLE = "Courses";

    static final int MSG_REFRESH = 0;
    static final int REFRESH_INTERVAL = 60 * 1000;

    private @NonNull
    Handler refreshHandler;

    private @Nullable
    Bundle savedInstanceState;

    //// Creation, initialization, restoration


    @Override
    public void initialize() {
        super.initialize();
        refreshHandler = createRefreshHandler();
    }

    @Override
    public void onViewStateRestored(ListViewInterface view, @Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(view, savedInstanceState);

        this.savedInstanceState = savedInstanceState;

        getCourseHolder().addListener(this);
        if (getCourseHolder().getState() != CourseHolder.State.Unitialized) {
            handleLoadedCourses();
            reload();
        } else {
            view.showLoading();
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
    }

    private void onHolderLoaded() {
        handleLoadedCourses();
    }

    //// Actions

    private void handleLoadedCourses() {
        view.hideLoading();
        restoreIfNeeded();
        reload();
    }

    public void scheduleRefresh() {
        refreshHandler.sendEmptyMessageDelayed(MSG_REFRESH, REFRESH_INTERVAL);
    }

    public void invalidateRefreshSchedule() {
        refreshHandler.removeMessages(MSG_REFRESH);
    }

    public void refresh() {
        view.updateRows();
        scheduleRefresh();
    }

    //// Creation Methods

    @NonNull
    protected CourseListFactory createProviderFactory() {
        return new CourseListFactory(getCourseHolder());
    }

    @Override
    public CompareStrategyFactory<Course> createCompareStrategyFactory() {
        return new CourseCompareStrategyFactory();
    }

    private Handler createRefreshHandler() {
        return new Handler(Looper.myLooper(), new Handler.Callback() {
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
        int index = getItems().indexOf(course);
        if (index != -1) {
            view.updateRow(index);
        }
    }

    // PagerModuleItemWithTitle

    @Override
    public String getTitle() {
        return DEFAULT_TITLE;
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

    private CourseCompareStrategyFactory getCompareStrategyFactory() {
        return (CourseCompareStrategyFactory)compareStrategyFactory;
    }

    private SortOrderCompareStrategy getCompareStrategy() {
        return (SortOrderCompareStrategy)compareStrategy;
    }
}
