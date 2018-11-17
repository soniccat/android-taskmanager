package com.example.alexeyglushkov.uimodulesandclasses.pagermodule.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.util.SparseArray;

import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.presenter.PagerPresenter;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public class PagerViewImp implements PagerView, PagerAdapter.Listener, ViewPager.OnPageChangeListener {
    private final String PRESENTER_CLASS = "PRESENTER_CLASS_PagerViewImp";

    private PagerPresenter presenter;
    private int itemCount;

    private @NonNull ViewPager viewPager;
    private @NonNull PagerAdapter pagerAdapter;

    //// Creation

    public PagerViewImp(@NonNull ViewPager pager, FragmentManager manager) {
        viewPager = pager;
        initialize(manager);
    }

    public void onViewCreated(Bundle savedInstanceState) {
        if (presenter == null && savedInstanceState != null) {
            try {
                Class presenterClass = Class.forName(savedInstanceState.getString(PRESENTER_CLASS));
                if (presenterClass != null) {
                    presenter = (PagerPresenter) presenterClass.newInstance();
                    presenter.setView(this);
                }
            } catch (Exception e) {
            }
        }

        presenter.onViewCreated(savedInstanceState);
    }

    public void initialize(FragmentManager fragmentManager) {
        viewPager.addOnPageChangeListener(this);

        pagerAdapter = new PagerAdapter(fragmentManager);
        pagerAdapter.setListener(this);
        viewPager.setAdapter(pagerAdapter);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString(PRESENTER_CLASS, presenter.getClass().getName());
        presenter.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO: create an util method
        SparseArray<Fragment> fragments = pagerAdapter.getFragments();
        SparseArray<Object> childs = new SparseArray<>();

        for (int i=0; i<fragments.size(); ++i) {
            int key = fragments.keyAt(i);
            Fragment fr = fragments.get(key);
            if (fr != null) {
                childs.put(key, fr);
            }
        }

        presenter.onViewStateRestored(this, viewPager.getCurrentItem(), childs, savedInstanceState);
    }

    //// Interface

    // PagerView

    public void updateView(int index) {
        pagerAdapter.notifyDataSetChanged();
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
        pagerAdapter.notifyDataSetChanged();
    }

    // PagerAdapter.Listener

    @Override
    public int getFragmentCount() {
        return itemCount;
    }

    @Override
    public Fragment getFragmentAtIndex(int index) {
        return (Fragment) presenter.getViewAtIndex(index);
    }

    @Override
    public String getTitleAtIndex(int index) {
        return presenter.getViewTitleAtIndex(index);
    }

    @Override
    public void onPagerAdapterStateRestored(int count) {
        itemCount = count; // store to have ViewPager restored correctly
    }

    // ViewPager.OnPageChangeListener

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        presenter.onPageChanged(viewPager.getCurrentItem());
    }

    //// Setter

    @Override
    public void setPresenter(PagerPresenter presenter) {
        this.presenter = presenter;
        this.presenter.setView(this);
    }

    //// Getter

    public PagerPresenter getPresenter() {
        return presenter;
    }
}
