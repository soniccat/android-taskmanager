package listfragment;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public abstract class SimpleCompareStrategy<T> implements CompareStrategy<T> {
    private boolean isInversed;

    @Override
    public boolean isInversed() {
        return isInversed;
    }

    @Override
    public void inverse() {
        isInversed = !isInversed;
    }
}
