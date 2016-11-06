package stackmodule;

import stackmodule.view.StackView;

/**
 * Created by alexeyglushkov on 04.11.16.
 */

public interface StackModule {
    void push(Object obj, StackView.Callback callback);
    void pop(StackView.Callback callback);

    int getSize();
    Object getObjectAtIndex(int i);
    StackModuleItem getModuleAtIndex(int i);
}
