package com.example.alexeyglushkov.wordteacher.learningmodule;

import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter.ActivityModuleItem;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter.ActivityPresenterFactory;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view.ActivityModuleItemView;
import com.example.alexeyglushkov.wordteacher.learningmodule.presenter.LearnPresenterImp;
import com.example.alexeyglushkov.wordteacher.learningmodule.view.LearnFragment;

/**
 * Created by alexeyglushkov on 09.04.17.
 */

public class LearnActivityFactory implements ActivityPresenterFactory {
    @Override
    public ActivityModuleItem createModule() {
        LearnPresenterImp presenter = new LearnPresenterImp();
        LearnFragment view = new LearnFragment();

        presenter.setView(view);
        view.setPresenter(presenter);

        return presenter;
    }

    @Override
    public ActivityModuleItem restore(ActivityModuleItemView view) {
        LearnPresenterImp presenter = (LearnPresenterImp)((LearnFragment)view).getPresenter();
        return presenter;
    }
}
