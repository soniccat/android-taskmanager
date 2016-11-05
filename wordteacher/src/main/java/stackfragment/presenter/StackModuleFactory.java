package stackfragment.presenter;

import stackfragment.StackModuleInterface;
import stackfragment.StackModuleItem;

/**
 * Created by alexeyglushkov on 04.11.16.
 */

public interface StackModuleFactory {
    Object rootObject();
    StackModuleItem moduleFromObject(Object object, StackModuleInterface stackModule);
    StackModuleItem restoreModule(Object object, StackModuleInterface stackModule);
}
