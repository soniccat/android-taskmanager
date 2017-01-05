package pagermodule.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import pagermodule.PagerModuleFactory;
import pagermodule.PagerModuleItem;
import pagermodule.PagerModuleItemView;
import pagermodule.PagerModuleItemWithTitle;
import pagermodule.PagerModuleListener;
import pagermodule.view.PagerView;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public class StatePagerPresenter implements PagerPresenter {
    private PagerModuleFactory factory;
    private PagerModuleListener listener;

    private PagerView view;
    private int currentIndex;
    private int size;
    private int fillCount = 2;
    private SparseArray<PagerModuleItem> items = new SparseArray<>();

    //// Creation and restoration

    @Override
    public void onViewCreated(Bundle savedInstanceState) {
        if (factory == null && savedInstanceState != null) {
            try {
                Class factoryClass = Class.forName(savedInstanceState.getString("factoryClass"));
                if (factoryClass != null) {
                    factory = (PagerModuleFactory) factoryClass.newInstance();
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
    public void onViewStateRestored(PagerView view, int currentIndex, SparseArray<Object> childs, Bundle savedInstanceState) {
        this.currentIndex = currentIndex;
        setView(view);
        for (int i=0; i<childs.size(); ++i) {
            int key = childs.keyAt(i);

            PagerModuleItem item = factory.restoreModule(key, childs.get(key), this);
            items.put(key, item);
        }
    }


    //// Events

    @Override
    public void onPageChanged(int i) {
        currentIndex = i;
        updateItems(items.size());
        listener.onCurrentPageChanged();
    }

    //// Actions

    @Override
    public void reload() {
        size = listener.getPageCount();
        updateItems(size);

        view.setItemCount(size);
    }

    private void updateItems(int size) {
        for (int i = 0; i < items.size(); ++i) {
            if (needRemove(i)) {
                items.remove(i);
            }
        }

        for (int i = currentIndex - (fillCount - 1); i < currentIndex + fillCount; ++i) {
            if (i < 0 || i >= size || items.get(i) != null) {
                continue;
            }

            loadModule(i);
        }
    }

    private PagerModuleItem loadModule(int i) {
        PagerModuleItem module = factory.moduleAtIndex(i, this);
        items.put(i, module);
        return module;
    }

    private boolean needRemove(int i) {
        return i <= currentIndex - fillCount || i >= currentIndex + fillCount;
    }

    public void updatePage(int i) {
        view.updateView(i);
    }

    //// Setter

    @Override
    public void setCurrentIndex(int i) {
        currentIndex = i;
        reload();
    }

    @Override
    public void setView(PagerView view) {
        this.view = view;
    }

    @Override
    public void setListener(PagerModuleListener listener) {
        this.listener = listener;
    }

    @Override
    public void setFactory(PagerModuleFactory factory) {
        this.factory = factory;
    }

    //// Getter

    @Override
    public int getCurrentIndex() {
        return currentIndex;
    }

    private PagerModuleItem requireModuleAtIndex(int i) {
        PagerModuleItem item = getModuleAtIndex(i);
        if (item == null) {
            item = loadModule(i);
        }

        return item;
    }

    @Override
    public PagerModuleItem getModuleAtIndex(int i) {
        return items.get(i);
    }

    @Override
    public PagerModuleItemView getViewAtIndex(int i) {
        return requireModuleAtIndex(i).getPagerModuleItemView();
    }

    @Override
    @Nullable public String getViewTitleAtIndex(int i) {
        PagerModuleItem item = getModuleAtIndex(i);
        String str = null;

        if (item instanceof PagerModuleItemWithTitle) {
            str = ((PagerModuleItemWithTitle)item).getTitle();
        }

        return str;
    }

    @Override
    public PagerView getView() {
        return view;
    }

    public PagerModuleFactory getFactory() {
        return factory;
    }
}
