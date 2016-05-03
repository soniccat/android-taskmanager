package com.example.alexeyglushkov.wordteacher;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizletCardsFragment extends Fragment {

    enum ViewType {
        Sets,
        Cards
    }

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;
    private ViewType viewType = ViewType.Sets;

    public QuizletCardsFragment() {
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

        recreateAdapter();
    }

    public void updateSets(List<QuizletSet> sets) {
        if (viewType == ViewType.Sets) {
            getSetAdapter().updateSets(sets);
        } else {
            getWordAdapter().updateCards(getCards(sets));
        }
    }

    public List<QuizletTerm> getCards(List<QuizletSet> sets) {
        List<QuizletTerm> cards = new ArrayList<>();
        for (QuizletSet set : sets) {
            cards.addAll(set.getTerms());
        }

        Collections.sort(cards, new Comparator<QuizletTerm>() {
            @Override
            public int compare(QuizletTerm lhs, QuizletTerm rhs) {
                return lhs.getTerm().compareTo(rhs.getTerm());
            }
        });

        return cards;
    }

    public void updateViewType(ViewType aViewType) {
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
            recyclerView.setAdapter(adapter);
        }
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
                onCardClicked(view, card);
            }

            @Override
            public void onMenuClicked(View view, QuizletTerm card) {
                onTermMenuClicked(view, card);
            }
        });

        return adapter;
    }

    private void onSetClicked(View v, QuizletSet set) {

    }

    private void onSetMenuClicked(View v, QuizletSet set) {

    }

    private void onTermClicked(View v, QuizletTerm term) {

    }

    private void onTermMenuClicked(View v, QuizletTerm term) {

    }
}
