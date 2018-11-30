package com.example.alexeyglushkov.wordteacher.listmodule;

import android.os.Bundle;
import android.os.Parcel;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public class SimpleCompareStrategy<T> implements CompareStrategy<T> {
    private boolean isInversed;

    public SimpleCompareStrategy() {
    }

    protected SimpleCompareStrategy(Parcel in) {
        this.isInversed = in.readByte() != 0;
    }

    @Override
    public boolean isInversed() {
        return isInversed;
    }

    @Override
    public void inverse() {
        isInversed = !isInversed;
    }

    @Override
    public int compare(T lhs, T rhs) {
        return 0;
    }

    //// Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isInversed ? (byte) 1 : (byte) 0);
    }

    public static final Creator<SimpleCompareStrategy> CREATOR = new Creator<SimpleCompareStrategy>() {
        @Override
        public SimpleCompareStrategy createFromParcel(Parcel source) {
            return new SimpleCompareStrategy(source);
        }

        @Override
        public SimpleCompareStrategy[] newArray(int size) {
            return new SimpleCompareStrategy[size];
        }
    };
}
