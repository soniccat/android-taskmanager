package listmodule;

import android.os.Bundle;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public interface CompareStrategyFactory<T> {
    CompareStrategy<T> createDefault();
    CompareStrategy<T> restore(Bundle bundle);
}
