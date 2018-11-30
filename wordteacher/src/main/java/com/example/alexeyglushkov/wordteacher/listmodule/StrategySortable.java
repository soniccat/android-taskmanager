package com.example.alexeyglushkov.wordteacher.listmodule;

import androidx.annotation.Nullable;

public interface StrategySortable<T> {
    void setCompareStrategy(@Nullable CompareStrategy<T> compareStrategy);
}
