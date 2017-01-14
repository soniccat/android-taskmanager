package com.example.alexeyglushkov.wordteacher.listmodule;

import com.example.alexeyglushkov.wordteacher.main.Preferences;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public interface ListModuleInterface {
    void reload();
    void setSortOrder(Preferences.SortOrder order);
    void delete(Object data);
}
