package listfragment;

import android.support.v7.widget.RecyclerView;

/**
 * Created by alexeyglushkov on 23.07.16.
 */
public abstract class BaseListAdaptor<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {
    public abstract int getDataIndex(T data);
    public abstract void deleteDataAtIndex(int index);
}
