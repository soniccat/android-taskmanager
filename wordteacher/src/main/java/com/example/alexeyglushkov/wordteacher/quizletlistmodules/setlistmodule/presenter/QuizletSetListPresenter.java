package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter;

import androidx.lifecycle.Observer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.Resource;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategyFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.NullStorableListProvider;
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

public class QuizletSetListPresenter extends SimpleListPresenter<QuizletSet> implements
        Sortable,
        Observer<Resource<List<QuizletSet>>> {

    public static String DEFAULT_TITLE = "Sets";

    private Bundle savedInstanceState;

    @Override
    public void onViewStateRestored(ListViewInterface view, @Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(view, savedInstanceState);

        this.savedInstanceState = savedInstanceState;
        getQuizletService().getLiveSets().observeForever(this);

        if (savedInstanceState != null) {
            onServiceStateChanged(getQuizletService().getLiveSets().getValue());
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
    public void onDestroyView() {
        super.onDestroyView();
        getQuizletService().getLiveSets().removeObserver(this);
    }

    //// Actions

    private void handleLoadedSets() {
        view.hideLoading();
        restoreIfNeeded();
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
        if (listResource.error != null) {
            view.hideLoading();
        } else {
            onServiceStateChanged(listResource);
        }
    }

    private void onServiceStateChanged(@NonNull Resource<List<QuizletSet>> listResource) {
        boolean hasData = listResource.data != null && listResource.data.size() > 0;
        boolean isLoading = listResource.state == Resource.State.Loading;

        if (!hasData && isLoading) {
            view.showLoading();

        } else if (hasData && !isLoading) {
            handleLoadedSets();
        }
    }

    @NonNull
    protected QuizletSetListProviderFactory createProviderFactory() {
        return new QuizletSetListProviderFactory(getQuizletService());
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

    public QuizletService getQuizletService() {
        return getMainApplication().getQuizletService();
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
