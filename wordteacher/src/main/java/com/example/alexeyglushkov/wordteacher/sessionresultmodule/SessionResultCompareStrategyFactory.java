package com.example.alexeyglushkov.wordteacher.sessionresultmodule;

import android.os.Bundle;

import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategy;
import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategyFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.SimpleCompareStrategy;
import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.wordteacher.model.SessionCardResult;
import com.example.alexeyglushkov.wordteacher.tools.SortOrderCompareStrategyFactory;

/**
 * Created by alexeyglushkov on 15.01.17.
 */

public class SessionResultCompareStrategyFactory implements CompareStrategyFactory<SessionCardResult> {
    @Override
    public CompareStrategy<SessionCardResult> createDefault() {
        return new SimpleCompareStrategy<SessionCardResult>() {
            @Override
            public int compare(SessionCardResult lhs, SessionCardResult rhs) {
                return SessionResultCompareStrategyFactory.compare(lhs.isRight, rhs.isRight);
            }
        };
    }

    @Override
    public CompareStrategy<SessionCardResult> restore(Bundle bundle) {
        return createDefault();
    }

    public static int compare(boolean lhs, boolean rhs) {
        return lhs == rhs ? 0 : lhs ? 1 : -1;
    }
}
