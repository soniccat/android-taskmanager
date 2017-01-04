package quizletfragments;

import android.view.View;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import listmodule.view.BaseListFragment;
import listmodule.view.SimpleListFragment;
import quizletfragments.sets.QuizletSetListFragment;
import quizletfragments.sets.QuizletSetListPresenter;
import quizletfragments.terms.QuizletTermListFragment;
import quizletfragments.terms.QuizletTermListPresenter;
import stackmodule.StackModule;
import stackmodule.StackModuleFactory;
import stackmodule.StackModuleItem;

/**
 * Created by alexeyglushkov on 06.11.16.
 */

public class QuizletStackModuleFactory implements StackModuleFactory {
    private SimpleListFragment.Listener<QuizletSet> quizletSetListener;
    private SimpleListFragment.Listener<QuizletTerm> quizletTermListener;

    @Override
    public StackModuleItem rootModule(final StackModule stackModule) {
        QuizletSetListPresenter listPresenter = new QuizletSetListPresenter();
        QuizletSetListFragment fragment = QuizletSetListFragment.create();
        bindSetListPresenterListener(listPresenter, stackModule);

        listPresenter.setView(fragment);
        fragment.setPresenter(listPresenter);

        return listPresenter;
    }

    private void bindSetListPresenterListener(QuizletSetListPresenter presenter, final StackModule stackModule) {
        presenter.setListener(new SimpleListFragment.Listener<QuizletSet>() {
            @Override
            public void onRowClicked(QuizletSet data) {
                stackModule.push(data, null);
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

        return listPresenter;
    }

    private void bindTermListPresenterListener(QuizletTermListPresenter presenter, StackModule stackModule) {
        presenter.setListener(new SimpleListFragment.Listener<QuizletTerm>() {
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
        });
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

    //// Setters

    public void setQuizletSetListener(SimpleListFragment.Listener<QuizletSet> quizletSetListener) {
        this.quizletSetListener = quizletSetListener;
    }

    public void setQuizletTermListener(SimpleListFragment.Listener<QuizletTerm> quizletTermListener) {
        this.quizletTermListener = quizletTermListener;
    }
}
