package com.example.alexeyglushkov.wordteacher;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by alexeyglushkov on 03.05.16.
 */
public class StackContainer extends Fragment {

    private Fragment pendingFragment;

    public void showFragment(Fragment fragment) {
        pendingFragment = fragment;

        if (getActivity() != null && getView() != null) {
            addFragment(fragment);
        }
    }

    private void addFragment(Fragment fragment) {
        boolean needSaveState = getAttachedFragment() != null;

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (needSaveState) {
            transaction.addToBackStack("currentState");
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }

        transaction.replace(R.id.container, fragment).commitAllowingStateLoss();

        if (pendingFragment == fragment) {
            pendingFragment = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addFragment(pendingFragment);
    }

    public Fragment getFragment() {
        return pendingFragment != null ? pendingFragment : getAttachedFragment();
    }

    public int getBackStackSize() {
        return getChildFragmentManager().getBackStackEntryCount();
    }

    private Fragment getAttachedFragment() {
        return getChildFragmentManager().findFragmentById(R.id.container);
    }
}
