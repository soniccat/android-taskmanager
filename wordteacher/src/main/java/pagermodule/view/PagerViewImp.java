package pagermodule.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;
import java.util.List;

import coursefragments.CourseListStackFragment;
import pagermodule.presenter.PagerPresenter;
import quizletfragments.QuizletStackFragment;

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

    public void initialize(FragmentManager fragmentManager) {
        viewPager.addOnPageChangeListener(this);

        pagerAdapter = new PagerAdapter(fragmentManager);
        pagerAdapter.setListener(this);
        viewPager.setAdapter(pagerAdapter);
    }

    //// Interface

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
        String title = null;
        Fragment fragment = getFragmentAtIndex(index);

        if (fragment != null && fragment instanceof PagerModuleItemWithTitle) {
            PagerModuleItemWithTitle fragmentWithTitle = (PagerModuleItemWithTitle)fragment;
            title = fragmentWithTitle.getTitle();

        } else {
            title = defaultTitles.get(index);
        }

        return title;
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

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
        pagerAdapter.notifyDataSetChanged();
    }

    public void setPresenter(PagerPresenter presenter) {
        this.presenter = presenter;
    }
}
