package stackfragment.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.wordteacher.R;

import java.util.List;

/**
 * Created by alexeyglushkov on 03.05.16.
 */
public class StackFragment extends Fragment implements FragmentManager.OnBackStackChangedListener {

    protected Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private boolean isReadyToAddFragment() {
        return getActivity() != null && getView() != null;
    }

    protected void addFragment(Fragment fragment, final TransactionCallback callback) {
        boolean needSaveState = getAttachedFragment() != null;

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (needSaveState) {
            getChildFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    getChildFragmentManager().removeOnBackStackChangedListener(this);
                    if (callback != null) {
                        callback.onFinished();
                    }
                    StackFragment.this.onBackStackChanged();
                }
            });
            transaction.addToBackStack("currentState");
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }

        transaction.replace(R.id.container, fragment).commitAllowingStateLoss();

        if (!needSaveState) {
            getChildFragmentManager().executePendingTransactions();
            if (callback != null) {
                callback.onFinished();
                StackFragment.this.onBackStackChanged();
            }
        }
    }

    public void popFragment(final TransactionCallback callback) {
        getChildFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                getChildFragmentManager().removeOnBackStackChangedListener(this);
                if (callback != null) {
                    callback.onFinished();
                }

                StackFragment.this.onBackStackChanged();
            }
        });

        getChildFragmentManager().popBackStack();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getChildFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onBackStackChanged() {
        listener.onBackStackChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getChildFragmentManager().removeOnBackStackChangedListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public Fragment getTopFragment() {
        // calling findFragmentById when view is null is error prone
        int size = getFragmentCount();
        return size > 0 ? getFragment(size - 1) : null;
    }

    public int getFragmentCount() {
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        int size = 0;
        if (fragments != null) {
            size = fragments.size();

            // looks like a bullshit
            int lastLiveIndex = -1;
            for (int i=0; i<size; ++i) {
                if (fragments.get(i) != null) {
                    lastLiveIndex = i;
                } else {
                    break;
                }
            }

            size = lastLiveIndex + 1;
        }

        return fragments != null ? size : 0;
    }

    public int getBackStackSize() {
        // calling findFragmentById when view is null is error prone
        return isReadyToAddFragment() ? getChildFragmentManager().getBackStackEntryCount() : 0;
    }

    private Fragment getAttachedFragment() {
        return isReadyToAddFragment() ? getChildFragmentManager().findFragmentById(R.id.container) : null;
    }

    protected Fragment getFragment(int index) {
        List<Fragment> list = getChildFragmentManager().getFragments();
        return list != null && index < list.size() ? list.get(index) : null;
    }

    public interface Listener {
        void onBackStackChanged();
    }

    public interface TransactionCallback {
        void onFinished();
    }
}
