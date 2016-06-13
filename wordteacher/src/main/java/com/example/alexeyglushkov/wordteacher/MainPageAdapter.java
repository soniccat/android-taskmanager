package com.example.alexeyglushkov.wordteacher;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import tools.Tools;

/**
 * Created by alexeyglushkov on 02.05.16.
 */
public class MainPageAdapter extends FragmentStatePagerAdapter {

    // keep last titles because fragment could be fully unloaded
    private SparseArray<String> titles = new SparseArray<>();
    private SparseArray<Fragment> fragments = new SparseArray<>();
    private Listener listener;

    public MainPageAdapter(FragmentManager fm) {
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
        // to show a valid title even when a fragment isn't in the memory
        String title = listener.getTitleAtIndex(position, false);
        if (title != null) {
            titles.put(position, title);
        } else {
            title = titles.get(position);

            if (title == null) {
                title = listener.getTitleAtIndex(position, true);
            }
        }

        return title;
    }

    @Override
    public Parcelable saveState() {
        Bundle bundle = (Bundle)super.saveState();
        if (bundle != null) {
            Tools.storeSparceArray(titles, bundle, 0);
        }
        return bundle;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
        Bundle bundle = (Bundle)state;
        titles = Tools.readSparceArray(bundle, 0);
    }

    public Fragment getFragment(int i) {
        return fragments.get(i);
    }

    private int getFragmentIndex(Fragment fragment) {
        return fragments.indexOfValue(fragment);
    }

    public interface Listener {
        int getFragmentCount();
        Fragment getFragmentAtIndex(int index);
        String getTitleAtIndex(int index, boolean isDefault);
    }
}
