package stackmodule.view;

import pagermodule.PagerModuleItemView;
import stackmodule.StackModuleItemView;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public interface StackView extends PagerModuleItemView {
    void pushView(StackModuleItemView view, Callback callback);
    void popView(Callback callback);

    interface Callback {
        void finished();
    }
}
