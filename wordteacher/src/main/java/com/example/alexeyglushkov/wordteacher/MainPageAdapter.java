package com.example.alexeyglushkov.wordteacher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

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
    public int getCount() {
        return 3;
    }

    private boolean isStackContainer(int position) {
        return position == 0 || position == 2;
    }

    @Override
    public Fragment getItem(final int position) {
        Fragment result = null;
        if (position == 1) {
            QuizletCardsFragment quizletFragment = createQuizletFragment(position);
            result = quizletFragment;

        } else if (isStackContainer(position)) {
            final StackContainer stackContainer = new StackContainer();
            result = stackContainer;
        }

        return result;
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

    // must be called from onAttach
    public void updateStackContainerListener(final StackContainer stackContainer) {
        if (stackContainer.getListener() == null) {
            stackContainer.setListener(new StackContainer.Listener() {
                @Override
                public void onViewCreated(Bundle savedInstanceState) {
                    final int position = getFragmentIndex(stackContainer);
                    // fill stackContainer.fragment
                    onStackContainerReady(stackContainer, position, savedInstanceState);
                    onFragmentReady(stackContainer.getFragment(), position);

                    final View view = stackContainer.getView();
                    view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            view.getViewTreeObserver().removeOnPreDrawListener(this);
                            notifyDataSetChanged();
                            return false;
                        }
                    });
                }

                @Override
                public void onBackStackChanged() {
                    final int position = getFragmentIndex(stackContainer);
                    onFragmentReady(stackContainer.getFragment(), position);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void onFragmentReady(Fragment fragment, int position) {
        listener.onStackFragmentReady(fragment, position);
    }

    public void onStackContainerReady(StackContainer container, int position, Bundle savedInstanceState) {
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

    @NonNull
    private QuizletCardsFragment createQuizletFragment(int position) {
        QuizletCardsFragment quizletFragment = new QuizletCardsFragment();
        QuizletCardsFragment.ViewType viewType = getQuizletFragmentType(position);
        quizletFragment.setViewType(viewType);
        return quizletFragment;
    }

    @NonNull
    private QuizletCardsFragment.ViewType getQuizletFragmentType(int position) {
        return position == 0 ? QuizletCardsFragment.ViewType.Sets : QuizletCardsFragment.ViewType.Cards;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String result = "";
        if (isStackContainer(position)) {
            if (getStackContainer(position) != null) {
                result = getStackContainerTitle(position);
            }

            if (result.length() == 0) {
                result = getDefaultStackTitle(position);
            }

        } else {
            result = "Cards";
        }

        return result;
    }

    private String getStackContainerTitle(int position) {
        String result = "";
        StackContainer stackContainer = getStackContainer(position);
        if (stackContainer.getBackStackSize() > 0) {
            if (position == 0) {
                QuizletCardsFragment cardsFragment = (QuizletCardsFragment) stackContainer.getFragment();
                if (cardsFragment.getParentSet() != null) {
                    result = cardsFragment.getParentSet().getTitle();
                }
            }
        }
        return result;
    }

    @NonNull
    private String getDefaultStackTitle(int position) {
        String result;
        if (position == 0) {
            result = "Sets";
        } else {
            result = "Courses";
        }
        return result;
    }

    private StackContainer getStackContainer(int position) {
        return (StackContainer)fragments.get(position);
    }

    public Fragment getFragment(int i) {
        return fragments.get(i);
    }

    private int getFragmentIndex(Fragment fragment) {
        return fragments.indexOfValue(fragment);
    }

    public interface Listener {
        void onStackFragmentReady(Fragment fragment, int position);
    }
}
