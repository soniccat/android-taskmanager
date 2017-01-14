package com.example.alexeyglushkov.wordteacher.listmodule;

import android.os.Bundle;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public interface CompareStrategy<T> {
    int compare(T lhs, T rhs);

    boolean isInversed();
    void inverse();

    void store(Bundle bundle);
    void restore(Bundle bundle);
}
