package listmodule;

import android.os.Bundle;

/**
 * Created by alexeyglushkov on 20.08.16.
 */
public interface StorableListProvider<T> extends ListProvider<T> {
    void store(Bundle bundle);

    // static boolean canRestore(Bundle bundle);
    void restore(Bundle bundle, Object context);
}
