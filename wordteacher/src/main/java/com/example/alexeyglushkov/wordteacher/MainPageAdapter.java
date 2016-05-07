package com.example.alexeyglushkov.wordteacher;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by alexeyglushkov on 02.05.16.
 */
public class MainPageAdapter extends FragmentStatePagerAdapter {

    StackContainer stackContainer;

    public MainPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        QuizletCardsFragment quizletFragment = new QuizletCardsFragment();
        QuizletCardsFragment.ViewType viewType = position == 0 ? QuizletCardsFragment.ViewType.Sets : QuizletCardsFragment.ViewType.Cards;
        quizletFragment.setViewType(viewType);

        Fragment result = quizletFragment;
        if (position == 0) {
            stackContainer = new StackContainer();
            stackContainer.showFragment(quizletFragment);
            result = stackContainer;
        }

        return result;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String result = "";
        if (position == 0) {
            if (stackContainer != null) {
                result = getStackContainerTitle();
            }

            if (result.length() == 0) {
                result = "Sets";
            }

        } else {
            result = "Cards";
        }

        return result;
    }

    private String getStackContainerTitle() {
        String result = "";
        if (stackContainer.getBackStackSize() > 0) {
            QuizletCardsFragment cardsFragment = (QuizletCardsFragment)stackContainer.getFragment();
            if (cardsFragment.getParentSet() != null) {
                result = cardsFragment.getParentSet().getTitle();
            }
        }
        return result;
    }
}
