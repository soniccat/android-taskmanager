package com.example.alexeyglushkov.wordteacher.sessionresultmodule;

import android.os.Bundle;

import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategyFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProviderFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.presenter.BaseListPresenter;
import com.example.alexeyglushkov.wordteacher.model.LearnSession;
import com.example.alexeyglushkov.wordteacher.model.SessionCardResult;

/**
 * Created by alexeyglushkov on 15.01.17.
 */

public class SessionResultPresenterImp extends BaseListPresenter<SessionCardResult> implements SessionResultPresenter {
    private LearnSession session;

    @Override
    public void onCreated(Bundle savedInstanceState, Bundle extras) {
        super.onCreated(savedInstanceState, extras);

        if (session == null && savedInstanceState == null && extras != null) {
            session = extras.getParcelable(SessionResultPresenter.EXTERNAL_SESSION);
        }
    }

    @Override
    public LearnSession getLearnSession() {
        return session;
    }

    @Override
    protected StorableListProviderFactory<SessionCardResult> createProviderFactory() {
        return new SessionResultListProviderFactory();
    }

    @Override
    protected CompareStrategyFactory<SessionCardResult> createCompareStrategyFactory() {
        return new SessionResultCompareStrategyFactory();
    }
}
