package quizletfragments.terms;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.R;

import java.util.List;

import listfragment.BaseListAdaptor;
import listfragment.BaseListFragment;
import listfragment.CompareStrategyFactory;
import main.MainApplication;
import main.Preferences;
import tools.SortOrderCompareStrategy;
import tools.Sortable;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizletTermListFragment extends BaseListFragment<QuizletTerm> implements Sortable {

    //// Creation, initialization, restoration

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        getMainApplication().addQuizletServiceListener(new MainApplication.ReadyListener() {
            @Override
            public void onReady() {
                onQuizletServiceLoaded(savedInstanceState);
            }
        });

        reload();
    }

    //// Events

    private void onQuizletServiceLoaded(Bundle savedInstanceState) {
        providerFactory = createFactory();
        restoreProviderIfNeeded(savedInstanceState);
        reload();
    }

    //// Creation methods

    @Override
    protected BaseListAdaptor createAdapter() {
        return createTermAdapter();
    }

    private QuizletTermAdapter createTermAdapter() {
        QuizletTermAdapter adapter = new QuizletTermAdapter(new QuizletTermAdapter.Listener() {
            @Override
            public void onTermClicked(View view, QuizletTerm card) {
                QuizletTermListFragment.this.getListener().onRowClicked(card);
            }

            @Override
            public void onMenuClicked(View view, QuizletTerm card) {
                QuizletTermListFragment.this.getListener().onRowMenuClicked(card, view);
            }
        });

        return adapter;
    }

    private void createFactoryIfNeeded() {
        if (providerFactory == null) {
            providerFactory = createFactory();
        }
    }

    @NonNull
    private QuizletTermListFactory createFactory() {
        return new QuizletTermListFactory(getQuizletService());
    }

    @Override
    public CompareStrategyFactory<QuizletTerm> createCompareStrategyFactory() {
        return new QuizletTermCompareStrategyFactory();
    }

    //// Setters

    // Set Data

    public void setTermSet(QuizletSet set) {
        createFactoryIfNeeded();
        provider = providerFactory.createFromObject(set);
    }

    public void setTerms(List<QuizletTerm> terms) {
        provider = providerFactory.createFromList(terms);
    }

    // Set UI

    @Override
    public void setSortOrder(Preferences.SortOrder sortOrder) {
        Preferences.setQuizletTermSortOrder(sortOrder);

        createCompareStrategyFactoryIfNeeded();
        setCompareStrategy(getCompareStrategyFactory().createStrategy(sortOrder));
        reload();
    }

    //// Getters

    // Get App Data

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public QuizletService getQuizletService() {
        return getMainApplication().getQuizletService();
    }

    // Get Data

    @Override
    public Preferences.SortOrder getSortOrder() {
        return getCompareStrategy().getSortOrder();
    }

    public QuizletSet getParentSet() {
        QuizletSet parentSet = null;
        if (provider instanceof QuizletSetTermListProvider) {
            QuizletSetTermListProvider setProvider = (QuizletSetTermListProvider)provider;
            parentSet = setProvider.getSet();
        }

        return parentSet;
    }

    // Cast Getters

    private QuizletTermCompareStrategyFactory getCompareStrategyFactory() {
        return (QuizletTermCompareStrategyFactory)compareStrategyFactory;
    }

    private SortOrderCompareStrategy getCompareStrategy() {
        return (SortOrderCompareStrategy)compareStrategy;
    }
}
