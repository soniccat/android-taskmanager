package com.example.alexeyglushkov.wordteacher;

import android.support.annotation.NonNull;
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
        Fragment result = null;
        if (position == 0 || position == 1) {
            QuizletCardsFragment quizletFragment = createQuizletFragment(position);
            result = quizletFragment;

        } else {
            result = new CourseFragment();
        }

        if (isStackContainer(position)) {
            stackContainer = new StackContainer();
            stackContainer.showFragment(result);
            result = stackContainer;
        }

        return result;
    }

    private boolean isStackContainer(int position) {
        return position == 0 || position == 2;
    }

    @NonNull
    private QuizletCardsFragment createQuizletFragment(int position) {
        QuizletCardsFragment quizletFragment = new QuizletCardsFragment();
        QuizletCardsFragment.ViewType viewType = position == 0 ? QuizletCardsFragment.ViewType.Sets : QuizletCardsFragment.ViewType.Cards;
        quizletFragment.setViewType(viewType);
        return quizletFragment;
    }

    @Override
    public int getCount() {
        return 3;
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
