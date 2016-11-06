package stackmodule.presenter;

import android.os.Bundle;

import stackmodule.StackModule;
import stackmodule.StackModuleFactory;
import stackmodule.StackModuleListener;

/**
 * Created by alexeyglushkov on 04.11.16.
 */

public interface StackPresenterInterface extends StackModule {
    void onBackStackChanged();
    void onViewCreated(Bundle savedInstanceState);
    void onViewStateRestored(Bundle savedInstanceState);

    void setListener(StackModuleListener listener);
    void setFactory(StackModuleFactory factory);
}
