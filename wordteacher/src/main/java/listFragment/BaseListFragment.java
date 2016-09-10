package listfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by alexeyglushkov on 23.07.16.
 */
public abstract class BaseListFragment<T> extends Fragment {
    protected RecyclerView recyclerView;
    protected BaseListAdaptor adapter;
    protected Listener listener;

    protected StorableListProviderFactory<T> providerFactory;
    protected StorableListProvider<T> provider = new NullStorableListProvider<>();

    protected CompareStrategyFactory<T> compareStrategyFactory = new NullCompareStrategyFactory<>();
    protected CompareStrategy<T> compareStrategy;

    //// Creation, initialization, restoration

    private void initializeIfNeeded() {
        if (providerFactory == null) {
            initialize();
        }
    }

    protected void initialize() {
        providerFactory = createProviderFactory();
        compareStrategyFactory = createCompareStrategyFactory();
        adapter = createAdapter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeIfNeeded();

        if (savedInstanceState != null) {
            //provider = providerFactory.restore(savedInstanceState);
            compareStrategy = compareStrategyFactory.restore(savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        applyAdapter();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        provider.store(outState);
        storeCompareStrategyIfNeeded(outState);
    }

    // Init methods

    protected CompareStrategyFactory<T> createCompareStrategyFactory() {
        return new NullCompareStrategyFactory<>();
    }

    private void applyAdapter() {
        recyclerView.setAdapter(adapter);
    }

    private void storeCompareStrategyIfNeeded(Bundle outState) {
        if (compareStrategy != null) {
            compareStrategy.store(outState);
        }
    }

    // Actions

    public void reload() {
        if (getView() != null) {
            setAdapterItems(getSortedItems(getItems()));
        }
    }

    public void deleteView(T data) {
        int index = adapter.getDataIndex(data);
        if (index != -1) {
            View view = getDataView(index);
            if (view != null) {
                int position = recyclerView.getChildLayoutPosition(view);
                adapter.deleteDataAtIndex(index);
                adapter.notifyItemRemoved(position);
            }
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

    //// Creation Methods

    protected abstract BaseListAdaptor createAdapter();
    protected abstract StorableListProviderFactory<T> createProviderFactory();

    //// Setters

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setCompareStrategy(CompareStrategy<T> compareStrategy) {
        this.compareStrategy = compareStrategy;
        reload();
    }

    // UI Setters

    protected void setAdapterItems(List<T> inItems) {
        List<T> courses = new ArrayList<>(inItems);
        adapter.setItems(courses);
    }

    //// Getters

    public Listener getListener() {
        return listener;
    }

    // Data Getters

    protected List<T> getItems() {
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

    //// UI Getters

    private View getDataView(int index) {
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(index);
        View view = holder.itemView;
        return view;
    }

    // Statuses

    public boolean hasItems() {
        return getItems().size() > 0;
    }

    //// Inner Interfaces

    public interface Listener<T> {
        void onRowClicked(T data);
        void onRowMenuClicked(T data, View view);
        void onRowViewDeleted(T data);
    }

}
