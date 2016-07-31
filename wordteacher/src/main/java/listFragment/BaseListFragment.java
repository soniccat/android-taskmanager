package listfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.alexeyglushkov.wordteacher.R;

/**
 * Created by alexeyglushkov on 23.07.16.
 */
public abstract class BaseListFragment<T> extends Fragment {
    protected RecyclerView recyclerView;
    protected BaseListAdaptor adapter;
    protected Listener listener;

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

    private void applyAdapter() {
        recyclerView.setAdapter(adapter);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Listener getListener() {
        return listener;
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

    private View getDataView(int index) {
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(index);
        View view = holder.itemView;
        return view;
    }

    protected abstract BaseListAdaptor createAdapter();
    //protected abstract void restoreAdapter(Bundle savedInstanceState);

    public interface Listener<T> {
        void onRowClicked(T data);
        void onRowMenuClicked(T data, View view);
        void onRowViewDeleted(T data);
    }

}
