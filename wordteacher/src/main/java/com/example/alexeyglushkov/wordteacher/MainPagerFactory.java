package com.example.alexeyglushkov.wordteacher;

import android.support.annotation.NonNull;
import android.view.View;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import listmodule.view.BaseListFragment;
import listmodule.view.SimpleListFragment;
import listmodule.view.SimpleListFragmentListenerAdapter;
import pagermodule.PagerModule;
import pagermodule.PagerModuleFactory;
import pagermodule.PagerModuleItem;
import quizletfragments.QuizletStackModuleFactory;
import quizletfragments.terms.QuizletTermListFragment;
import quizletfragments.terms.QuizletTermListPresenter;
import stackmodule.StackModuleListener;
import stackmodule.presenter.StackPresenter;
import stackmodule.view.StackFragment;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public class MainPagerFactory implements PagerModuleFactory {
    private StackModuleListener stackModuleListener;
    private SimpleListFragment.Listener<QuizletSet> quizletSetListener;
    private SimpleListFragment.Listener<QuizletTerm> quizletTermListener;

    //// Interfaces

    // PgerModuleFactory

    @Override
    public PagerModuleItem moduleAtIndex(int i, final PagerModule pagerModule) {
        PagerModuleItem item = null;
        switch (i) {
            case 0: {
                item = createQuizletStackModule(pagerModule);
                break;
            }
            case 1: {
                item = createQuizletTermsModule();
                break;
            }
            case 2:
                break;
        }

        return item;
    }

    @Override
    public PagerModuleItem restoreModule(int i, Object viewObject, PagerModule pagerModule) {
        PagerModuleItem item = null;

        if (i == 0) {
            item = restoreQuizletStackModule((StackFragment) viewObject, pagerModule);

        } else if (i == 1) {
            item = restoreQuizletTermListModule((QuizletTermListFragment) viewObject);
        }

        return item;
    }

    @NonNull
    private PagerModuleItem restoreQuizletStackModule(StackFragment view, PagerModule pagerModule) {
        StackPresenter presenter = view.getPresenter();

        QuizletStackModuleFactory factory = (QuizletStackModuleFactory)presenter.getFactory();
        factory.setQuizletSetListener(createQuizletSetListener());
        factory.setQuizletTermListener(createQuizletTermListener());

        bindStackListener(pagerModule, view.getPresenter());
        return presenter;
    }

    private PagerModuleItem restoreQuizletTermListModule(QuizletTermListFragment view) {
        QuizletTermListPresenter presenter = (QuizletTermListPresenter)view.getPresenter();
        presenter.setListener(createQuizletTermListener());

        return presenter;
    }

    //// Creation methods

    @NonNull
    private PagerModuleItem createQuizletTermsModule() {
        QuizletTermListPresenter listPresenter = new QuizletTermListPresenter();
        listPresenter.setListener(createQuizletTermListener());

        QuizletTermListFragment view = QuizletTermListFragment.create();
        listPresenter.setView(view);
        view.setPresenter(listPresenter);
        return listPresenter;
    }

    @NonNull
    private PagerModuleItem createQuizletStackModule(PagerModule pagerModule) {
        QuizletStackModuleFactory factory = new QuizletStackModuleFactory();
        factory.setQuizletSetListener(createQuizletSetListener());
        factory.setQuizletTermListener(createQuizletTermListener());

        StackFragment view = new StackFragment();
        StackPresenter stackPresenter = new StackPresenter();
        stackPresenter.setFactory(factory);
        stackPresenter.setView(view);
        bindStackListener(pagerModule, stackPresenter);
        view.setPresenter(stackPresenter);

        return stackPresenter;
    }

    private SimpleListFragment.Listener<QuizletSet> createQuizletSetListener() {
        return new SimpleListFragmentListenerAdapter<>(createQuizletSetListenerProvider());
    }

    private SimpleListFragment.Listener<QuizletTerm> createQuizletTermListener() {
        return new SimpleListFragmentListenerAdapter<>(createQuizletTermListenerProvider());
    }

    private SimpleListFragmentListenerAdapter.ListenerProvider<QuizletSet> createQuizletSetListenerProvider() {
        // we return provider because listener is null at the moment of restoration and will be set later
        return new SimpleListFragmentListenerAdapter.ListenerProvider<QuizletSet>() {
            @Override
            public SimpleListFragment.Listener<QuizletSet> getListener() {
                return quizletSetListener;
            }
        };
    }

    private SimpleListFragmentListenerAdapter.ListenerProvider<QuizletTerm> createQuizletTermListenerProvider() {
        // we return provider because listener is null at the moment of restoration and will be set later
        return new SimpleListFragmentListenerAdapter.ListenerProvider<QuizletTerm>() {
            @Override
            public SimpleListFragment.Listener<QuizletTerm> getListener() {
                return quizletTermListener;
            }
        };
    }


    //// Setters

    private void bindStackListener(final PagerModule pagerModule, StackPresenter stackPresenter) {
        stackPresenter.setListener(new StackModuleListener() {
            @Override
            public void onBackStackChanged() {
                pagerModule.updatePage(0);

                if (stackModuleListener != null) {
                    stackModuleListener.onBackStackChanged();
                }
            }
        });
    }

    public void setStackModuleListener(StackModuleListener stackModuleListener) {
        this.stackModuleListener = stackModuleListener;
    }

    public void setQuizletSetListener(SimpleListFragment.Listener<QuizletSet> quizletSetListener) {
        this.quizletSetListener = quizletSetListener;
    }

    public void setQuizletTermListener(SimpleListFragment.Listener<QuizletTerm> quizletTermListener) {
        this.quizletTermListener = quizletTermListener;
    }
}
