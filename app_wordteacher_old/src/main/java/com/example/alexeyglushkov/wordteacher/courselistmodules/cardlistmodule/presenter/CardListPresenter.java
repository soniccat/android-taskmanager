package com.example.alexeyglushkov.wordteacher.courselistmodules.cardlistmodule.presenter;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;


import java.util.List;

import com.aglushkov.repository.livedata.Resource;
import com.aglushkov.repository.livedata.ResourceLiveDataProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategyFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.NullStorableListProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.ResourceListLiveDataProviderImp;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableResourceListLiveDataProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.presenter.SimpleListPresenter;
import com.example.alexeyglushkov.wordteacher.listmodule.view.ListViewInterface;
import com.example.alexeyglushkov.wordteacher.main.MainApplication;
import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.wordteacher.model.Card;
import com.example.alexeyglushkov.wordteacher.model.Course;
import com.example.alexeyglushkov.wordteacher.model.CourseHolder;
import com.example.alexeyglushkov.wordteacher.tools.SortOrderCompareStrategy;
import com.example.alexeyglushkov.wordteacher.tools.Sortable;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public class CardListPresenter extends SimpleListPresenter<Card> implements Sortable {

    public static String DEFAULT_TITLE = "Cards";

    @Override
    protected StorableResourceListLiveDataProvider<Card> createLiveDataProvider(Bundle bundle) {
        return new ResourceListLiveDataProviderImp<>(bundle, new ResourceLiveDataProvider<List<Card>>() {
            @Override
            public LiveData<Resource<List<Card>>> getLiveData() {
                return null;
            }
        }) ;
    }

    //// Interface

    // BaseListPresenter

//    @NonNull
//    protected CardListProviderFactory createProviderFactory() {
//        return new CardListProviderFactory(getCourseHolder());
//    }

    // PagerModuleItemWithTitle

    @Override
    public String getTitle() {
        Course set = getParentCourse();
        return set != null ? set.getTitle() : DEFAULT_TITLE;
    }


    // Sortable

//    @Override
//    public void setSortOrder(Preferences.SortOrder sortOrder) {
//        setCompareStrategy(getCompareStrategyFactory().createStrategy(sortOrder));
//        reload();
//    }

//    @Override
//    public Preferences.SortOrder getSortOrder() {
//        return getCompareStrategy().getSortOrder();
//    }


    //// Setters

    // Data Setters

    public void setParentCourse(Course course) {
        // TODO:
    }

    //// Getters

    // App Getters

    private MainApplication getMainApplication() {
        return MainApplication.Companion.getInstance();
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    // Data Getters

    public @Nullable
    Course getParentCourse() {
        Course result = null;
//        if (provider instanceof CourseCardListProvider) {
//            CourseCardListProvider courseProvider = (CourseCardListProvider)provider;
//            result = courseProvider.getCourse();
//        }

        return result;
    }

    // State Getters

    // Cast Getters

//    private CardCompareStrategyFactory getCompareStrategyFactory() {
//        return (CardCompareStrategyFactory)compareStrategyFactory;
//    }

    private SortOrderCompareStrategy getCompareStrategy() {
        return (SortOrderCompareStrategy)compareStrategy;
    }
}
