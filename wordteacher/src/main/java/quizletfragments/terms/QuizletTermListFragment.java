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

    public void reload() {
        setAdapterTerms(getItems());
    }

    public static int compare(long lhs, long rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }

    private List<QuizletTerm> sortTerms(List<QuizletTerm> terms) {
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
            case BY_CREATE_DATE: return compare(lhs.getRank(), rhs.getRank());
            case BY_CREATE_DATE_INV: return compare(rhs.getRank(), lhs.getRank());
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

    @NonNull
    private QuizletTermListFactory createFactory() {
        return new QuizletTermListFactory(getQuizletService());
    }

    //// Setters

    // Set Data

    public void setTermSet(QuizletSet set) {
        provider = factory.createFromObject(set);
    }

    public void setTerms(List<QuizletTerm> terms) {
        provider = factory.createFromList(terms);
    }

    // Set UI

    private void setAdapterTerms(List<QuizletTerm> inTerms) {
        List<QuizletTerm> terms = new ArrayList<>();
        if (inTerms != null) {
            terms.addAll(inTerms);
            sortTerms(terms);
        }

        getTermAdapter().updateTerms(terms);
    }

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

    private QuizletTermAdapter getTermAdapter() {
        return (QuizletTermAdapter)adapter;
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
