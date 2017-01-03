package pagermodule.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import pagermodule.PagerModuleItemWithTitle;
import pagermodule.presenter.PagerPresenter;
import stackmodule.presenter.StackPresenter;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public class PagerViewImp implements PagerView, PagerAdapter.Listener, ViewPager.OnPageChangeListener {
    private PagerPresenter presenter;
    private int itemCount;

    private List<String> defaultTitles = new ArrayList<>();

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
                Class presenterClass = Class.forName(savedInstanceState.getString("presenterClass"));
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
        outState.putString("presenterClass", presenter.getClass().getName());
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

        presenter.onViewStateRestored(this, childs, savedInstanceState);
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
        String title = presenter.getViewTitleAtIndex(index);
        if (title == null && index < defaultTitles.size()){
            title = defaultTitles.get(index);
        }

        return title;
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

    public void setDefaultTitles(List<String> defaultTitles) {
        this.defaultTitles = defaultTitles;
    }

    public void setPresenter(PagerPresenter presenter) {
        this.presenter = presenter;
        this.presenter.setView(this);
    }

    //// Getter


    public PagerPresenter getPresenter() {
        return presenter;
    }
}
