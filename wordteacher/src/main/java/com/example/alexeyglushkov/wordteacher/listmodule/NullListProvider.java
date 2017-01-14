package com.example.alexeyglushkov.wordteacher.listmodule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 21.08.16.
 */
public class NullListProvider<T> implements ListProvider<T> {
    private List<T> emptyList = new ArrayList<>();

    @Override
    public List<T> getList() {
        return emptyList;
    }
}
