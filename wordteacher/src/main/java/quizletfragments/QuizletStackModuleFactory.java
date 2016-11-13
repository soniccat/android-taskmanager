package quizletfragments;

import android.view.View;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import listmodule.view.BaseListFragment;
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
    @Override
    public StackModuleItem rootModule(final StackModule stackModule) {
        QuizletSetListPresenter listPresenter = new QuizletSetListPresenter();
        QuizletSetListFragment fragment = QuizletSetListFragment.create();
        setSetListFragmentListener(fragment, stackModule);

        listPresenter.setView(fragment);
        fragment.setPresenter(listPresenter);

        return listPresenter;
    }

    private void setSetListFragmentListener(QuizletSetListFragment fragment, final StackModule stackModule) {
        fragment.setListener(new BaseListFragment.Listener<QuizletSet>() {
            @Override
            public void onRowClicked(QuizletSet data) {
                stackModule.push(data, null);
            }

            @Override
            public void onRowMenuClicked(QuizletSet data, View view) {
            }

            @Override
            public void onRowViewDeleted(QuizletSet data) {
            }
        });
    }

    @Override
    public StackModuleItem moduleFromObject(Object object, StackModule stackModule) {
        QuizletTermListPresenter listPresenter = new QuizletTermListPresenter();
        QuizletTermListFragment fragment = QuizletTermListFragment.create();
        setTermListFragmentListener(fragment, stackModule);

        listPresenter.setView(fragment);
        fragment.setPresenter(listPresenter);
        listPresenter.setTermSet((QuizletSet) object);

        return listPresenter;
    }

    private void setTermListFragmentListener(QuizletTermListFragment fragment, StackModule stackModule) {
        fragment.setListener(new BaseListFragment.Listener<QuizletTerm>() {
            @Override
            public void onRowClicked(QuizletTerm data) {
            }

            @Override
            public void onRowMenuClicked(QuizletTerm data, View view) {
            }

            @Override
            public void onRowViewDeleted(QuizletTerm data) {
            }
        });
    }

    @Override
    public StackModuleItem restoreModule(Object viewObject, StackModule stackModule) {
        StackModuleItem item = null;
        if (viewObject instanceof QuizletSetListFragment) {
            QuizletSetListFragment setListFragment = (QuizletSetListFragment)viewObject;
            item = (StackModuleItem)setListFragment.getPresenter();
            setSetListFragmentListener(setListFragment, stackModule);

        } else if (viewObject instanceof QuizletTermListFragment) {
            QuizletTermListFragment termListFragment = (QuizletTermListFragment)viewObject;
            item = (StackModuleItem)termListFragment.getPresenter();
            setTermListFragmentListener(termListFragment, stackModule);
        }

        return item;
    }
}
