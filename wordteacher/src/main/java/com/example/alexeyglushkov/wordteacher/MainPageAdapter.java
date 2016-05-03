package com.example.alexeyglushkov.wordteacher;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by alexeyglushkov on 02.05.16.
 */
public class MainPageAdapter extends FragmentStatePagerAdapter {
    public MainPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        QuizletCardsFragment fr = new QuizletCardsFragment();
        QuizletCardsFragment.ViewType viewType = position == 0 ? QuizletCardsFragment.ViewType.Sets : QuizletCardsFragment.ViewType.Cards;
        fr.updateViewType(viewType);
        return fr;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return position == 0 ? "Sets" : "Cards";
    }
}
