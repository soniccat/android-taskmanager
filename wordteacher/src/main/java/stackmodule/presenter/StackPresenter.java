package stackmodule.presenter;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import pagermodule.PagerModuleItem;
import pagermodule.PagerModuleItemView;
import stackmodule.StackModuleFactory;
import stackmodule.StackModuleItem;
import stackmodule.StackModuleListener;
import stackmodule.view.StackView;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public class StackPresenter implements StackPresenterInterface, PagerModuleItem {
    private StackModuleFactory factory;
    private StackModuleListener listener;

    private StackView view;
    private List<StackModuleItem> items = new ArrayList<>();

    //// Creation

    @Override
    public void onViewCreated(Bundle savedInstanceState) {
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (items.size() == 0) {
            initialize();
        }
    }

    public void initialize() {
        StackModuleItem module = factory.rootModule(this);
        push(module, null);
    }

    //// Events

    public void onBackStackChanged() {
        if (listener != null) {
            listener.onBackStackChanged();
        }
    }

    //// Action

    @Override
    public void push(Object obj, StackView.Callback callback) {
        StackModuleItem item = factory.moduleFromObject(obj, this);
        push(item, callback);
    }

    private void push(StackModuleItem item, StackView.Callback callback) {
        items.add(item);
        view.pushView(item.getView(), callback);
    }

    @Override
    public void pop(StackView.Callback callback) {
        items.remove(items.size()-1);
        view.popView(callback);
    }

    //// Interfaces

    // PagerModuleItem

    @Override
    public PagerModuleItemView getView() {
        return view;
    }

    //// Setters

    public void setView(StackView view) {
        this.view = view;
    }

    @Override
    public void setListener(StackModuleListener listener) {
        this.listener = listener;
    }

    @Override
    public void setFactory(StackModuleFactory factory) {
        this.factory = factory;
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
