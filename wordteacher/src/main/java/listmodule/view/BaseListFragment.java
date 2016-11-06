package listmodule.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;
import java.util.List;

import listmodule.presenter.ListPresenterInterface;

/**
 * Created by alexeyglushkov on 23.07.16.
 */
public abstract class BaseListFragment<T> extends Fragment implements ListViewInterface<T> {
    private ListPresenterInterface eventHandler;

    protected RecyclerView recyclerView;
    protected View loader;

    protected BaseListAdaptor adapter;
    protected Listener<T> listener;

    //// Creation, initialization, restoration

    protected void initialize() {
        eventHandler.initialize();
        adapter = createAdapter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventHandler.onViewCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        loader = view.findViewById(R.id.loader);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        applyAdapter();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        eventHandler.store(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        eventHandler.onViewStateRestored(savedInstanceState);
    }

    private void applyAdapter() {
        recyclerView.setAdapter(adapter);
    }

    //// Events

    @Override
    public void onResume() {
        super.onResume();
        eventHandler.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        eventHandler.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        eventHandler.onDestroyView();

        recyclerView = null;

        adapter.cleanup();
        adapter = null;
        listener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventHandler.onDestroy();
    }

    //// Actions

    public void reload(List<T> items) {
        if (getView() != null) {
            setAdapterItems(items);
        }
    }

    public void updateRow(int index) {
        if (index < adapter.items.size()) {
            adapter.notifyItemRangeChanged(index, 1);
        }
    }

    public void updateRows() {
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
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

    // Update UI

    public void showLoading() {
        loader.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        loader.setVisibility(View.INVISIBLE);
    }

    //// Creation Methods

    protected abstract BaseListAdaptor createAdapter();

    //// Setters

    public void setListener(Listener<T> listener) {
        this.listener = listener;
    }

    // UI Setters

    protected void setAdapterItems(List<T> inItems) {
        List<T> courses = new ArrayList<>(inItems);
        adapter.setItems(courses);
    }

    //// Getters

    public Listener<T> getListener() {
        return listener;
    }

    //// UI Getters

    private View getDataView(int index) {
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(index);
        View view = holder.itemView;
        return view;
    }

    //// Inner Interfaces

    public interface Listener<T> {
        void onRowClicked(T data);
        void onRowMenuClicked(T data, View view);
        void onRowViewDeleted(T data);
    }
}
