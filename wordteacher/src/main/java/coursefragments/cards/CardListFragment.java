package coursefragments.cards;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.wordteacher.R;

import java.util.List;

import listfragment.listmodule.view.BaseListAdaptor;
import listfragment.listmodule.view.BaseListFragment;
import listfragment.CompareStrategyFactory;
import listfragment.NullStorableListProvider;
import main.MainApplication;
import main.Preferences;
import model.Card;
import model.Course;
import model.CourseHolder;
import tools.SortOrderCompareStrategy;
import tools.Sortable;

/**
 * Created by alexeyglushkov on 30.07.16.
 */
public class CardListFragment extends BaseListFragment<Card> implements Sortable, CourseHolder.CourseHolderListener {

    //// Creation, initialization, restoration
    private Bundle savedInstanceState;

    public static CardListFragment create() {
        CardListFragment fragment = new CardListFragment();
        fragment.initialize();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        getCourseHolder().addListener(this);
        if (getCourseHolder().getState() != CourseHolder.State.Unitialized) {
            handleLoadedCourses();
        } else {
            showLoading();
        }
    }

    private void restoreIfNeeded() {
        if (this.savedInstanceState != null || provider instanceof NullStorableListProvider) {
            provider = providerFactory.restore(this.savedInstanceState);
            this.savedInstanceState = null;
        }
    }

    //// Events


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getCourseHolder().removeListener(this);
    }

    private void onHolderLoaded() {
        handleLoadedCourses();
    }

    private void handleLoadedCourses() {
        hideLoading();
        restoreIfNeeded();
        reload();
    }

    //// Actions

    //// Creation Methods

    @Override
    protected BaseListAdaptor createAdapter() {
        return createCardAdapter();
    }

    private CardListAdapter createCardAdapter() {
        CardListAdapter adapter = new CardListAdapter(new CardListAdapter.Listener() {
            @Override
            public void onCardClicked(View view, Card card) {
                CardListFragment.this.getListener().onRowClicked(card);
            }

            @Override
            public void onMenuClicked(View view, Card card) {
                CardListFragment.this.getListener().onRowMenuClicked(card, view);
            }

            @Override
            public void onCardViewDeleted(View view, Card card) {
                CardListFragment.this.getListener().onRowViewDeleted(card);
            }
        });

        return adapter;
    }

    @NonNull
    protected CardListFactory createProviderFactory() {
        return new CardListFactory(getCourseHolder());
    }

    public CompareStrategyFactory<Card> createCompareStrategyFactory() {
        return new CardCompareStrategyFactory();
    }

    //// Interface

    // Sortable

    @Override
    public void setSortOrder(Preferences.SortOrder sortOrder) {
        setCompareStrategy(getCompareStrategyFactory().createStrategy(sortOrder));
        reload();
    }

    @Override
    public Preferences.SortOrder getSortOrder() {
        return getCompareStrategy().getSortOrder();
    }

    // CourseHolder.CourseHolderListener

    @Override
    public void onLoaded(CourseHolder holder) {
        onHolderLoaded();
    }

    @Override
    public void onCoursesAdded(@NonNull CourseHolder holder, @NonNull List<Course> courses) {
        Course parentCourse = getParentCourse();
        if (!isExplicitCardList() && parentCourse == null) {
            reload();
        }
    }

    @Override
    public void onCoursesRemoved(@NonNull CourseHolder holder, @NonNull List<Course> courses) {
        Course parentCourse = getParentCourse();
        if (parentCourse != null) {
            if (courses.contains(getParentCourse())) {
                provider = new NullStorableListProvider<>();
                reload();
            }
        } else {
            reload();
        }
    }

    @Override
    public void onCourseUpdated(@NonNull CourseHolder holder, @NonNull Course course, @NonNull CourseHolder.UpdateBatch batch) {
        Course parentCourse = getParentCourse();
        if (parentCourse != null) {
            if (course.equals(parentCourse)) {
                reload();
            }
        } else {
            reload();
        }
    }

    //// Setters

    // Data Setters

    public void setParentCourse(Course course) {
        provider = providerFactory.createFromObject(course);
        reload();
    }

    //// Getters

    // App Getters

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    // Data Getters

    public @Nullable Course getParentCourse() {
        Course result = null;
        if (provider instanceof CourseCardListProvider) {
            CourseCardListProvider courseProvider = (CourseCardListProvider)provider;
            result = courseProvider.getCourse();
        }

        return result;
    }

    // State Getters

    private boolean isExplicitCardList() {
        return provider instanceof CardListProvider;
    }

    // Cast Getters

    private CardListAdapter getCardAdapter() {
        return (CardListAdapter)adapter;
    }

    private CardCompareStrategyFactory getCompareStrategyFactory() {
        return (CardCompareStrategyFactory)compareStrategyFactory;
    }

    private SortOrderCompareStrategy getCompareStrategy() {
        return (SortOrderCompareStrategy)compareStrategy;
    }
}
