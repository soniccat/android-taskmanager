package com.example.alexeyglushkov.wordteacher;

import android.widget.StackView;

import pagermodule.PagerModule;
import pagermodule.PagerModuleFactory;
import pagermodule.PagerModuleItem;
import quizletfragments.QuizletStackModuleFactory;
import stackmodule.StackModule;
import stackmodule.StackModuleFactory;
import stackmodule.presenter.StackPresenter;
import stackmodule.view.StackFragment;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public class MainPagerFactory implements PagerModuleFactory {
    @Override
    public PagerModuleItem moduleAtIndex(int i, PagerModule stackModule) {
        PagerModuleItem item = null;
        switch (i) {
            case 0:
                QuizletStackModuleFactory factory = new QuizletStackModuleFactory();
                StackFragment view = new StackFragment();

                StackPresenter stackPresenter = new StackPresenter();
                stackPresenter.setFactory(factory);
                stackPresenter.setView(view);
                view.setPresenter(stackPresenter);

                item = stackPresenter;
                break;
            case 1:
                break;
            case 2:
                break;
        }

        return item;
    }

    @Override
    public PagerModuleItem restoreModule(int i, PagerModule stackModule) {
        return null;
    }
}
