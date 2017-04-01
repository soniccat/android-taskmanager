package com.example.alexeyglushkov.wordteacher.listmodule;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by alexeyglushkov on 20.08.16.
 */
public class SimpleListProvider<T> implements ListProvider<T> {
    protected List<T> items;

    public SimpleListProvider(List<T> items) {
        this.items = items;
    }

    public SimpleListProvider() {
        items = new ArrayList<>();
    }

    @Override
    public List<T> getList() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
