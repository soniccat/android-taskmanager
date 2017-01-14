package com.example.alexeyglushkov.wordteacher.tools;

import com.example.alexeyglushkov.wordteacher.main.Preferences;

/**
 * Created by alexeyglushkov on 14.08.16.
 */
public interface Sortable {
    void setSortOrder(Preferences.SortOrder sortOrder);
    Preferences.SortOrder getSortOrder();
}
