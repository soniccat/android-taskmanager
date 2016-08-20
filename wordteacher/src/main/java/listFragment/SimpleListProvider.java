package listfragment;
import java.util.List;


/**
 * Created by alexeyglushkov on 20.08.16.
 */
public class SimpleListProvider<T> implements ListProvider<T> {
    private List<T> items;

    public SimpleListProvider(List<T> items) {
        this.items = items;
    }

    @Override
    public List<T> getList() {
        return items;
    }
}
