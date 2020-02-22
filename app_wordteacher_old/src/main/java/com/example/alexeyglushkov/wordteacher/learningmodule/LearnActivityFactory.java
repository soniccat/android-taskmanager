package com.example.alexeyglushkov.wordteacher.learningmodule;

import android.content.Intent;

import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.ActivityModule;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter.ActivityModuleItem;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter.ActivityPresenter;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter.ActivityPresenterFactory;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view.ActivityModuleItemView;
import com.example.alexeyglushkov.wordteacher.learningmodule.presenter.LearnPresenterImp;
import com.example.alexeyglushkov.wordteacher.learningmodule.view.LearnFragment;

/**
 * Created by alexeyglushkov on 09.04.17.
 */

public class LearnActivityFactory implements ActivityPresenterFactory {
    @Override
    public ActivityModuleItem createModule(ActivityModule module) {
        final LearnPresenterImp presenter = new LearnPresenterImp();
        LearnFragment view = new LearnFragment();

        presenter.setView(view);
        view.setPresenter(presenter);

        bindPresenterListener(presenter, module);
        return presenter;
    }

    @Override
    public ActivityModuleItem restore(ActivityModuleItemView view, ActivityModule module) {
        LearnPresenterImp presenter = (LearnPresenterImp)((LearnFragment)view).getPresenter();

        bindPresenterListener(presenter, module);
        return presenter;
    }

    private void bindPresenterListener(final LearnPresenterImp presenter, ActivityModule module) {
        ActivityPresenter activityPresenter = (ActivityPresenter)module;
        activityPresenter.setListener(new ActivityPresenter.Listener() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                presenter.onActivityResult(requestCode, resultCode, data);
            }
        });
    }
}
