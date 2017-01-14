package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategyFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.NullStorableListProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.presenter.SimpleListPresenter;
import com.example.alexeyglushkov.wordteacher.listmodule.view.ListViewInterface;
import com.example.alexeyglushkov.wordteacher.main.MainApplication;
import com.example.alexeyglushkov.wordteacher.main.Preferences;
import tools.SortOrderCompareStrategy;
import tools.Sortable;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public class QuizletSetListPresenter extends SimpleListPresenter<QuizletSet> implements
        Sortable,
        QuizletService.QuizletServiceListener {

    public static String DEFAULT_TITLE = "Sets";

    private Bundle savedInstanceState;

    @Override
    public void onViewStateRestored(ListViewInterface view, @Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(view, savedInstanceState);
        getQuizletService().addListener(this);

        this.savedInstanceState = savedInstanceState;
        if (getQuizletService().getState() != QuizletService.State.Unitialized) {
            handleLoadedSets();
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
        getQuizletService().removeListener(this);
    }

    private void onQuizletServiceLoaded() {
        handleLoadedSets();
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

    // QuizletService.QuizletServiceListener

    @Override
    public void onStateChanged(QuizletService service, QuizletService.State oldState) {
        if (service.getState() == QuizletService.State.Loading) {
            view.showLoading();
        } else {
            onQuizletServiceLoaded();
        }
    }

    @Override
    public void onLoadError(QuizletService service, Error error) {
        view.hideLoading();
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
