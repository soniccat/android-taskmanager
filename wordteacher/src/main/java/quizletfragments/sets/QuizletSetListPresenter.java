package quizletfragments.sets;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import listmodule.CompareStrategyFactory;
import listmodule.NullStorableListProvider;
import listmodule.presenter.BaseListPresenter;
import main.MainApplication;
import main.Preferences;
import tools.SortOrderCompareStrategy;
import tools.Sortable;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public class QuizletSetListPresenter extends BaseListPresenter<QuizletSet> implements Sortable, QuizletService.QuizletServiceListener {

    private Bundle savedInstanceState;

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
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
    protected QuizletSetListFactory createProviderFactory() {
        return new QuizletSetListFactory(getQuizletService());
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
