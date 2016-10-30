package listfragment.listmodule.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import listfragment.CompareStrategy;
import listfragment.CompareStrategyFactory;
import listfragment.NullCompareStrategyFactory;
import listfragment.NullStorableListProvider;
import listfragment.StorableListProvider;
import listfragment.StorableListProviderFactory;
import listfragment.listmodule.ListModuleInterface;
import listfragment.listmodule.view.ListViewInterface;
import main.Preferences;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public abstract class BaseListPresenter<T> implements ListPresenterInterface, ListModuleInterface {
    protected StorableListProviderFactory<T> providerFactory;
    protected StorableListProvider<T> provider = new NullStorableListProvider<>();

    protected CompareStrategyFactory<T> compareStrategyFactory = new NullCompareStrategyFactory<>();
    protected CompareStrategy<T> compareStrategy;

    protected ListViewInterface view;

    //// Initialization

    public void initializeIfNeeded() {
        if (providerFactory == null) {
            initialize();
        }
    }

    public void initialize() {
        providerFactory = createProviderFactory();
        compareStrategyFactory = createCompareStrategyFactory();
    }

    protected CompareStrategyFactory<T> createCompareStrategyFactory() {
        return new NullCompareStrategyFactory<>();
    }

    protected abstract StorableListProviderFactory<T> createProviderFactory();

    //// Events

    public void onViewCreated(Bundle savedInstanceState) {
        initializeIfNeeded();

        if (savedInstanceState != null) {
            compareStrategy = compareStrategyFactory.restore(savedInstanceState);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
    }

    @Override
    public void onDestroyView() {
    }

    public void onDestroy() {
        // clear here to support onSaveInstanceState
        providerFactory = null;
        provider = null;

        compareStrategyFactory = null;
        compareStrategy = null;
    }

    //// Actions

    private void sortItems(List<T> inItems, final CompareStrategy<T> compareStrategy) {
        Collections.sort(inItems, new Comparator<T>() {
            @Override
            public int compare(T lhs, T rhs) {
                return compareStrategy.compare(lhs, rhs);
            }
        });
    }

    // Storing

    public void store(Bundle bundle) {
        provider.store(bundle);
        storeCompareStrategyIfNeeded(bundle);
    }

    private void storeCompareStrategyIfNeeded(Bundle outState) {
        if (compareStrategy != null) {
            compareStrategy.store(outState);
        }
    }

    //// Interfaces

    // ListModuleInterface

    @Override
    public void reload() {
        view.reload(getItems());
    }

    @Override
    public void setSortOrder(Preferences.SortOrder order) {
    }

    //// Setter

    public void setCompareStrategy(CompareStrategy<T> compareStrategy) {
        this.compareStrategy = compareStrategy;
        view.reload(getItems());
    }

    //// Getters

    protected List<T> getProviderItems() {
        return provider.getList();
    }

    protected List<T> getSortedItems(List<T> inItems) {
        List<T> result = null;
        if (compareStrategy != null) {
            result = new ArrayList<>(inItems);
            sortItems(result, compareStrategy);

        } else {
            result = inItems;
        }

        return result;
    }

    public List<T> getItems() {
        return getSortedItems(getProviderItems());
    }

    // Statuses

    public boolean hasItems() {
        return getItems().size() > 0;
    }
}
