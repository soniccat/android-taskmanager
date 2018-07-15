package com.example.alexeyglushkov.wordteacher.quizletlistmodules.termlistmodule.presenter;

import android.arch.lifecycle.Observer;
import android.database.Observable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.Resource;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.List;

import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategyFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.NullStorableListProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.presenter.SimpleListPresenter;
import com.example.alexeyglushkov.wordteacher.listmodule.view.ListViewInterface;
import com.example.alexeyglushkov.wordteacher.main.MainApplication;
import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.PagerModuleItemWithTitle;
import com.example.alexeyglushkov.wordteacher.tools.SortOrderCompareStrategy;
import com.example.alexeyglushkov.wordteacher.tools.Sortable;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public class QuizletTermListPresenter extends SimpleListPresenter<QuizletTerm>
        implements Sortable,
        PagerModuleItemWithTitle,
        Observer<Resource<List<QuizletSet>>> {
    public static String DEFAULT_TITLE = "Cards";

    private Bundle savedInstanceState;

    //// Events

    @Override
    public void onViewStateRestored(ListViewInterface view, @Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(view, savedInstanceState);

        this.savedInstanceState = savedInstanceState;
        getQuizletService().getLiveSets().observeForever(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQuizletService().getLiveSets().removeObserver(this);
    }

    //// Actions

    private void handleLoadedSets() {
        view.hideLoading();
        restoreIfNeeded();
        view.reload(getItems());
    }

    private void restoreIfNeeded() {
        if (this.savedInstanceState != null || provider instanceof NullStorableListProvider) {
            provider = providerFactory.restore(this.savedInstanceState);
            this.savedInstanceState = null;
        }
    }

    //// Overrides

    @NonNull
    protected QuizletTermListProviderFactory createProviderFactory() {
        return new QuizletTermListProviderFactory(getQuizletService());
    }

    @Override
    public CompareStrategyFactory<QuizletTerm> createCompareStrategyFactory() {
        return new QuizletTermCompareStrategyFactory();
    }

    //// Interface

    // Observer<Resource<List<QuizletSet>>>

    @Override
    public void onChanged(@NonNull Resource<List<QuizletSet>> listResource) {
        if (listResource.error != null) {
            view.hideLoading();

        } else {
            boolean hasData = listResource.data != null && listResource.data.size() > 0;
            boolean isLoading = listResource.state == Resource.State.Loading;

            if (!hasData && isLoading) {
                view.showLoading();

            } else if (hasData && !isLoading) {
                handleLoadedSets();
            }
        }
    }

    // PagerModuleItemWithTitle

    @Override
    public String getTitle() {
        QuizletSet set = getParentSet();
        return set != null ? set.getTitle() : DEFAULT_TITLE;
    }

    // Sortable

    @Override
    public void setSortOrder(Preferences.SortOrder sortOrder) {
        super.setSortOrder(sortOrder);

        setCompareStrategy(getCompareStrategyFactory().createStrategy(sortOrder));
        view.reload(getItems());
    }

    @Override
    public Preferences.SortOrder getSortOrder() {
        return getCompareStrategy().getSortOrder();
    }

    //// Setters

    // Set Data

    public void setTermSet(QuizletSet set) {
        provider = providerFactory.createFromObject(set);
    }

    public void setTerms(List<QuizletTerm> terms) {
        provider = providerFactory.createFromList(terms);
    }

    //// Getters

    private QuizletSet getParentSet() {
        QuizletSet parentSet = null;
        if (provider instanceof QuizletSetTermListProvider) {
            QuizletSetTermListProvider setProvider = (QuizletSetTermListProvider)provider;
            parentSet = setProvider.getSet();
        }

        return parentSet;
    }

    // Get App Data

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    private QuizletService getQuizletService() {
        return getMainApplication().getQuizletService();
    }

    // Cast Getters

    private QuizletTermCompareStrategyFactory getCompareStrategyFactory() {
        return (QuizletTermCompareStrategyFactory)compareStrategyFactory;
    }

    private SortOrderCompareStrategy getCompareStrategy() {
        return (SortOrderCompareStrategy)compareStrategy;
    }
}
