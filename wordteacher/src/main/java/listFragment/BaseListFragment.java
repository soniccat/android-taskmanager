package listfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 23.07.16.
 */
public abstract class BaseListFragment<T> extends Fragment {
    protected RecyclerView recyclerView;
    protected BaseListAdaptor adapter;
    protected Listener listener;

    protected StorableListProviderFactory<T> factory;
    protected StorableListProvider<T> provider = new NullStorableListProvider<>();

    //// Creation, initialization, restoration

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = createAdapter();
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
    }

    // Init methods

    private void applyAdapter() {
        recyclerView.setAdapter(adapter);
    }

    protected void restoreProviderIfNeeded(Bundle savedInstanceState) {
        if (provider instanceof NullStorableListProvider) {
            provider = factory.restore(savedInstanceState);
        }
    }

    // Actions

    public void reload() {
        setAdapterItems(getSortedItems(getItems()));
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

    protected List<T> getSortedItems(List<T> inItems) {
        return inItems;
    }

    //// Creation Methods

    protected abstract BaseListAdaptor createAdapter();

    //// Setters

    public void setListener(Listener listener) {
        this.listener = listener;
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
