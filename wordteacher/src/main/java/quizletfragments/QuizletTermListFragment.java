package quizletfragments;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import listfragment.BaseListAdaptor;
import listfragment.BaseListFragment;
import main.Preferences;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizletTermListFragment extends BaseListFragment<QuizletTerm> {
    public static final String PARENT_SET_ID = "PARENT_SET_ID";
    public static final String STORE_TERM_IDS = "STORE_TERM_IDS";

    private QuizletTermListProvider provider;
    private Preferences.SortOrder sortOrder = Preferences.getQuizletSetSortOrder();

    public void setSortOrder(Preferences.SortOrder sortOrder) {
        Preferences.setQuizletTermSortOrder(sortOrder);

        this.sortOrder = sortOrder;
        reload();
    }

    public Preferences.SortOrder getSortOrder() {
        return sortOrder;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            restore(savedInstanceState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveProvider(outState);
    }

    private void saveProvider(Bundle outState) {
        if (provider instanceof QuizletSetTermListProvider) {
            QuizletSetTermListProvider setProvider = (QuizletSetTermListProvider)provider;
            outState.putLong(PARENT_SET_ID, setProvider.getSet().getId());

        } else {

        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    public void setParentSet(QuizletSet set) {
        this.parentSet = set;
    }

    public QuizletSet getParentSet() {
        return parentSet;
    }

    public void updateSets(List<QuizletSet> sets) {
        if (viewType == ViewType.Sets) {
            getSetAdapter().updateSets(sortSets(sets));
        } else {
            getTermAdapter().updateCards(sortTerms(getTerms(sets)));
        }
    }

    @Override
    protected BaseListAdaptor createAdapter() {
        return createTermAdapter();
    }

    public void setTerms(List<QuizletTerm> inTerms) {
        List<QuizletTerm> terms = new ArrayList<>();
        terms.addAll(sortTerms(inTerms));

        getTermAdapter().updateCards(terms);
    }

    public void reload() {
        setTerms(parentSet.getTerms());
    }

    /*
    private void updateAdapter() {
        if (viewType == ViewType.Sets) {
            getSetAdapter().updateSets(sortSets(getSetAdapter().getSets()));
        } else {
            getTermAdapter().updateCards(sortTerms(getTermAdapter().getTerms()));
        }
    }
    */

    public boolean hasTerms() {
        List<QuizletTerm> cards = getTerms();
        int count = cards != null ? cards.size() : 0;
        return count > 0;
    }

    public List<QuizletTerm> getTerms() {
        return parentSet.getTerms();
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

    private QuizletTermAdapter getTermAdapter() {
        return (QuizletTermAdapter)adapter;
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
}
