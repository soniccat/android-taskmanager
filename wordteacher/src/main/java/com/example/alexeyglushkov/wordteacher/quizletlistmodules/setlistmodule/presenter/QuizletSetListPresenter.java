package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter;

import androidx.annotation.NonNull;

import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategy;
import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategyFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableResourceListLiveDataProviderFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.presenter.SimpleListPresenter;
import com.example.alexeyglushkov.wordteacher.main.MainApplication;
import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.wordteacher.tools.SortOrderCompareStrategy;
import com.example.alexeyglushkov.wordteacher.tools.Sortable;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public class QuizletSetListPresenter extends SimpleListPresenter<QuizletSet>
        implements Sortable {

    public static String DEFAULT_TITLE = "Sets";

    //// Interface methods

    // PagerModuleItemWithTitle

    @Override
    public String getTitle() {
        return DEFAULT_TITLE;
    }

    //// Creation Method

    @NonNull
    protected QuizletSetListProviderFactory createProviderFactory() {
        return null;
    }

    @Override
    protected StorableResourceListLiveDataProviderFactory<QuizletSet> createLiveDataProviderFactory() {
        return new QuizletSetListLiveDataProviderFactory(getQuizletRepository());
    }

    protected SortOrderCompareStrategy<QuizletSet> createSortStrategy(Preferences.SortOrder sortOrder) {
        return new QuizletSetCompareStrategy(sortOrder);
    }

    //// Getters

    // App Getters

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public QuizletRepository getQuizletRepository() {
        return getMainApplication().getQuizletRepository();
    }
}
