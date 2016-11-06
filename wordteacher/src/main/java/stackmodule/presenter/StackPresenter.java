package stackmodule.presenter;

import java.util.ArrayList;
import java.util.List;

import stackmodule.StackModuleFactory;
import stackmodule.StackModule;
import stackmodule.StackModuleItem;
import stackmodule.StackModuleListener;
import stackmodule.view.StackView;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public class StackPresenter implements StackModule, StackPresenterInterface {
    private StackModuleFactory factory;
    private StackModuleListener listener;

    private StackView view;
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
    public void push(Object obj, StackView.Callback callback) {
        StackModuleItem item = factory.moduleFromObject(obj, this);
        items.add(item);

        view.pushView(item.getView(), callback);
    }

    @Override
    public void pop(StackView.Callback callback) {
        items.remove(items.size()-1);
        view.popView(callback);
    }

    //// Getters


    @Override
    public int getSize() {
        return items.size();
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
