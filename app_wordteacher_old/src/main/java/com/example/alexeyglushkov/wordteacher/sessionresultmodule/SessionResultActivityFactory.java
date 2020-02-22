package com.example.alexeyglushkov.wordteacher.sessionresultmodule;

import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.ActivityModule;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter.ActivityModuleItem;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter.ActivityPresenterFactory;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view.ActivityModuleItemView;
import com.example.alexeyglushkov.wordteacher.sessionresultmodule.presenter.SessionResultPresenterImp;
import com.example.alexeyglushkov.wordteacher.sessionresultmodule.view.SessionResultFragment;

/**
 * Created by alexeyglushkov on 08.04.17.
 */

public class SessionResultActivityFactory implements ActivityPresenterFactory {
    @Override
    public ActivityModuleItem createModule(ActivityModule module) {
        SessionResultPresenterImp presenter = new SessionResultPresenterImp();
        SessionResultFragment view = new SessionResultFragment();

        view.setPresenter(presenter);
        presenter.setView(view);

        return presenter;
    }

    @Override
    public ActivityModuleItem restore(ActivityModuleItemView view, ActivityModule module) {
        SessionResultPresenterImp presenter = (SessionResultPresenterImp)((SessionResultFragment)view).getPresenter();
        return presenter;
    }
}
