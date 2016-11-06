package stackmodule.presenter;

import stackmodule.StackModule;
import stackmodule.StackModuleFactory;
import stackmodule.StackModuleListener;

/**
 * Created by alexeyglushkov on 04.11.16.
 */

public interface StackPresenterInterface extends StackModule {
    void setListener(StackModuleListener listener);
    void setFactory(StackModuleFactory factory);
}
