package com.example.alexeyglushkov.wordteacher.sessionresultmodule;

import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategyFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProviderFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.presenter.BaseListPresenter;
import com.example.alexeyglushkov.wordteacher.model.LearnSession;
import com.example.alexeyglushkov.wordteacher.model.SessionCardResult;

/**
 * Created by alexeyglushkov on 15.01.17.
 */

public class SessionResultPresenterImp extends BaseListPresenter<SessionCardResult> implements SessionResultModule {
    private LearnSession session;

    

    @Override
    protected StorableListProviderFactory<SessionCardResult> createProviderFactory() {
        return null;
    }

    @Override
    protected CompareStrategyFactory<SessionCardResult> createCompareStrategyFactory() {
        return new SessionResultCompareStrategyFactory();
    }
}
