package listfragment;

import android.support.v7.widget.RecyclerView;

import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 23.07.16.
 */
public abstract class BaseListAdaptor<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {
    protected List<T> items = new ArrayList<>();

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> inItems) {
        items = new ArrayList<>();
        items.addAll(inItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public int getDataIndex(T data) {
        return items.indexOf(data);
    }

    public void deleteDataAtIndex(int index) {
        items.remove(index);
    }
}
