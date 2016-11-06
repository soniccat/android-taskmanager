package pagermodule.presenter;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import pagermodule.PagerModuleFactory;
import pagermodule.PagerModuleItem;
import pagermodule.PagerModuleItemView;
import pagermodule.PagerModuleListener;
import pagermodule.view.PagerView;
import stackmodule.StackModuleFactory;
import stackmodule.StackModuleItem;
import stackmodule.StackModuleListener;
import stackmodule.view.StackView;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public class StatePagerPresenter implements PagerPresenter {
    private PagerModuleFactory factory;
    private PagerModuleListener listener;

    private PagerView view;
    private int currentIndex;
    private int fillCount = 2;
    private SparseArray<PagerModuleItem> items = new SparseArray<>();

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
        int size = listener.getPageCount();
        updateItems(size);

        view.setItemCount(size);
    }

    private void updateItems(int size) {
        for (int i = 0; i < size; ++i) {
            int index = items.indexOfKey(i);
            if (needRemove(index)) {
                items.removeAt(i);

            } else {
                PagerModuleItem module = factory.moduleAtIndex(index, this);
                items.put(index, module);
            }
        }
    }

    private boolean needRemove(int i) {
        return i <= currentIndex - fillCount || i >= currentIndex + fillCount;
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

    @Override
    public PagerModuleItem getModuleAtIndex(int i) {
        return items.get(i);
    }

    @Override
    public PagerModuleItemView getViewAtIndex(int i) {
        return getModuleAtIndex(i).getView();
    }
}
