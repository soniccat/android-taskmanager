package stackfragment.view;

import android.support.v4.app.Fragment;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public interface StackFragmentView {
    void addFragment(Fragment fragment, final StackFragment.TransactionCallback callback);
    void popFragment(final StackFragment.TransactionCallback callback);
}
