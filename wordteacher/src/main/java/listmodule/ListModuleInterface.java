package listmodule;

import main.Preferences;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public interface ListModuleInterface {
    void reload();
    void setSortOrder(Preferences.SortOrder order);
    void delete(Object data);
}
