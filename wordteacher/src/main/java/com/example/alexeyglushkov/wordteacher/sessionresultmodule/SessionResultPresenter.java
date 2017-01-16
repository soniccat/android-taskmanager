package com.example.alexeyglushkov.wordteacher.sessionresultmodule;

import com.example.alexeyglushkov.wordteacher.listmodule.presenter.ListPresenterInterface;
import com.example.alexeyglushkov.wordteacher.model.LearnSession;

/**
 * Created by alexeyglushkov on 15.01.17.
 */

public interface SessionResultPresenter extends ListPresenterInterface, SessionResultModule {
    LearnSession getLearnSession();
}
