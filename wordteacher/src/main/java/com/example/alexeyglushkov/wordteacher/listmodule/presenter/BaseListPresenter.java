package com.example.alexeyglushkov.wordteacher.listmodule.presenter;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.aglushkov.repository.livedata.Resource;
import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategy;
import com.example.alexeyglushkov.wordteacher.listmodule.EmptyStorableListLiveDataProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.ResourceListLiveDataProviderImp;
import com.example.alexeyglushkov.wordteacher.listmodule.StrategySortable;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableResourceListLiveDataProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.view.ListViewInterface;
import com.example.alexeyglushkov.wordteacher.main.Preferences;

import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.PagerModuleItemView;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.PagerModuleItemWithTitle;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.StackModuleItem;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.StackModuleItemView;
import com.example.alexeyglushkov.wordteacher.tools.Sortable;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public abstract class BaseListPresenter<T>
        implements ListPresenterInterface,
        Observer<Resource<List<T>>>,
        StackModuleItem,
        PagerModuleItemWithTitle {
    protected final EmptyStorableListLiveDataProvider<T> EMPTY_LIST_DATA_PROVIDER = new EmptyStorableListLiveDataProvider<>();
    protected StorableResourceListLiveDataProvider<T> liveDataProvider = EMPTY_LIST_DATA_PROVIDER;

    //protected CompareStrategyFactory<T> compareStrategyFactory = new NullCompareStrategyFactory<>();
    protected @Nullable CompareStrategy<T> compareStrategy; // TODO: remove it somehow

    protected ListViewInterface<T> view;

    //// Initialization
    public BaseListPresenter() {
        initialize();
    }

    public void initialize() {
    }

    protected CompareStrategy<T> createSortStrategy(Preferences.SortOrder order) {
        return null;
    }

//    protected CompareStrategyFactory<T> createCompareStrategyFactory() {
//        return new NullCompareStrategyFactory<>();
//    }

    // TODO: make abstract
    protected StorableResourceListLiveDataProvider<T> createLiveDataProvider(Bundle bundle) {
        return null;
    }

    //// Events

    @Override
    public void onCreated(Bundle state, Bundle extras) {
        if (state != null || liveDataProvider instanceof EmptyStorableListLiveDataProvider) {
            if (this.liveDataProvider == EMPTY_LIST_DATA_PROVIDER) { //TODO: remove the condition after getting rid of a deprecated provider
                this.liveDataProvider = createLiveDataProvider(state);
            }

            if (state == null && compareStrategy != null && this.liveDataProvider instanceof StrategySortable) {
                setCompareStrategy(compareStrategy);
            }
        }
    }

    @Override
    public void onViewCreated(ListViewInterface view, Bundle state) {
        setView(view);
        this.liveDataProvider.getListLiveData().observe(this.view, this);
    }

    @Override
    public void onViewStateRestored(ListViewInterface view, @Nullable final Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroyView() {
    }

    public void onDestroy() {
        // clear here to support onSaveInstanceState

        if (liveDataProvider != null) {
            //liveDataProvider.getListLiveData().removeObserver(this);
            liveDataProvider = null;
        }

        //compareStrategyFactory = null;
        compareStrategy = null;
    }

    // Observer<T>

    @Override
    public void onChanged(Resource<List<T>> listResource) {
        updateUI(listResource);
    }

    //// Actions

    private void updateUI(Resource<List<T>> listResource) {
        boolean hasData = listResource.data != null && listResource.data.size() > 0;
        boolean isLoading = listResource.state == Resource.State.Loading;

        if (listResource.error != null) {
            view.hideLoading();
        } else if (isLoading) {
            view.showLoading();
        } else if (hasData) {
            view.hideLoading();
            view.reload(listResource.data);
        }
    }

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
        liveDataProvider.store(bundle);
    }

    //// Interfaces

    // PagerModuleItemWithTitle

    @Override
    public String getTitle() {
        return null;
    }

    // StackModuleItem

    @Override
    public Object getObject() {
        return null;
    }

    @Override
    public StackModuleItemView getStackModuleItemView() {
        return view;
    }

    // PagerModuleItem

    @Override
    public PagerModuleItemView getPagerModuleItemView() {
        return view;
    }

    // ListModuleInterface

    // TODO: remove
    @Override
    public void reload() {
        //view.reload(getItems());
    }

    @Override
    public void setSortOrder(Preferences.SortOrder order) {
        setCompareStrategy(createSortStrategy(order));
    }

    // TODO: remove
    @Override
    public void delete(Object data) {
//        int index = getItems().indexOf(data);
//        if (index != -1) {
//            view.deleteRow(index);
//        }
    }

    //// Setter

    public void setCompareStrategy(CompareStrategy<T> compareStrategy) {
        if (liveDataProvider instanceof StrategySortable) {
            ((StrategySortable<T>) liveDataProvider).setCompareStrategy(compareStrategy);
            this.compareStrategy = null;
        } else {
            // store here to setup after liveDataProvider initialization
            this.compareStrategy = compareStrategy;
        }
    }

    public void setView(ListViewInterface view) {
        this.view = view;
    }

    //// Getters

    @Nullable
    public Preferences.SortOrder getSortOrder() {
        Preferences.SortOrder order = null;
        if (liveDataProvider instanceof ResourceListLiveDataProviderImp) {
            CompareStrategy<T> strategy = ((ResourceListLiveDataProviderImp<T>) liveDataProvider).getCompareStrategy();

            if (strategy instanceof Sortable) {
                order = ((Sortable) strategy).getSortOrder();
            }
        }

        return order;
    }

//    protected List<T> getProviderItems() {
//        return provider.getList();
//    }
//
//    protected List<T> getSortedItems(List<T> inItems) {
//        List<T> result = null;
//        if (compareStrategy != null) {
//            result = new ArrayList<>(inItems);
//            sortItems(result, compareStrategy);
//
//        } else {
//            result = inItems;
//        }
//
//        return result;
//    }

//    public List<T> getItems() {
//        return getSortedItems(getProviderItems());
//    }
}
