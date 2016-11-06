package pagermodule;

import stackmodule.StackModule;
import stackmodule.StackModuleItem;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public interface PagerModuleFactory {
    PagerModuleItem moduleAtIndex(int i, PagerModule stackModule);
    PagerModuleItem restoreModule(int i, PagerModule stackModule);
}
