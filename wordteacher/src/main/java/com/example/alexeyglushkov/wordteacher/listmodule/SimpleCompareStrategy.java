package com.example.alexeyglushkov.wordteacher.listmodule;

import android.os.Bundle;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public abstract class SimpleCompareStrategy<T> implements CompareStrategy<T> {
    private boolean isInversed;

    @Override
    public boolean isInversed() {
        return isInversed;
    }

    @Override
    public void inverse() {
        isInversed = !isInversed;
    }

    @Override
    public void store(Bundle bundle) {
    }

    @Override
    public void restore(Bundle bundle) {
    }
}
