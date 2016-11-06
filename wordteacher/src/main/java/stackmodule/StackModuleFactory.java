package stackmodule;

/**
 * Created by alexeyglushkov on 04.11.16.
 */

public interface StackModuleFactory {
    Object rootObject();
    StackModuleItem moduleFromObject(Object object, StackModule stackModule);
    StackModuleItem restoreModule(Object object, StackModule stackModule);
}
