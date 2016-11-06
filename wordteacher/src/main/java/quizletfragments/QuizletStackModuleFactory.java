package quizletfragments;

import android.view.View;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import listmodule.view.BaseListFragment;
import quizletfragments.sets.QuizletSetListFragment;
import quizletfragments.sets.QuizletSetListPresenter;
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

        listPresenter.setView(fragment);
        fragment.setPresenter(listPresenter);

        return listPresenter;
    }

    @Override
    public StackModuleItem moduleFromObject(Object object, StackModule stackModule) {
        return null;
    }

    @Override
    public StackModuleItem restoreModule(Object object, StackModule stackModule) {
        return null;
    }
}
