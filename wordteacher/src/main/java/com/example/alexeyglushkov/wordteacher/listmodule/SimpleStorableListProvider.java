package com.example.alexeyglushkov.wordteacher.listmodule;

import java.util.List;

/**
 * Created by alexeyglushkov on 20.08.16.
 */
public abstract class SimpleStorableListProvider<T> extends SimpleListProvider<T> implements StorableListProvider<T> {
    public SimpleStorableListProvider(List<T> items) {
        super(items);
    }
}
