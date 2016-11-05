package stackfragment.presenter;

import java.util.ArrayList;
import java.util.List;

import stackfragment.StackModuleInterface;
import stackfragment.StackModuleItem;
import stackfragment.StackModuleListener;
import stackfragment.view.StackFragmentView;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public class StackPresenter implements StackModuleInterface, StackPresenterInterface {
    private StackModuleFactory factory;
    private StackModuleListener listener;

    private StackFragmentView view;
    private List<StackModuleItem> items = new ArrayList<>();

    @Override
    public void setListener(StackModuleListener listener) {
        this.listener = listener;
    }

    @Override
    public void setFactory(StackModuleFactory factory) {
        this.factory = factory;
    }

    @Override
    public void push(Object obj, StackFragmentView.Callback callback) {
        StackModuleItem item = factory.moduleFromObject(obj, this);
        items.add(item);

        view.pushView(item.getView(), callback);
    }

    @Override
    public void pop(StackFragmentView.Callback callback) {
        items.remove(items.size()-1);
        view.popView(callback);
    }

    @Override
    public Object getObjectAtIndex(int i) {
        return getModuleAtIndex(i).getObject();
    }

    @Override
    public StackModuleItem getModuleAtIndex(int i) {
        return items.get(i);
    }
}
