package com.example.alexeyglushkov.wordteacher.listmodule;

import android.os.Bundle;
import android.os.Parcelable;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public interface CompareStrategy<T> extends Parcelable {
    int compare(T lhs, T rhs);

    boolean isInversed();
    void inverse();
}
