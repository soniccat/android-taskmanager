package com.example.alexeyglushkov.uimodulesandclasses.pagermodule.view;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Created by alexeyglushkov on 02.05.16.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private SparseArray<Fragment> fragments = new SparseArray<>();
    private Listener listener;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return listener.getFragmentCount();
    }

    @Override
    public Fragment getItem(final int position) {
        return listener.getFragmentAtIndex(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        fragments.remove(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return listener.getTitleAtIndex(position);
    }

    @Override
    public Parcelable saveState() {
        Bundle bundle = (Bundle)super.saveState();
        int[] fragmentKeys = new int[fragments.size()];
        for (int i=0; i<fragments.size(); ++i) {
            fragmentKeys[i] = fragments.keyAt(i);
        }

        bundle.putIntArray("PagerAdapter_fragmentKeys", fragmentKeys);
        bundle.putInt("PagerAdapter_getCount", getCount());
        return bundle;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
        Bundle bundle = (Bundle)state;
        int[] fragmentKeys = bundle.getIntArray("PagerAdapter_fragmentKeys");
        for (int i = 0; i < fragmentKeys.length; ++i) {
            int key = fragmentKeys[i];
            Fragment fr = (Fragment) instantiateItem(null, key);
            fragments.put(key, fr);
        }

        int pageCount = bundle.getInt("PagerAdapter_getCount");
        listener.onPagerAdapterStateRestored(pageCount);
    }

    public @Nullable Fragment getFragment(int i) {
        return fragments.get(i);
    }

    private int getFragmentIndex(Fragment fragment) {
        return fragments.indexOfValue(fragment);
    }

    public SparseArray<Fragment> getFragments() {
        return fragments;
    }

    public interface Listener {
        int getFragmentCount();
        Fragment getFragmentAtIndex(int index);
        String getTitleAtIndex(int index);
        void onPagerAdapterStateRestored(int count);
    }
}
