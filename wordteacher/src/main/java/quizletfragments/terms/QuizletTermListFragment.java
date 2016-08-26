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

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import listfragment.BaseListAdaptor;
import listfragment.BaseListFragment;
import listfragment.NullStorableListProvider;
import listfragment.StorableListProvider;
import listfragment.StorableListProviderFactory;
import main.MainApplication;
import main.Preferences;
import quizletfragments.QuizletSortable;
import tools.LongTools;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizletTermListFragment extends BaseListFragment<QuizletTerm> implements QuizletSortable {

    private Preferences.SortOrder sortOrder = Preferences.getQuizletSetSortOrder();

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
        factory = createFactory();
        restoreProviderIfNeeded(savedInstanceState);
        reload();
    }

    //// Actions

    protected List<QuizletTerm> sortItems(List<QuizletTerm> terms) {
        Collections.sort(terms, new Comparator<QuizletTerm>() {
            @Override
            public int compare(QuizletTerm lhs, QuizletTerm rhs) {
                return compareQuizletTerms(lhs, rhs);
            }
        });

        return terms;
    }

    private int compareQuizletTerms(QuizletTerm lhs, QuizletTerm rhs) {
        switch (sortOrder) {
            case BY_NAME: return lhs.getTerm().compareToIgnoreCase(rhs.getTerm());
            case BY_NAME_INV: return rhs.getTerm().compareToIgnoreCase(lhs.getTerm());
            case BY_CREATE_DATE: return LongTools.compare(lhs.getRank(), rhs.getRank());
            case BY_CREATE_DATE_INV: return LongTools.compare(rhs.getRank(), lhs.getRank());
        }

        return lhs.getTerm().compareToIgnoreCase(rhs.getTerm());
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
        if (factory == null) {
            factory = createFactory();
        }
    }

    @NonNull
    private QuizletTermListFactory createFactory() {
        return new QuizletTermListFactory(getQuizletService());
    }

    //// Setters

    // Set Data

    public void setTermSet(QuizletSet set) {
        createFactoryIfNeeded();
        provider = factory.createFromObject(set);
    }

    public void setTerms(List<QuizletTerm> terms) {
        provider = factory.createFromList(terms);
    }

    // Set UI

    @Override
    public void setSortOrder(Preferences.SortOrder sortOrder) {
        Preferences.setQuizletTermSortOrder(sortOrder);

        this.sortOrder = sortOrder;
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
        return sortOrder;
    }

    public QuizletSet getParentSet() {
        QuizletSet parentSet = null;
        if (provider instanceof QuizletSetTermListProvider) {
            QuizletSetTermListProvider setProvider = (QuizletSetTermListProvider)provider;
            parentSet = setProvider.getSet();
        }

        return parentSet;
    }
}
