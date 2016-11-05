package stackfragment.view;

import android.support.v4.app.Fragment;

import stackfragment.StackModuleItemView;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public interface StackFragmentView {
    void pushView(StackModuleItemView view, Callback callback);
    void popView(Callback callback);

    interface Callback {
        void finished();
    }
}
