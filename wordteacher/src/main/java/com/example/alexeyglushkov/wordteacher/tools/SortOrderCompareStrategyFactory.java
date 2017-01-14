package com.example.alexeyglushkov.wordteacher.tools;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategy;
import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategyFactory;
import com.example.alexeyglushkov.wordteacher.main.Preferences;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public abstract class SortOrderCompareStrategyFactory<T> implements CompareStrategyFactory<T> {
    public CompareStrategy<T> createStrategy(final Preferences.SortOrder order) {
        return createSortOrderStrategy(order);
    }

    private SortOrderCompareStrategy<T> createSortOrderStrategy() {
        return createSortOrderStrategy(Preferences.SortOrder.BY_NAME);
    }

    @NonNull
    private SortOrderCompareStrategy<T> createSortOrderStrategy(final Preferences.SortOrder order) {
        return new SortOrderCompareStrategy<T>(order) {
            @Override
            public int compare(T lhs, T rhs) {
                return SortOrderCompareStrategyFactory.this.compare(lhs, rhs, getSortOrder());
            }
        };
    }

    @Override
    public CompareStrategy<T> createDefault() {
        return createSortOrderStrategy();
    }

    @Override
    public CompareStrategy<T> restore(Bundle bundle) {
        CompareStrategy<T> strategy = createSortOrderStrategy();
        strategy.restore(bundle);
        return strategy;
    }

    abstract protected int compare(T lhs, T rhs, Preferences.SortOrder sortOrder);
}
