package com.example.alexeyglushkov.wordteacher.courselistmodules;

import com.example.alexeyglushkov.wordteacher.courselistmodules.cardlistmodule.view.CardListFragment;
import com.example.alexeyglushkov.wordteacher.courselistmodules.cardlistmodule.presenter.CardListPresenter;
import com.example.alexeyglushkov.wordteacher.courselistmodules.courselistmodule.view.CourseListFragment;
import com.example.alexeyglushkov.wordteacher.courselistmodules.courselistmodule.presenter.CourseListPresenter;
import com.example.alexeyglushkov.wordteacher.listmodule.view.SimpleListFragment;
import com.example.alexeyglushkov.wordteacher.listmodule.view.SimpleListFragmentListenerAdapter;
import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.wordteacher.model.Card;
import com.example.alexeyglushkov.wordteacher.model.Course;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.StackModule;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.StackModuleFactory;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.StackModuleItem;

/**
 * Created by alexeyglushkov on 04.01.17.
 */

public class CourseStackModuleFactory implements StackModuleFactory {
    private SimpleListFragment.Listener<Course> courseListListener;
    private SimpleListFragment.Listener<Card> cardListListener;

    @Override
    public StackModuleItem rootModule(final StackModule stackModule) {
        CourseListPresenter listPresenter = new CourseListPresenter();
        CourseListFragment fragment = CourseListFragment.create();
        bindSetListPresenterListener(listPresenter, stackModule);

        listPresenter.setView(fragment);
        fragment.setPresenter(listPresenter);

        listPresenter.setSortOrder(Preferences.getCourseListSortOrder());
        return listPresenter;
    }

    private void bindSetListPresenterListener(CourseListPresenter presenter, final StackModule stackModule) {
        presenter.setListener(new SimpleListFragmentListenerAdapter<>(createCourseListListenerProvider()));
    }

    @Override
    public StackModuleItem moduleFromObject(Object object, StackModule stackModule) {
        CardListPresenter listPresenter = new CardListPresenter();
        CardListFragment fragment = CardListFragment.create();
        bindTermListPresenterListener(listPresenter, stackModule);

        listPresenter.setView(fragment);
        fragment.setPresenter(listPresenter);
        listPresenter.setParentCourse((Course) object);

        listPresenter.setSortOrder(Preferences.getCardListSortOrder());
        return listPresenter;
    }

    private void bindTermListPresenterListener(CardListPresenter presenter, StackModule stackModule) {
        presenter.setListener(new SimpleListFragmentListenerAdapter<Card>(createCardListListenerProvider()));
    }

    @Override
    public StackModuleItem restoreModule(Object viewObject, StackModule stackModule) {
        StackModuleItem item = null;
        if (viewObject instanceof CourseListFragment) {
            CourseListFragment setListFragment = (CourseListFragment)viewObject;
            CourseListPresenter setListPresenter = (CourseListPresenter)setListFragment.getPresenter();
            bindSetListPresenterListener(setListPresenter, stackModule);
            item = setListPresenter;

        } else if (viewObject instanceof CardListFragment) {
            CardListFragment termListFragment = (CardListFragment)viewObject;
            CardListPresenter termListPresenter = (CardListPresenter)termListFragment.getPresenter();
            bindTermListPresenterListener(termListPresenter, stackModule);
            item = termListPresenter;
        }

        return item;
    }

    //// Creation methods

    private SimpleListFragmentListenerAdapter.ListenerProvider<Course> createCourseListListenerProvider() {
        // we return provider because listener is null at the moment of restoration and will be set later
        return new SimpleListFragmentListenerAdapter.ListenerProvider<Course>() {
            @Override
            public SimpleListFragment.Listener<Course> getListener() {
                return courseListListener;
            }
        };
    }

    private SimpleListFragmentListenerAdapter.ListenerProvider<Card> createCardListListenerProvider() {
        // we return provider because listener is null at the moment of restoration and will be set later
        return new SimpleListFragmentListenerAdapter.ListenerProvider<Card>() {
            @Override
            public SimpleListFragment.Listener<Card> getListener() {
                return cardListListener;
            }
        };
    }

    //// Setters

    public void setCourseListListener(SimpleListFragment.Listener<Course> cardListener) {
        this.courseListListener = cardListener;
    }

    public void setCardListListener(SimpleListFragment.Listener<Card> cardListener) {
        this.cardListListener = cardListener;
    }
}
