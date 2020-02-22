package com.example.alexeyglushkov.wordteacher.quizletlistmodules;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import com.example.alexeyglushkov.wordteacher.listmodule.view.SimpleListFragment;
import com.example.alexeyglushkov.wordteacher.listmodule.view.SimpleListFragmentListenerAdapter;
import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.view.QuizletSetListFragment;
import com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter.QuizletSetListPresenter;
import com.example.alexeyglushkov.wordteacher.quizletlistmodules.termlistmodule.view.QuizletTermListFragment;
import com.example.alexeyglushkov.wordteacher.quizletlistmodules.termlistmodule.presenter.QuizletTermListPresenter;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.StackModule;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.StackModuleFactory;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.StackModuleItem;

/**
 * Created by alexeyglushkov on 06.11.16.
 */

public class QuizletStackModuleFactory implements StackModuleFactory {
    private SimpleListFragment.Listener<QuizletSet> quizletSetListener;
    private SimpleListFragment.Listener<QuizletTerm> quizletTermListener;

    @Override
    public StackModuleItem rootModule(final StackModule stackModule) {
        QuizletSetListPresenter listPresenter = new QuizletSetListPresenter();
        QuizletSetListFragment view = QuizletSetListFragment.create();
        bindSetListPresenterListener(listPresenter, stackModule);

        listPresenter.setView(view);
        view.setPresenter(listPresenter);

        listPresenter.setSortOrder(Preferences.INSTANCE.getQuizletSetSortOrder());
        return listPresenter;
    }

    private void bindSetListPresenterListener(QuizletSetListPresenter presenter, final StackModule stackModule) {
        presenter.setListener(new SimpleListFragmentListenerAdapter<QuizletSet>(createQuizletSetListenerProvider()) {
            @Override
            public void onRowClicked(QuizletSet data) {
                stackModule.push(data, null);
                super.onRowClicked(data);
            }
        });
    }

    @Override
    public StackModuleItem moduleFromObject(Object object, StackModule stackModule) {
        QuizletTermListPresenter listPresenter = new QuizletTermListPresenter();
        QuizletTermListFragment fragment = QuizletTermListFragment.create();
        bindTermListPresenterListener(listPresenter, stackModule);

        listPresenter.setView(fragment);
        fragment.setPresenter(listPresenter);
        listPresenter.setTermSet((QuizletSet) object);

        listPresenter.setSortOrder(Preferences.INSTANCE.getQuizletTermSortOrder());
        return listPresenter;
    }

    private void bindTermListPresenterListener(QuizletTermListPresenter presenter, StackModule stackModule) {
        presenter.setListener(new SimpleListFragmentListenerAdapter<QuizletTerm>(createQuizletTermListenerProvider()));
    }

    @Override
    public StackModuleItem restoreModule(Object viewObject, StackModule stackModule) {
        StackModuleItem item = null;
        if (viewObject instanceof QuizletSetListFragment) {
            QuizletSetListFragment setListFragment = (QuizletSetListFragment)viewObject;
            QuizletSetListPresenter setListPresenter = (QuizletSetListPresenter)setListFragment.getPresenter();
            bindSetListPresenterListener(setListPresenter, stackModule);
            item = setListPresenter;

        } else if (viewObject instanceof QuizletTermListFragment) {
            QuizletTermListFragment termListFragment = (QuizletTermListFragment)viewObject;
            QuizletTermListPresenter termListPresenter = (QuizletTermListPresenter)termListFragment.getPresenter();
            bindTermListPresenterListener(termListPresenter, stackModule);
            item = termListPresenter;
        }

        return item;
    }

    //// Creation methods

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

    public void setQuizletSetListener(SimpleListFragment.Listener<QuizletSet> quizletSetListener) {
        this.quizletSetListener = quizletSetListener;
    }

    public void setQuizletTermListener(SimpleListFragment.Listener<QuizletTerm> quizletTermListener) {
        this.quizletTermListener = quizletTermListener;
    }
}
