package com.example.alexeyglushkov.wordteacher;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import main.Preferences;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizletCardsFragment extends Fragment {

    enum ViewType {
        Sets,
        Cards
    }

    private QuizletSet parentSet;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;
    private ViewType viewType = ViewType.Sets;
    private Listener listener;

    private Preferences.SortOrder sortOrder = Preferences.SortOrder.BY_NAME;

    public QuizletCardsFragment() {
    }

    public void setSortOrder(Preferences.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        updateAdapter();
    }

    public Preferences.SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        if (adapter == null) {
            recreateAdapter();
        }

        if (recyclerView.getAdapter() == null) {
            applyAdapter();
        }
    }

    private void restore(@Nullable Bundle savedInstanceState) {
        int intViewType = savedInstanceState.getInt("viewType");
        viewType = ViewType.values()[intViewType];
        parentSet = savedInstanceState.getParcelable("parentSet");
        sortOrder = Preferences.SortOrder.values()[savedInstanceState.getInt("sortOrder")];
        recreateAdapter();
        restoreAdapter(savedInstanceState);
    }

    private void restoreAdapter(@Nullable Bundle savedInstanceState) {
        Parcelable parcelable = savedInstanceState.getParcelable("adapter");
        if (viewType == ViewType.Sets) {
            getSetAdapter().onRestoreInstanceState(parcelable);
        } else {
            getWordAdapter().onRestoreInstanceState(parcelable);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("viewType", viewType.ordinal());
        outState.putParcelable("parentSet", parentSet);
        outState.putInt("sortOrder", sortOrder.ordinal());
        saveAdapterState(outState);
    }

    private void saveAdapterState(Bundle outState) {
        Parcelable parcelable;
        if (viewType == ViewType.Sets) {
            parcelable = getSetAdapter().onSaveInstanceState();
        } else {
            parcelable = getWordAdapter().onSaveInstanceState();
        }

        outState.putParcelable("adapter", parcelable);
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
            getWordAdapter().updateCards(sortCards(getCards(sets)));
        }
    }

    private void updateAdapter() {
        if (viewType == ViewType.Sets) {
            getSetAdapter().updateSets(sortSets(getSetAdapter().getSets()));
        } else {
            getWordAdapter().updateCards(sortCards(getWordAdapter().getCards()));
        }
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

    public static int compare(long lhs, long rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }

    public List<QuizletTerm> getCards(List<QuizletSet> sets) {
        List<QuizletTerm> cards = new ArrayList<>();
        for (QuizletSet set : sets) {
            cards.addAll(set.getTerms());
        }

        return cards;
    }

    private List<QuizletTerm> sortCards(List<QuizletTerm> cards) {
        Collections.sort(cards, new Comparator<QuizletTerm>() {
            @Override
            public int compare(QuizletTerm lhs, QuizletTerm rhs) {
                return compareQuizletTerms(lhs, rhs);
            }
        });

        return cards;
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

    public void setViewType(ViewType aViewType) {
        if (viewType != aViewType) {
            viewType = aViewType;
            recreateAdapter();
        }
    }

    private QuizletSetAdapter getSetAdapter() {
        return (QuizletSetAdapter)adapter;
    }

    private QuizletCardAdapter getWordAdapter() {
        return (QuizletCardAdapter)adapter;
    }

    private void recreateAdapter() {
        if (viewType == ViewType.Sets) {
            adapter = createSetAdapter();
        } else {
            adapter = createWordAdapter();
        }

        if (recyclerView != null) {
            applyAdapter();
        }
    }

    private void applyAdapter() {
        recyclerView.setAdapter(adapter);
    }

    private QuizletSetAdapter createSetAdapter() {
        QuizletSetAdapter adapter = new QuizletSetAdapter(new QuizletSetAdapter.Listener() {
            @Override
            public void onSetClicked(View view, QuizletSet set) {
                QuizletCardsFragment.this.onSetClicked(view, set);
            }

            @Override
            public void onMenuClicked(View view, QuizletSet set) {
                QuizletCardsFragment.this.onSetMenuClicked(view, set);
            }
        });

        return adapter;
    }

    private QuizletCardAdapter createWordAdapter() {
        QuizletCardAdapter adapter = new QuizletCardAdapter(new QuizletCardAdapter.Listener() {
            @Override
            public void onCardClicked(View view, QuizletTerm card) {
                QuizletCardsFragment.this.onCardClicked(view, card);
            }

            @Override
            public void onMenuClicked(View view, QuizletTerm card) {
                QuizletCardsFragment.this.onTermMenuClicked(view, card);
            }
        });

        return adapter;
    }

    private void onSetClicked(View v, QuizletSet set) {
        listener.onSetClicked(set);
    }

    private void onSetMenuClicked(View v, QuizletSet set) {
        listener.onSetMenuClicked(set, v);
    }

    private void onCardClicked(View v, QuizletTerm card) {
        listener.onTermClicked(card);
    }

    private void onTermMenuClicked(View v, QuizletTerm card) {
        listener.onTermMenuClicked(card, v);
    }

    public interface Listener {
        void onSetClicked(QuizletSet set);
        void onSetMenuClicked(QuizletSet set, View view);
        void onTermClicked(QuizletTerm card);
        void onTermMenuClicked(QuizletTerm card, View view);
    }
}
