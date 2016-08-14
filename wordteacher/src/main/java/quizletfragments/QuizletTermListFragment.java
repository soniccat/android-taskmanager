package quizletfragments;

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
import main.MainApplication;
import main.Preferences;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizletTermListFragment extends BaseListFragment<QuizletTerm> implements QuizletSortable {
    public static final String PARENT_SET_ID = "PARENT_SET_ID";
    public static final String STORE_TERM_IDS = "STORE_TERM_IDS";

    private QuizletTermListProvider provider;
    private Preferences.SortOrder sortOrder = Preferences.getQuizletSetSortOrder();

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public QuizletService getQuizletService() {
        return getMainApplication().getQuizletService();
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
            outState.putLongArray(STORE_TERM_IDS, getIdArray());
        }
    }

    private long[] getIdArray() {
        List<QuizletTerm> terms = getTerms();
        long[] ids = new long[getTerms().size()];

        for (int i=0; i<terms.size(); ++i) {
            ids[i] = terms.get(i).getId();
        }

        return ids;
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            getMainApplication().addCourseHolderListener(new MainApplication.ReadyListener() {
                @Override
                public void onReady() {
                    onQuizletServiceLoaded(savedInstanceState);
                }
            });
        } else {
            reload();
        }
    }

    private void onQuizletServiceLoaded(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(PARENT_SET_ID)) {
            long setId = savedInstanceState.getLong(PARENT_SET_ID);
            provider = createSetProvider(setId);

        } else {
            long[] ids = savedInstanceState.getLongArray(STORE_TERM_IDS);
            provider = createTermListProvider(getQuizletTerms(ids));
        }

        reload();
    }

    private List<QuizletTerm> getQuizletTerms(long[] ids) {
        List<QuizletTerm> terms = new ArrayList<>();
        for (long id : ids) {
            QuizletTerm term = getQuizletService().getTerm(id);
            terms.add(term);
        }

        return terms;
    }

    private QuizletTermListProvider createSetProvider(long setId) {
        QuizletSet set = getQuizletService().getSet(setId);
        Assert.assertNotNull(set);

        return createSetProvider(set);
    }

    @NonNull
    private QuizletTermListProvider createSetProvider(QuizletSet set) {
        return new QuizletSetTermListProvider(set);
    }

    private QuizletTermListProvider createTermListProvider(List<QuizletTerm> terms) {
        return new QuizletSimpleTermListProvider(terms);
    }

    public void setParentSet(QuizletSet set) {
        this.provider = createSetProvider(set);
    }

    public QuizletSet getParentSet() {
        QuizletSet parentSet = null;
        if (provider instanceof QuizletSetTermListProvider) {
            QuizletSetTermListProvider setProvider = (QuizletSetTermListProvider)provider;
            parentSet = setProvider.getSet();
        }

        return parentSet;
    }

    public void setTermSet(QuizletSet set) {
        provider = createSetProvider(set);
        setAdapterTerms(getTerms());
    }

    public void setTerms(List<QuizletTerm> terms) {
        provider = createTermListProvider(terms);
        setAdapterTerms(getTerms());
    }

    private void setAdapterTerms(List<QuizletTerm> inTerms) {
        List<QuizletTerm> terms = new ArrayList<>();
        if (inTerms != null) {
            terms.addAll(inTerms);
            sortTerms(terms);
        }

        getTermAdapter().updateTerms(terms);
    }

    @Override
    protected BaseListAdaptor createAdapter() {
        return createTermAdapter();
    }

    public void reload() {
        setAdapterTerms(getTerms());
    }

    /*
    private void updateAdapter() {
        if (viewType == ViewType.Sets) {
            getSetAdapter().setSets(sortSets(getSetAdapter().getSets()));
        } else {
            getTermAdapter().updateTerms(sortTerms(getTermAdapter().getTerms()));
        }
    }
    */

    public boolean hasTerms() {
        List<QuizletTerm> cards = getTerms();
        int count = cards != null ? cards.size() : 0;
        return count > 0;
    }

    public List<QuizletTerm> getTerms() {
        return provider != null ? provider.getTerms() : null;
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

    @Override
    public void setSortOrder(Preferences.SortOrder sortOrder) {
        Preferences.setQuizletTermSortOrder(sortOrder);

        this.sortOrder = sortOrder;
        reload();
    }

    @Override
    public Preferences.SortOrder getSortOrder() {
        return sortOrder;
    }
}
