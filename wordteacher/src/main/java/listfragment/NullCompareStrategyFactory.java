package listfragment;

import android.os.Bundle;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public class NullCompareStrategyFactory implements CompareStrategyFactory {
    @Override
    public CompareStrategy restore(Bundle bundle) {
        return null;
    }
}
