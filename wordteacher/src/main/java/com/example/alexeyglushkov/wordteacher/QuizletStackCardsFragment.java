package com.example.alexeyglushkov.wordteacher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 11.06.16.
 */
public class QuizletStackCardsFragment extends StackContainer implements QuizletCardsFragment.Listener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getSetFragment() == null) {
            showSetFragment();
        } else {
            restoreListeners();
        }
    }

    public void updateSets(List<QuizletSet> sets) {
        QuizletCardsFragment setFragment = getSetFragment();
        setFragment.updateSets(sets);
    }

    private void showSetFragment() {
        QuizletCardsFragment setFragment = new QuizletCardsFragment();
        setFragment.setViewType(QuizletCardsFragment.ViewType.Sets);
        setFragment.setListener(this);

        addFragment(setFragment, new TransactionCallback() {
            @Override
            public void onFinished() {
            }
        });
    }

    private void showWordFragment(QuizletSet set) {
        QuizletCardsFragment fragment = new QuizletCardsFragment();
        fragment.setListener(this);
        fragment.setViewType(QuizletCardsFragment.ViewType.Cards);

        ArrayList<QuizletSet> list = new ArrayList<>();
        list.add(set);

        fragment.setParentSet(set);
        fragment.updateSets(list);

        addFragment(fragment, new TransactionCallback() {
            @Override
            public void onFinished() {
            }
        });
    }

    private void restoreListeners() {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            QuizletCardsFragment quizletFragment = (QuizletCardsFragment)fragment;
            quizletFragment.setListener(this);
        }
    }

    private QuizletCardsFragment getSetFragment() {
        List<Fragment> list = getChildFragmentManager().getFragments();
        return list != null && list.size() > 0 ? (QuizletCardsFragment)list.get(0) : null;
    }

    private QuizletCardsFragment getCardsFragment() {
        List<Fragment> list = getChildFragmentManager().getFragments();
        return list != null && list.size() > 1 ? (QuizletCardsFragment)list.get(1) : null;
    }

    public void setListener(Listener listener) {
        super.setListener(listener);
    }

    private Listener getStackListener() {
        return (Listener)this.listener;
    }

    public String getTitle() {
        String title = null;
        if (getBackStackSize() > 0) {
            title = getCardsFragment().getParentSet().getTitle();
        } else {
            title = "Sets";
        }

        return title;
    }

    // QuizletCardsFragment.Listener

    @Override
    public void onSetClicked(QuizletSet set) {
        showWordFragment(set);
    }

    @Override
    public void onSetMenuClicked(QuizletSet set, View view) {
        getStackListener().onSetMenuClicked(set, view);
    }

    @Override
    public void onTermClicked(QuizletTerm card) {
        getStackListener().onTermClicked(card);
    }

    @Override
    public void onTermMenuClicked(QuizletTerm card, View view) {
        getStackListener().onTermMenuClicked(card, view);
    }

    public interface Listener extends QuizletCardsFragment.Listener, StackContainer.Listener {
    }
}
