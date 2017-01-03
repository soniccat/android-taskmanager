package com.example.alexeyglushkov.wordteacher;

import android.view.View;
import android.widget.StackView;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import listmodule.view.BaseListFragment;
import pagermodule.PagerModule;
import pagermodule.PagerModuleFactory;
import pagermodule.PagerModuleItem;
import quizletfragments.QuizletStackModuleFactory;
import quizletfragments.terms.QuizletTermListFragment;
import quizletfragments.terms.QuizletTermListPresenter;
import stackmodule.StackModule;
import stackmodule.StackModuleFactory;
import stackmodule.StackModuleListener;
import stackmodule.presenter.StackPresenter;
import stackmodule.view.StackFragment;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public class MainPagerFactory implements PagerModuleFactory {
    private BaseListFragment.Listener<QuizletSet> quizletSetListener;
    private BaseListFragment.Listener<QuizletTerm> quizletTermListener;

    @Override
    public PagerModuleItem moduleAtIndex(int i, final PagerModule pagerModule) {
        PagerModuleItem item = null;
        switch (i) {
            case 0: {
                QuizletStackModuleFactory factory = new QuizletStackModuleFactory();
                factory.setQuizletSetListener(createQuizletSetListener());
                factory.setQuizletTermListener(createQuizletTermListener());

                StackFragment view = new StackFragment();
                StackPresenter stackPresenter = new StackPresenter();
                stackPresenter.setFactory(factory);
                stackPresenter.setView(view);
                setStackListener(pagerModule, stackPresenter);
                view.setPresenter(stackPresenter);

                item = stackPresenter;
                break;
            }
            case 1: {
                QuizletTermListPresenter listPresenter = new QuizletTermListPresenter();
                QuizletTermListFragment view = QuizletTermListFragment.create();
                view.setListener(createQuizletTermListener());

                listPresenter.setView(view);
                view.setPresenter(listPresenter);

                item = listPresenter;
                break;
            }
            case 2:
                break;
        }

        return item;
    }

    private void setStackListener(final PagerModule pagerModule, StackPresenter stackPresenter) {
        stackPresenter.setListener(new StackModuleListener() {
            @Override
            public void onBackStackChanged() {
                pagerModule.updatePage(0);
            }
        });
    }

    @Override
    public PagerModuleItem restoreModule(int i, Object viewObject, PagerModule pagerModule) {
        PagerModuleItem item = null;

        if (i == 0) {
            StackFragment view = (StackFragment)viewObject;
            StackPresenter presenter = view.getPresenter();

            QuizletStackModuleFactory factory = (QuizletStackModuleFactory)presenter.getFactory();
            factory.setQuizletSetListener(createQuizletSetListener());
            factory.setQuizletTermListener(createQuizletTermListener());

            setStackListener(pagerModule, view.getPresenter());

            item = presenter;

        } else if (i == 1) {
            QuizletTermListFragment view = (QuizletTermListFragment)viewObject;
            QuizletTermListPresenter presenter = (QuizletTermListPresenter)view.getPresenter();
            view.setListener(createQuizletTermListener());

            item = presenter;
        }

        return item;
    }

    //// Creation methods

    private BaseListFragment.Listener<QuizletSet> createQuizletSetListener() {
        return new BaseListFragment.Listener<QuizletSet>() {
            @Override
            public void onRowClicked(QuizletSet data) {
                if (quizletSetListener != null) {
                    quizletSetListener.onRowClicked(data);
                }
            }

            @Override
            public void onRowMenuClicked(QuizletSet data, View view) {
                if (quizletSetListener != null) {
                    quizletSetListener.onRowMenuClicked(data, view);
                }
            }

            @Override
            public void onRowViewDeleted(QuizletSet data) {
                if (quizletSetListener != null) {
                    quizletSetListener.onRowViewDeleted(data);
                }
            }
        };
    }

    private BaseListFragment.Listener<QuizletTerm> createQuizletTermListener() {
        return new BaseListFragment.Listener<QuizletTerm>() {
            @Override
            public void onRowClicked(QuizletTerm data) {
                if (quizletTermListener != null) {
                    quizletTermListener.onRowClicked(data);
                }
            }

            @Override
            public void onRowMenuClicked(QuizletTerm data, View view) {
                if (quizletTermListener != null) {
                    quizletTermListener.onRowMenuClicked(data, view);
                }
            }

            @Override
            public void onRowViewDeleted(QuizletTerm data) {
                if (quizletTermListener != null) {
                    quizletTermListener.onRowViewDeleted(data);
                }
            }
        };
    }

    //// Setters

    public void setQuizletSetListener(BaseListFragment.Listener<QuizletSet> quizletSetListener) {
        this.quizletSetListener = quizletSetListener;
    }

    public void setQuizletTermListener(BaseListFragment.Listener<QuizletTerm> quizletTermListener) {
        this.quizletTermListener = quizletTermListener;
    }
}
