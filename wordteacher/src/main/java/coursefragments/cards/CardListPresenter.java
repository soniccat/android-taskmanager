package coursefragments.cards;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import java.util.List;

import listmodule.CompareStrategyFactory;
import listmodule.NullStorableListProvider;
import listmodule.presenter.SimpleListPresenter;
import listmodule.view.ListViewInterface;
import main.MainApplication;
import main.Preferences;
import model.Card;
import model.Course;
import model.CourseHolder;
import tools.SortOrderCompareStrategy;
import tools.Sortable;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public class CardListPresenter extends SimpleListPresenter<Card> implements Sortable, CourseHolder.CourseHolderListener {

    public static String DEFAULT_TITLE = "Cards";
    private Bundle savedInstanceState;

    //// Creation, initialization, restoration

    @Override
    public void onViewStateRestored(ListViewInterface view, @Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(view, savedInstanceState);

        this.savedInstanceState = savedInstanceState;

        getCourseHolder().addListener(this);
        if (getCourseHolder().getState() != CourseHolder.State.Unitialized) {
            handleLoadedCourses();
        } else {
            view.showLoading();
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

    //// Actions

    private void handleLoadedCourses() {
        view.hideLoading();
        restoreIfNeeded();
        reload();
    }

    //// Interface

    // BaseListPresenter

    @NonNull
    protected CardListFactory createProviderFactory() {
        return new CardListFactory(getCourseHolder());
    }

    public CompareStrategyFactory<Card> createCompareStrategyFactory() {
        return new CardCompareStrategyFactory();
    }

    // PagerModuleItemWithTitle

    @Override
    public String getTitle() {
        Course set = getParentCourse();
        return set != null ? set.getTitle() : DEFAULT_TITLE;
    }


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

    public @Nullable
    Course getParentCourse() {
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

    private CardCompareStrategyFactory getCompareStrategyFactory() {
        return (CardCompareStrategyFactory)compareStrategyFactory;
    }

    private SortOrderCompareStrategy getCompareStrategy() {
        return (SortOrderCompareStrategy)compareStrategy;
    }
}
