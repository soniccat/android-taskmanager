package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter;

import androidx.lifecycle.Observer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.quizletservice.Resource;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategyFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.EmptyStorableListLiveDataProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.NullStorableListProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableListLiveDataProviderFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.presenter.SimpleListPresenter;
import com.example.alexeyglushkov.wordteacher.listmodule.view.ListViewInterface;
import com.example.alexeyglushkov.wordteacher.main.MainApplication;
import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.wordteacher.tools.SortOrderCompareStrategy;
import com.example.alexeyglushkov.wordteacher.tools.Sortable;

import java.util.List;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public class QuizletSetListPresenter extends SimpleListPresenter<QuizletSet>
        implements Observer<Resource<List<QuizletSet>>>, Sortable {

    public static String DEFAULT_TITLE = "Sets";

    private Bundle savedInstanceState;

    @Override
    public void onViewStateRestored(ListViewInterface view, @Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(view, savedInstanceState);

        this.savedInstanceState = savedInstanceState;
        getQuizletRepository().getLiveData().observeForever((Observer<Resource<List<QuizletSet>>>)this);

        if (savedInstanceState != null) {
            onServiceStateChanged(getQuizletRepository().getLiveData().getValue());
        }
    }

    //// Events

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQuizletRepository().getLiveData().removeObserver(this);
    }

    //// Actions

    private void handleLoadedSets() {
        view.hideLoading();
        reload();
    }

    //// Interface methods

    // PagerModuleItemWithTitle

    @Override
    public String getTitle() {
        return DEFAULT_TITLE;
    }

    // Observer<Resource<List<QuizletSet>>>

    @Override
    public void onChanged(@NonNull Resource<List<QuizletSet>> listResource) {
        onServiceStateChanged(listResource);
    }

    private void onServiceStateChanged(@NonNull Resource<List<QuizletSet>> listResource) {
        boolean hasData = listResource.data != null && listResource.data.size() > 0;
        boolean isLoading = listResource.state == Resource.State.Loading;

        if (listResource.error != null) {
            view.hideLoading();
        } else if (isLoading) {
            view.showLoading();
        } else if (hasData) {
            handleLoadedSets();
        }
    }

    @NonNull
    protected QuizletSetListProviderFactory createProviderFactory() {
        return new QuizletSetListProviderFactory(getQuizletRepository());
    }

    @Override
    protected StorableListLiveDataProviderFactory<QuizletSet> createLiveDataProviderFactory() {
        return new QuizletSetListLiveDataProviderFactory(getQuizletRepository());
    }

    public CompareStrategyFactory<QuizletSet> createCompareStrategyFactory() {
        return new QuizletSetCompareStrategyFactory();
    }

    //// Setters

    public void setSortOrder(Preferences.SortOrder sortOrder) {
        setCompareStrategy(getCompareStrategyFactory().createStrategy(sortOrder));
        reload();
    }

    //// Getters

    // App Getters

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public QuizletRepository getQuizletRepository() {
        return getMainApplication().getQuizletRepository();
    }

    // Data Getters

    public Preferences.SortOrder getSortOrder() {
        return getCompareStrategy().getSortOrder();
    }

    // Cast Getters

    private QuizletSetCompareStrategyFactory getCompareStrategyFactory() {
        return (QuizletSetCompareStrategyFactory)compareStrategyFactory;
    }

    private SortOrderCompareStrategy getCompareStrategy() {
        return (SortOrderCompareStrategy)compareStrategy;
    }
}
