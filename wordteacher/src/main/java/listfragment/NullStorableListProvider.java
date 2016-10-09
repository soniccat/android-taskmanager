package listfragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alexeyglushkov on 21.08.16.
 */
public class NullStorableListProvider<T> implements StorableListProvider<T> {

    @Override
    public void store(Bundle bundle) {

    }

    @Override
    public void restore(Bundle bundle, Object context) {

    }

    @Override
    public List<T> getList() {
        return Collections.emptyList();
    }
}
