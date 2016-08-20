package listfragment;

import android.os.Bundle;

import java.util.List;

/**
 * Created by alexeyglushkov on 20.08.16.
 */
public interface StorableListProviderFactory<T> {
    StorableListProvider<T> createFromList(List<T> list);
    StorableListProvider<T> restore(Bundle bundle);
    StorableListProvider<T> createDefault();
}
