package com.example.alexeyglushkov.wordteacher.courselistmodules.courselistmodule.presenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import com.aglushkov.repository.livedata.Resource;
import com.aglushkov.repository.livedata.ResourceLiveDataProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategyFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.NullStorableListProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.ResourceListLiveDataProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.ResourceListLiveDataProviderImp;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProviderFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableResourceListLiveDataProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.presenter.SimpleListPresenter;
import com.example.alexeyglushkov.wordteacher.listmodule.view.ListViewInterface;
import com.example.alexeyglushkov.wordteacher.main.MainApplication;
import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.wordteacher.model.Course;
import com.example.alexeyglushkov.wordteacher.model.CourseHolder;
import com.example.alexeyglushkov.wordteacher.tools.SortOrderCompareStrategy;
import com.example.alexeyglushkov.wordteacher.tools.Sortable;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public class CourseListPresenter extends SimpleListPresenter<Course> implements Sortable {
    public static String DEFAULT_TITLE = "Courses";

    private static final int MSG_REFRESH = 0;
    private static final int REFRESH_INTERVAL = 60 * 1000;

    private @NonNull Handler refreshHandler;

    //// Creation, initialization, restoration

    @Override
    public void initialize() {
        super.initialize();
        refreshHandler = createRefreshHandler();
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
        invalidateRefreshSchedule();
    }

    //// Actions

    private void scheduleRefresh() {
        refreshHandler.sendEmptyMessageDelayed(MSG_REFRESH, REFRESH_INTERVAL);
    }

    private void invalidateRefreshSchedule() {
        refreshHandler.removeMessages(MSG_REFRESH);
    }

    private void refresh() {
        //view.updateRows(); ??? // move to quizletsetlistpresenter
        scheduleRefresh();
    }

    @Override
    protected StorableResourceListLiveDataProvider<Course> createLiveDataProvider(Bundle bundle) {
        return new ResourceListLiveDataProviderImp<>(bundle, new ResourceLiveDataProvider<List<Course>>() {
            @Override
            public LiveData<Resource<List<Course>>> getLiveData() {
                return getCourseHolder().getCoursesLiveData();
            }
        });
    }

    //// Creation Methods

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

//    @Override
//    public Preferences.SortOrder getSortOrder() {
//        return getCompareStrategy().getSortOrder();
//    }

//    public void setSortOrder(Preferences.SortOrder sortOrder) {
//        setCompareStrategy(getCompareStrategyFactory().createStrategy(sortOrder));
//        reload();
//    }

    // PagerModuleItemWithTitle

    @Override
    public String getTitle() {
        return DEFAULT_TITLE;
    }

    //// Getters

    // App Getters

    private MainApplication getMainApplication() {
        return MainApplication.Companion.getInstance();
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }
}
