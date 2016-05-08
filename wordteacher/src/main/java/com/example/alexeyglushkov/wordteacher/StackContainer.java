package com.example.alexeyglushkov.wordteacher;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by alexeyglushkov on 03.05.16.
 */
public class StackContainer extends Fragment implements FragmentManager.OnBackStackChangedListener {

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void showFragment(Fragment fragment) {
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

        if (!needSaveState) {
            getChildFragmentManager().executePendingTransactions();
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

        getChildFragmentManager().addOnBackStackChangedListener(this);
        listener.onViewCreated(savedInstanceState);
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

    public Fragment getFragment() {
        return getAttachedFragment();
    }

    public int getBackStackSize() {
        return getChildFragmentManager().getBackStackEntryCount();
    }

    private Fragment getAttachedFragment() {
        return getChildFragmentManager().findFragmentById(R.id.container);
    }

    public interface Listener {
        void onViewCreated(Bundle savedInstanceState);
        void onBackStackChanged();
    }
}
