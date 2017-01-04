package listmodule;

import android.os.Bundle;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public class NullCompareStrategyFactory<T> implements CompareStrategyFactory<T> {
    @Override
    public CompareStrategy<T> createDefault() {
        return null;
    }

    @Override
    public CompareStrategy<T> restore(Bundle bundle) {
        return null;
    }
}
