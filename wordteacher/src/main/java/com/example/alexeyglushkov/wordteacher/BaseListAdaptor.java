package com.example.alexeyglushkov.wordteacher;

import android.support.v7.widget.RecyclerView;

/**
 * Created by alexeyglushkov on 23.07.16.
 */
public abstract class BaseListAdaptor<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {
    abstract int getDataIndex(T data);
    abstract void deleteDataAtIndex(int index);
}
