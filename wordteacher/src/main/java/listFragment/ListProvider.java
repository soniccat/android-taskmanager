package listfragment;

import java.util.List;

/**
 * Created by alexeyglushkov on 20.08.16.
 */
public interface ListProvider<T> {
    List<T> getList();
}