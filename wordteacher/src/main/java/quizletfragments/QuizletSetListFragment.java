package quizletfragments;

import android.view.View;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import listfragment.BaseListAdaptor;
import listfragment.BaseListFragment;
import main.Preferences;

/**
 * Created by alexeyglushkov on 07.08.16.
 */
public class QuizletSetListFragment extends BaseListFragment<QuizletSet> {
    private Preferences.SortOrder sortOrder = Preferences.getQuizletSetSortOrder();

    public void setSortOrder(Preferences.SortOrder sortOrder) {
        Preferences.setQuizletSetSortOrder(sortOrder);

        this.sortOrder = sortOrder;
        updateAdapter();
    }

    public Preferences.SortOrder getSortOrder() {
        return sortOrder;
    }

    public boolean hasSets() {
        List<QuizletSet> sets = getSets();
        int count = sets != null ? sets.size() : 0;
        return count > 0;
    }

    public List<QuizletSet> getSets() {
        List<QuizletSet> sets = getSetAdapter().getSets();;
        return sets;
    }

    @Override
    protected BaseListAdaptor createAdapter() {
        return createSetAdapter();
    }

    private QuizletSetAdapter createSetAdapter() {
        QuizletSetAdapter adapter = new QuizletSetAdapter(new QuizletSetAdapter.Listener() {
            @Override
            public void onSetClicked(View view, QuizletSet set) {
                QuizletSetListFragment.this.getListener().onRowClicked(view, set);
            }

            @Override
            public void onMenuClicked(View view, QuizletSet set) {
                QuizletSetListFragment.this.onSetMenuClicked(view, set);
            }
        });

        return adapter;
    }

    private QuizletSetAdapter getSetAdapter() {
        return (QuizletSetAdapter)adapter;
    }

    public List<QuizletTerm> getTerms(List<QuizletSet> sets) {
        List<QuizletTerm> cards = new ArrayList<>();
        for (QuizletSet set : sets) {
            cards.addAll(set.getTerms());
        }

        return cards;
    }

    private List<QuizletSet> sortSets(List<QuizletSet> sets) {
        Collections.sort(sets, new Comparator<QuizletSet>() {
            @Override
            public int compare(QuizletSet lhs, QuizletSet rhs) {
                return compareSets(lhs, rhs);
            }
        });

        return sets;
    }

    private int compareSets(QuizletSet lhs, QuizletSet rhs) {
        switch (sortOrder) {
            case BY_NAME: return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
            case BY_NAME_INV: return rhs.getTitle().compareToIgnoreCase(lhs.getTitle());
            case BY_CREATE_DATE: return compare(lhs.getCreateDate(), rhs.getCreateDate());
            case BY_CREATE_DATE_INV: return compare(rhs.getCreateDate(), lhs.getCreateDate());
        }

        return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
    }
}
