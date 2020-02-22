package com.example.alexeyglushkov.wordteacher.sessionresultmodule.presenter;

import com.example.alexeyglushkov.wordteacher.listmodule.presenter.ListPresenterInterface;
import com.example.alexeyglushkov.wordteacher.model.LearnSession;
import com.example.alexeyglushkov.wordteacher.sessionresultmodule.SessionResultModule;

/**
 * Created by alexeyglushkov on 15.01.17.
 */

public interface SessionResultPresenter extends ListPresenterInterface, SessionResultModule {
    String EXTERNAL_SESSION = "session";
}
