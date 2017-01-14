package com.example.alexeyglushkov.wordteacher.listmodule.view;

import android.view.View;

/**
 * Created by alexeyglushkov on 04.01.17.
 */

public class SimpleListFragmentListenerAdapter<T> implements SimpleListFragment.Listener<T> {
    private ListenerProvider<T> provider;

    public SimpleListFragmentListenerAdapter(ListenerProvider<T> listenerProvider) {
        provider = listenerProvider;
    }

    public SimpleListFragmentListenerAdapter() {
    }

    @Override
    public void onRowClicked(T data) {
        if (provider != null && provider.getListener() != null) {
            provider.getListener().onRowClicked(data);
        }
    }

    @Override
    public void onRowMenuClicked(T data, View view) {
        if (provider != null && provider.getListener() != null) {
            provider.getListener().onRowMenuClicked(data, view);
        }
    }

    @Override
    public void onRowViewDeleted(T data) {
        if (provider != null && provider.getListener() != null) {
            provider.getListener().onRowViewDeleted(data);
        }
    }

    public interface ListenerProvider<T> {
        SimpleListFragment.Listener<T> getListener();
    }
}
