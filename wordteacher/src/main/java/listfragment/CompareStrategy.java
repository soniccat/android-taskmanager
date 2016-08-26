package listfragment;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public interface CompareStrategy<T> {
    int compare(T lhs, T rhs);

    boolean isInversed();
    void inverse();
}
