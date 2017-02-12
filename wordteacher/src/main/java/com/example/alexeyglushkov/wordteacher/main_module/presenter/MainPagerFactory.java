package com.example.alexeyglushkov.wordteacher.main_module.presenter;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import com.example.alexeyglushkov.wordteacher.courselistmodules.CourseStackModuleFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.view.SimpleListFragment;
import com.example.alexeyglushkov.wordteacher.listmodule.view.SimpleListFragmentListenerAdapter;
import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.wordteacher.model.Card;
import com.example.alexeyglushkov.wordteacher.model.Course;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.PagerModule;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.PagerModuleFactory;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.PagerModuleItem;
import com.example.alexeyglushkov.wordteacher.quizletlistmodules.QuizletStackModuleFactory;
import com.example.alexeyglushkov.wordteacher.quizletlistmodules.termlistmodule.view.QuizletTermListFragment;
import com.example.alexeyglushkov.wordteacher.quizletlistmodules.termlistmodule.presenter.QuizletTermListPresenter;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.StackModuleListener;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.presenter.StackPresenter;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.view.StackFragment;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public class MainPagerFactory implements PagerModuleFactory {
    private StackModuleListener stackModuleListener;
    private SimpleListFragment.Listener<QuizletSet> quizletSetListener;
    private SimpleListFragment.Listener<QuizletTerm> quizletTermListener;
    private SimpleListFragment.Listener<Course> courseListListener;
    private SimpleListFragment.Listener<Card> cardListListener;

    //// Interfaces

    // PgerModuleFactory

    @Override
    public PagerModuleItem moduleAtIndex(int i, final PagerModule pagerModule) {
        PagerModuleItem item = null;
        switch (i) {
            case 0: {
                item = createQuizletStackModule(pagerModule);
                break;
            }
            case 1: {
                item = createQuizletTermsModule();
                break;
            }
            case 2:
                item = createCourseStackModule(pagerModule);
                break;
        }

        return item;
    }

    @Override
    public PagerModuleItem restoreModule(int i, Object viewObject, PagerModule pagerModule) {
        PagerModuleItem item = null;

        if (i == 0) {
            item = restoreQuizletStackModule((StackFragment) viewObject, pagerModule);

        } else if (i == 1) {
            item = restoreQuizletTermListModule((QuizletTermListFragment) viewObject);

        } else if (i == 2) {
            item = restoreCourseStackModule((StackFragment) viewObject, pagerModule);
        }

        return item;
    }

    @NonNull
    private PagerModuleItem restoreQuizletStackModule(StackFragment view, PagerModule pagerModule) {
        StackPresenter presenter = view.getPresenter();

        QuizletStackModuleFactory factory = (QuizletStackModuleFactory)presenter.getFactory();
        factory.setQuizletSetListener(createQuizletSetListener());
        factory.setQuizletTermListener(createQuizletTermListener());

        bindStackListener(pagerModule, view.getPresenter());
        return presenter;
    }

    private PagerModuleItem restoreQuizletTermListModule(QuizletTermListFragment view) {
        QuizletTermListPresenter presenter = (QuizletTermListPresenter)view.getPresenter();
        presenter.setListener(createQuizletTermListener());

        return presenter;
    }

    private PagerModuleItem restoreCourseStackModule(StackFragment view, PagerModule pagerModule) {
        StackPresenter presenter = view.getPresenter();

        CourseStackModuleFactory factory = (CourseStackModuleFactory)presenter.getFactory();
        factory.setCourseListListener(createCourseListener());
        factory.setCardListListener(createCardListener());

        bindStackListener(pagerModule, view.getPresenter());
        return presenter;
    }

    //// Creation methods

    @NonNull
    private PagerModuleItem createQuizletTermsModule() {
        QuizletTermListPresenter listPresenter = new QuizletTermListPresenter();
        listPresenter.setListener(createQuizletTermListener());

        QuizletTermListFragment view = QuizletTermListFragment.create();
        listPresenter.setView(view);
        view.setPresenter(listPresenter);

        listPresenter.setSortOrder(Preferences.getQuizletTermSortOrder());
        return listPresenter;
    }

    @NonNull
    private PagerModuleItem createQuizletStackModule(PagerModule pagerModule) {
        QuizletStackModuleFactory factory = new QuizletStackModuleFactory();
        factory.setQuizletSetListener(createQuizletSetListener());
        factory.setQuizletTermListener(createQuizletTermListener());

        StackFragment view = new StackFragment();
        StackPresenter stackPresenter = new StackPresenter();
        stackPresenter.setFactory(factory);
        stackPresenter.setView(view);
        bindStackListener(pagerModule, stackPresenter);
        view.setPresenter(stackPresenter);

        return stackPresenter;
    }

    @NonNull
    private PagerModuleItem createCourseStackModule(PagerModule pagerModule) {
        CourseStackModuleFactory factory = new CourseStackModuleFactory();
        factory.setCourseListListener(createCourseListener());
        factory.setCardListListener(createCardListener());

        StackFragment view = new StackFragment();
        StackPresenter stackPresenter = new StackPresenter();
        stackPresenter.setFactory(factory);
        stackPresenter.setView(view);
        bindStackListener(pagerModule, stackPresenter);
        view.setPresenter(stackPresenter);

        return stackPresenter;
    }

    private SimpleListFragment.Listener<QuizletSet> createQuizletSetListener() {
        return new SimpleListFragmentListenerAdapter<>(createQuizletSetListenerProvider());
    }

    private SimpleListFragment.Listener<QuizletTerm> createQuizletTermListener() {
        return new SimpleListFragmentListenerAdapter<>(createQuizletTermListenerProvider());
    }

    private SimpleListFragment.Listener<Course> createCourseListener() {
        return new SimpleListFragmentListenerAdapter<Course>(createCourseListenerProvider());
    }

    private SimpleListFragment.Listener<Card> createCardListener() {
        return new SimpleListFragmentListenerAdapter<>(createCardListenerProvider());
    }

    private SimpleListFragmentListenerAdapter.ListenerProvider<QuizletSet> createQuizletSetListenerProvider() {
        // we return provider because listener is null at the moment of restoration and will be set later
        return new SimpleListFragmentListenerAdapter.ListenerProvider<QuizletSet>() {
            @Override
            public SimpleListFragment.Listener<QuizletSet> getListener() {
                return quizletSetListener;
            }
        };
    }

    private SimpleListFragmentListenerAdapter.ListenerProvider<QuizletTerm> createQuizletTermListenerProvider() {
        // we return provider because listener is null at the moment of restoration and will be set later
        return new SimpleListFragmentListenerAdapter.ListenerProvider<QuizletTerm>() {
            @Override
            public SimpleListFragment.Listener<QuizletTerm> getListener() {
                return quizletTermListener;
            }
        };
    }

    private SimpleListFragmentListenerAdapter.ListenerProvider<Course> createCourseListenerProvider() {
        // we return provider because listener is null at the moment of restoration and will be set later
        return new SimpleListFragmentListenerAdapter.ListenerProvider<Course>() {
            @Override
            public SimpleListFragment.Listener<Course> getListener() {
                return courseListListener;
            }
        };
    }

    private SimpleListFragmentListenerAdapter.ListenerProvider<Card> createCardListenerProvider() {
        // we return provider because listener is null at the moment of restoration and will be set later
        return new SimpleListFragmentListenerAdapter.ListenerProvider<Card>() {
            @Override
            public SimpleListFragment.Listener<Card> getListener() {
                return cardListListener;
            }
        };
    }

    //// Setters

    private void bindStackListener(final PagerModule pagerModule, StackPresenter stackPresenter) {
        stackPresenter.setListener(new StackModuleListener() {
            @Override
            public void onBackStackChanged() {
                pagerModule.updatePage(0);

                if (stackModuleListener != null) {
                    stackModuleListener.onBackStackChanged();
                }
            }
        });
    }

    public void setStackModuleListener(StackModuleListener stackModuleListener) {
        this.stackModuleListener = stackModuleListener;
    }

    public void setQuizletSetListener(SimpleListFragment.Listener<QuizletSet> quizletSetListener) {
        this.quizletSetListener = quizletSetListener;
    }

    public void setQuizletTermListener(SimpleListFragment.Listener<QuizletTerm> quizletTermListener) {
        this.quizletTermListener = quizletTermListener;
    }

    public void setCourseListListener(SimpleListFragment.Listener<Course> courseListListener) {
        this.courseListListener = courseListListener;
    }

    public void setCardListListener(SimpleListFragment.Listener<Card> cardListListener) {
        this.cardListListener = cardListListener;
    }
}
