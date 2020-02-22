package com.example.alexeyglushkov.wordteacher.tools;

import android.os.Bundle;
import android.os.Parcel;

import com.example.alexeyglushkov.wordteacher.listmodule.SimpleCompareStrategy;
import com.example.alexeyglushkov.wordteacher.main.Preferences;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public class SortOrderCompareStrategy<T> extends SimpleCompareStrategy<T> implements Sortable {
    public static final Creator<SortOrderCompareStrategy> CREATOR = new Creator<SortOrderCompareStrategy>() {
        @Override
        public SortOrderCompareStrategy createFromParcel(Parcel source) {
            return new SortOrderCompareStrategy(source);
        }

        @Override
        public SortOrderCompareStrategy[] newArray(int size) {
            return new SortOrderCompareStrategy[size];
        }
    };

    private final static String SORT_KEY = "SORT_KEY";

    private Preferences.SortOrder sortOrder = Preferences.SortOrder.BY_NAME;

    public SortOrderCompareStrategy(Preferences.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public SortOrderCompareStrategy(Parcel in) {
        super(in);
        this.sortOrder = Preferences.SortOrder.values()[in.readInt()];
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(sortOrder.ordinal());
    }

    public Preferences.SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Preferences.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }
}
