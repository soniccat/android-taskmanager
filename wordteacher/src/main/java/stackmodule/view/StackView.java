package stackmodule.view;

import stackmodule.StackModuleItemView;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public interface StackView {
    void pushView(StackModuleItemView view, Callback callback);
    void popView(Callback callback);

    interface Callback {
        void finished();
    }
}
