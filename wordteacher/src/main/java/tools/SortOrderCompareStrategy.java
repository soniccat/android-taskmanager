package tools;

import android.os.Bundle;

import listmodule.SimpleCompareStrategy;
import com.example.alexeyglushkov.wordteacher.main.Preferences;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public abstract class SortOrderCompareStrategy<T> extends SimpleCompareStrategy<T> {
    private final static String SORT_KEY = "SORT_KEY";

    private Preferences.SortOrder sortOrder = Preferences.SortOrder.BY_NAME;

    public SortOrderCompareStrategy(Preferences.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public void restore(Bundle bundle) {
        int intOrder = bundle.getInt(SORT_KEY);
        setSortOrder(Preferences.SortOrder.values()[intOrder]);
    }

    @Override
    public void store(Bundle bundle) {
        bundle.putInt(SORT_KEY, getSortOrder().ordinal());
    }

    public Preferences.SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Preferences.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }
}
