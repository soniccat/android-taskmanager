package com.example.alexeyglushkov.wordteacher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.Stack;

/**
 * Created by alexeyglushkov on 02.05.16.
 */
public class MainPageAdapter extends FragmentStatePagerAdapter {

    private SparseArray<Fragment> fragments = new SparseArray<>();
    private Listener listener;

    public MainPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public Fragment getItem(final int position) {
        Fragment result = null;
        if (position == 1) {
            QuizletCardsFragment quizletFragment = createQuizletFragment(position);
            onFragmentReady(quizletFragment, position);
            result = quizletFragment;

        } else if (isStackContainer(position)) {
            final StackContainer stackContainer = new StackContainer();
            stackContainer.setListener(new StackContainer.Listener() {
                @Override
                public void onViewCreated(Bundle savedInstanceState) {
                    onStackContainerReady(stackContainer, position, savedInstanceState);
                    onFragmentReady(stackContainer, position);
                }

                @Override
                public void onBackStackChanged() {
                    onFragmentReady(stackContainer, position);
                }
            });

            result = stackContainer;
        }

        fragments.put(position, result);
        return result;
    }

    private void onFragmentReady(Fragment fragment, int position) {
        listener.onFragmentReady(fragment, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        fragments.remove(position);
    }

    public Fragment getFragment(int position) {
        return fragments.get(position);
    }

    private void onStackContainerReady(StackContainer container, int position, Bundle savedInstanceState) {
        Fragment result;
        if (savedInstanceState == null) {
            if (position == 0) {
                result = createQuizletFragment(position);
            } else {
                result = new CourseFragment();
            }

            container.showFragment(result);
        }
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
        if (isStackContainer(position)) {
            if (getStackContainer(position) != null) {
                result = getStackContainerTitle(position);
            }

            if (result.length() == 0) {
                if (position == 0) {
                    result = "Sets";
                } else {
                    result = "Courses";
                }
            }

        } else {
            result = "Cards";
        }

        return result;
    }

    private StackContainer getStackContainer(int position) {
        return (StackContainer)fragments.get(position);
    }

    private String getStackContainerTitle(int position) {
        String result = "";
        StackContainer stackContainer = getStackContainer(position);
        if (stackContainer.getBackStackSize() > 0) {
            QuizletCardsFragment cardsFragment = (QuizletCardsFragment)stackContainer.getFragment();
            if (cardsFragment.getParentSet() != null) {
                result = cardsFragment.getParentSet().getTitle();
            }
        }
        return result;
    }

    public interface Listener {
        void onFragmentReady(Fragment fragment, int position);
    }
}
