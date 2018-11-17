package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import androidx.annotation.NonNull;

import com.example.alexeyglushkov.tools.HandlerTools;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by alexeyglushkov on 28.05.17.
 */

public class SafeList<T> extends ArrayList<T> {
    private Handler safeHandler;
    private ArrayList<T> originalList;
    private ArrayList<T> safeList = new ArrayList<>();

    public SafeList(ArrayList<T> originalList, Handler handler) {
        this.originalList = originalList;
        this.safeHandler = handler;
        fillSafeList();
    }

    // Overrides

    @Override
    public boolean add(T t) {
        boolean result = originalList.add(t);
        fillSafeList();
        return result;
    }

    @Override
    public T remove(int index) {
        T result = originalList.remove(index);
        fillSafeList();
        return result;
    }

    @Override
    public boolean remove(Object o) {
        boolean result = originalList.remove(o);
        fillSafeList();
        return result;
    }

    @Override
    public int indexOf(Object o) {
        return originalList.indexOf(o);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return originalList.iterator();
    }

    @Override
    public int size() {
        return originalList.size();
    }

    // Sync methods

    private void fillSafeList() {
        HandlerTools.runOnHandlerThread(safeHandler, new Runnable() {
            @Override
            public void run() {
                fillSafeListOnThread((ArrayList<T>)originalList.clone());
            }
        });
    }

    private void fillSafeListOnThread(ArrayList<T> list) {
        safeList.clear();
        safeList.addAll(list);
    }

    // Getters

    public Handler getHandler() {
        return safeHandler;
    }

    public ArrayList<T> getOriginalList() {
        return originalList;
    }

    public ArrayList<T> getSafeList() {
        return safeList;
    }
}
