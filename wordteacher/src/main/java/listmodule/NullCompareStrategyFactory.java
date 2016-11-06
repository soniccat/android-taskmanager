package listmodule;

import android.os.Bundle;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public class NullCompareStrategyFactory<T> implements CompareStrategyFactory<T> {
    @Override
    public CompareStrategy restore(Bundle bundle) {
        return null;
    }
}
