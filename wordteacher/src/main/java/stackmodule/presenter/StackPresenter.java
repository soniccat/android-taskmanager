package stackmodule.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import pagermodule.PagerModuleItem;
import pagermodule.PagerModuleItemView;
import pagermodule.PagerModuleItemWithTitle;
import stackmodule.StackModuleFactory;
import stackmodule.StackModuleItem;
import stackmodule.StackModuleListener;
import stackmodule.view.StackView;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public class StackPresenter implements StackPresenterInterface, PagerModuleItem, PagerModuleItemWithTitle {
    private StackModuleFactory factory;
    private StackModuleListener listener;

    private StackView view;
    private List<StackModuleItem> items = new ArrayList<>();

    //// Creation

    @Override
    public void onViewCreated(Bundle savedInstanceState) {
        if (factory == null && savedInstanceState != null) {
            try {
                Class factoryClass = Class.forName(savedInstanceState.getString("factoryClass"));
                if (factoryClass != null) {
                    factory = (StackModuleFactory) factoryClass.newInstance();
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("factoryClass", factory.getClass().getName());
    }

    @Override
    public void onViewStateRestored(StackView view, List<Object> childs, Bundle savedInstanceState) {
        setView(view);
        for (int i=0; i<childs.size(); ++i) {
            StackModuleItem item = factory.restoreModule(childs.get(i), this);
            items.add(item);
        }

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

    // PagerModuleItemWithTitle

    @Override
    public String getTitle() {
        StackModuleItem item = getTopModule();
        String result = null;
        if (item instanceof PagerModuleItemWithTitle) {
            result = ((PagerModuleItemWithTitle)item).getTitle();
        }

        return result;
    }


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

    private @Nullable StackModuleItem getTopModule() {
        int size = getSize();
        return size > 0 ? getModuleAtIndex(size - 1) : null;
    }
}
