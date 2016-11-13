package com.example.alexeyglushkov.wordteacher;

import android.widget.StackView;

import pagermodule.PagerModule;
import pagermodule.PagerModuleFactory;
import pagermodule.PagerModuleItem;
import quizletfragments.QuizletStackModuleFactory;
import stackmodule.StackModule;
import stackmodule.StackModuleFactory;
import stackmodule.StackModuleListener;
import stackmodule.presenter.StackPresenter;
import stackmodule.view.StackFragment;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public class MainPagerFactory implements PagerModuleFactory {
    @Override
    public PagerModuleItem moduleAtIndex(int i, final PagerModule pagerModule) {
        PagerModuleItem item = null;
        switch (i) {
            case 0:
                QuizletStackModuleFactory factory = new QuizletStackModuleFactory();
                StackFragment view = new StackFragment();

                StackPresenter stackPresenter = new StackPresenter();
                stackPresenter.setFactory(factory);
                stackPresenter.setView(view);
                setStackListener(pagerModule, stackPresenter);
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
            setStackListener(pagerModule, view.getPresenter());

            item = view.getPresenter();
        }

        return item;
    }
}
