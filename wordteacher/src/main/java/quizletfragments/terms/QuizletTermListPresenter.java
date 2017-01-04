package quizletfragments.terms;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.List;

import listmodule.CompareStrategyFactory;
import listmodule.NullStorableListProvider;
import listmodule.presenter.BaseListPresenter;
import listmodule.view.ListViewInterface;
import main.MainApplication;
import main.Preferences;
import pagermodule.PagerModuleItemWithTitle;
import tools.SortOrderCompareStrategy;
import tools.Sortable;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public class QuizletTermListPresenter extends BaseListPresenter<QuizletTerm>
        implements Sortable,
        PagerModuleItemWithTitle, 
        QuizletService.QuizletServiceListener {
    private Bundle savedInstanceState;

    //// Events

    @Override
    public void onViewStateRestored(ListViewInterface view, @Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(view, savedInstanceState);

        this.savedInstanceState = savedInstanceState;

        getQuizletService().addListener(this);
        if (getQuizletService().getState() != QuizletService.State.Unitialized) {
            handleLoadedSets();
            view.reload(getItems());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQuizletService().removeListener(this);
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
    protected QuizletTermListFactory createProviderFactory() {
        return new QuizletTermListFactory(getQuizletService());
    }

    @Override
    public CompareStrategyFactory<QuizletTerm> createCompareStrategyFactory() {
        return new QuizletTermCompareStrategyFactory();
    }

    //// Interface

    // QuizletService.QuizletServiceListener

    @Override
    public void onStateChanged(QuizletService service, QuizletService.State oldState) {
        if (service.getState() == QuizletService.State.Loading) {
            view.showLoading();
        } else {
            handleLoadedSets();
        }
    }

    @Override
    public void onLoadError(QuizletService service, Error error) {
        view.hideLoading();
    }

    // PagerModuleItemWithTitle

    @Override
    public String getTitle() {
        QuizletSet set = getParentSet();
        return set != null ? set.getTitle() : "Cards";
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

    public QuizletSet getParentSet() {
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

    public QuizletService getQuizletService() {
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
