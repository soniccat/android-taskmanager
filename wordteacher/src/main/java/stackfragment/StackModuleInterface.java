package stackfragment;

import android.widget.StackView;

import stackfragment.presenter.StackModuleFactory;
import stackfragment.view.StackFragmentView;

/**
 * Created by alexeyglushkov on 04.11.16.
 */

public interface StackModuleInterface {
    void setListener(StackModuleListener listener);
    void setFactory(StackModuleFactory factory);
    void push(Object obj, StackFragmentView.Callback callback);
    void pop(StackFragmentView.Callback callback);

    Object getObjectAtIndex(int i);
    StackModuleItem getModuleAtIndex(int i);
}
