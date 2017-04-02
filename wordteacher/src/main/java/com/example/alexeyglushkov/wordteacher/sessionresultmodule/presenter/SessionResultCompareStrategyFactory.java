package com.example.alexeyglushkov.wordteacher.sessionresultmodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategy;
import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategyFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.SimpleCompareStrategy;
import com.example.alexeyglushkov.wordteacher.sessionresultmodule.view.SessionResultAdapterView;

/**
 * Created by alexeyglushkov on 15.01.17.
 */

public class SessionResultCompareStrategyFactory implements CompareStrategyFactory<SessionResultAdapterView> {
    @Override
    public CompareStrategy<SessionResultAdapterView> createDefault() {
        return new SimpleCompareStrategy<SessionResultAdapterView>() {
            @Override
            public int compare(SessionResultAdapterView lhs, SessionResultAdapterView rhs) {
                return SessionResultCompareStrategyFactory.compare(lhs.result.isRight, rhs.result.isRight);
            }
        };
    }

    @Override
    public CompareStrategy<SessionResultAdapterView> restore(Bundle bundle) {
        return createDefault();
    }

    public static int compare(boolean lhs, boolean rhs) {
        return lhs == rhs ? 0 : lhs ? 1 : -1;
    }
}
