package com.example.alexeyglushkov.wordteacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProxy;
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.service.CachableHttpLoadTask;
import com.example.alexeyglushkov.streamlib.CancelError;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import coursefragments.courses.CourseListPresenter;
import coursefragments.courses.CourseListPresenterMenuListener;
import learning.LearnActivity;
import listmodule.ListModuleInterface;
import main.MainApplication;
import main.Preferences;
import model.Card;
import model.Course;
import model.CourseHolder;
import pagermodule.PagerModule;
import pagermodule.PagerModuleItem;
import pagermodule.PagerModuleListener;
import pagermodule.presenter.PagerPresenter;
import pagermodule.presenter.StatePagerPresenter;
import pagermodule.view.PagerView;
import pagermodule.view.PagerViewImp;
import quizletfragments.sets.QuizletSetFragmentMenuListener;
import quizletfragments.sets.QuizletSetListPresenter;
import quizletfragments.terms.QuizletTermFragmentMenuListener;
import quizletfragments.terms.QuizletTermListPresenter;
import stackmodule.StackModule;
import stackmodule.StackModuleListener;
import tools.Sortable;

/**
 * Created by alexeyglushkov on 07.01.17.
 */

public class MainPresenterImp implements
        MainPresenter,
        PagerModuleListener,
        QuizletService.QuizletServiceListener,
        CourseHolder.CourseHolderListener {
    private PagerModule pagerModule;
    private MainView view;

    //// Creation and initialization

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getCourseHolder().addListener(this);
        getQuizletService().addListener(this);

        pagerModule = createPagerModule(savedInstanceState);
        if (savedInstanceState == null) {
            pagerModule.reload();
        }

        forceLoadSetsIfNeeded();
    }

    @Override
    public void onDestroy() {
        getCourseHolder().removeListener(this);
        getQuizletService().removeListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        PagerPresenter pagerPresenter = (PagerPresenter)pagerModule;
        pagerPresenter.getView().onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        StatePagerPresenter pagerPresenter = (StatePagerPresenter)pagerModule;
        pagerPresenter.getView().onRestoreInstanceState(savedInstanceState);
        MainPagerFactory factory = (MainPagerFactory)pagerPresenter.getFactory();
        factory.setStackModuleListener(createStackModuleListener());
        factory.setQuizletSetListener(createSetMenuListener());
        factory.setQuizletTermListener(createTermMenuListener());
        factory.setCourseListListener(createMenuCourseListener());

        pagerModule.reload();
        updateToolbarBackButton();
    }

    //// Events

    @Override
    public void onFabPressed() {
        loadQuizletSets();
    }

    //// Actions

    private void forceLoadSetsIfNeeded() {
        if (getQuizletService().getState() == QuizletService.State.Restored) {
            loadQuizletSets();
        }
    }

    private void loadQuizletSets() {
        CachableHttpLoadTask.CacheMode cacheMode = CachableHttpLoadTask.CacheMode.ONLY_STORE_TO_CACHE;
        final ServiceCommandProxy commandProxy = getQuizletService().loadSets(cacheMode, view.startProgress());

        view.startProgress(new MainView.ProgressCallback() {
            @Override
            public void onCancelled() {
                // while authorization it could be null
                if (!commandProxy.isEmpty()) {
                    getQuizletService().cancel(commandProxy.getServiceCommand());
                }
            }
        });
    }

    private void updateToolbarBackButton() {
        StackModule module = getStackModule(pagerModule.getCurrentIndex());

        boolean needShowBackButton = module != null && module.getSize() > 1;
        if (needShowBackButton) {
            view.showToolbarBackButton();
        } else {
            view.hideToolbarBackButton();
        }
    }

    private void startLearnNewWords(@NonNull Course course) {
        startLearnActivity(course.getNotStartedCards());
    }

    private void startLearnActivity(@NonNull List<Card> cards) {
        Intent activityIntent = new Intent(view.getContext(), LearnActivity.class);
        String[] cardIds = new String[cards.size()];

        for (int i=0; i<cards.size(); ++i) {
            Card card = cards.get(i);
            cardIds[i] = card.getId().toString();
        }

        activityIntent.putExtra(LearnActivity.EXTRA_CARD_IDS, cardIds);
        activityIntent.putExtra(LearnActivity.EXTRA_DEFINITION_TO_TERM, true);

        view.startActivityForResult(activityIntent, LearnActivity.ACTIVITY_RESULT);
    }

    // Backstack

    public void onBackStackChanged() {
        updateToolbarBackButton();
        view.invalidateToolbar();
    }

    public boolean onBackPressed() {
        final Object module = getCurrentModule();
        if (module != null && module instanceof StackModule) {
            StackModule stackModule = (StackModule)module;
            if (stackModule.getSize() > 1) {
                stackModule.pop(null);
                return true;
            }
        }

        return false;
    }

    //// Interface

    // PagerModuleListener

    @Override
    public int getPageCount() {
        return 3;
    }

    @Override
    public void onCurrentPageChanged() {
        view.invalidateToolbar();
    }

    // QuizletService.QuizletServiceListener

    @Override
    public void onStateChanged(QuizletService service, QuizletService.State oldState) {
        handleLoadedQuizletSets();
    }

    @Override
    public void onLoadError(QuizletService service, Error error) {
        boolean isCancelled = error instanceof CancelError;
        if (error instanceof Authorizer.AuthError) {
            isCancelled = ((Authorizer.AuthError)error).getReason() == Authorizer.AuthError.Reason.Cancelled;
        }

        if (!isCancelled) {
            showLoadErrorSnackBar(error);
        }

        loadingButton.stopLoading();
    }

    // CourseHolder.CourseHolderListener

    @Override
    public void onLoaded(CourseHolder holder) {
        onCourseHolderChanged();
    }

    @Override
    public void onCoursesAdded(@NonNull CourseHolder holder, @NonNull List<Course> courses) {
        onCourseHolderChanged();
    }

    @Override
    public void onCoursesRemoved(@NonNull CourseHolder holder, @NonNull List<Course> courses) {
        onCourseHolderChanged();
    }

    @Override
    public void onCourseUpdated(@NonNull CourseHolder holder, @NonNull Course course, @NonNull CourseHolder.UpdateBatch batch) {
        onCourseHolderChanged();
    }

    //// Creation methods

    private PagerModule createPagerModule(Bundle savedInstanceState) {
        PagerView pagerView = view.createPagerView();

        if (savedInstanceState == null) {
            MainPagerFactory factory = new MainPagerFactory();
            factory.setStackModuleListener(createStackModuleListener());
            factory.setQuizletSetListener(createSetMenuListener());
            factory.setQuizletTermListener(createTermMenuListener());
            factory.setCourseListListener(createMenuCourseListener());

            StatePagerPresenter pagerPresenter = new StatePagerPresenter();
            pagerPresenter.setDefaultTitles(new ArrayList<>(Arrays.asList(new String[]{QuizletSetListPresenter.DEFAULT_TITLE, QuizletTermListPresenter.DEFAULT_TITLE, CourseListPresenter.DEFAULT_TITLE})));
            pagerPresenter.setFactory(factory);

            pagerView.setPresenter(pagerPresenter);
            pagerPresenter.setView(pagerView);
            pagerView.onViewCreated(null);

        } else {
            pagerView.onViewCreated(savedInstanceState);
        }

        PagerPresenter presenter = pagerView.getPresenter();
        presenter.setListener(this);

        return presenter;
    }

    // TODO: move it somewhere

    private StackModuleListener createStackModuleListener() {
        return new StackModuleListener() {
            @Override
            public void onBackStackChanged() {
                MainPresenterImp.this.onBackStackChanged();
            }
        };
    }

    @NonNull
    private QuizletSetFragmentMenuListener createSetMenuListener() {
        return new QuizletSetFragmentMenuListener(this, getCourseHolder(), new QuizletSetFragmentMenuListener.Listener<QuizletSet>() {
            @Override
            public void onRowClicked(QuizletSet set) {
                // Handled inside QuizletStackModuleFactory
            }

            @Override
            public void onDataDeletionCancelled(QuizletSet data) {
            }

            @Override
            public void onDataDeleted(QuizletSet data, Exception exception) {
            }

            @Override
            public void onCourseCreated(Course course, Exception exception) {
                MainPresenterImp.this.onCourseChanged(course, exception);
            }

            @Override
            public void onCardsAdded(Course course, Exception exception) {
                MainPresenterImp.this.onCourseChanged(course, exception);
            }

            @Override
            public ViewGroup getDialogContainer() {
                return (ViewGroup) getRootView();
            }

            @Override
            public void onCourseChanged(Course course) {
            }
        });
    }

    @NonNull
    private QuizletTermFragmentMenuListener createTermMenuListener() {
        return new QuizletTermFragmentMenuListener(this, getCourseHolder(), new QuizletTermFragmentMenuListener.Listener<QuizletTerm>() {
            @Override
            public void onRowClicked(QuizletTerm data) {
            }

            @Override
            public void onDataDeletionCancelled(QuizletTerm data) {
            }

            @Override
            public void onDataDeleted(QuizletTerm data, Exception exception) {
            }

            @Override
            public void onCourseCreated(@NonNull Course course, Exception exception) {
                MainPresenterImp.this.onCourseChanged(course, exception);
            }

            @Override
            public void onCardsAdded(@NonNull Course course, Exception exception) {
                MainPresenterImp.this.onCourseChanged(course, exception);
            }

            @Nullable
            @Override
            public ViewGroup getDialogContainer() {
                return (ViewGroup) getRootView();
            }

            @Override
            public void onCourseChanged(@NonNull Course course) {
                MainActivity.this.onCourseChanged(course, null);
            }
        });
    }

    private CourseListPresenterMenuListener createMenuCourseListener() {
        return new CourseListPresenterMenuListener(this, getCourseHolder(), new CourseListPresenterMenuListener.Listener() {
            @Override
            public void onCourseDeleteClicked(Course course) {
                ListModuleInterface listModule = getCourseListModule();
                if (listModule != null) {
                    listModule.delete(course);
                }
            }

            @Override
            public void onShowCourseContentClicked(Course course) {
                StackModule module = getStackModule(pagerModule.getCurrentIndex());
                if (module != null) {
                    module.push(course, null);
                }
            }

            @Override
            public void onRowClicked(Course course) {
                MainActivity.this.onCourseClicked(course);
            }

            @Override
            public void onLearnNewWordsClick(Course course) {
                MainActivity.this.onLearnNewWordsClick(course);
            }

            @Override
            public void onDataDeletionCancelled(Course course) {
                ListModuleInterface listModule = getCourseListModule();
                if (listModule != null) {
                    listModule.reload();
                }
            }

            @Override
            public void onDataDeleted(Course course, Exception exception) {
            }

            @Override
            public View getSnackBarViewContainer() {
                return getRootView();
            }
        });
    }

    //// Setters

    public void setView(MainView view) {
        this.view = view;
    }

    //// Getters

    // Data getters

    @NonNull
    private List<Card> getReadyCards() {
        ArrayList<Card> cards = new ArrayList<>();
        for (Course course : getCourseHolder().getCourses()) {
            cards.addAll(course.getReadyToLearnCards());
        }

        return cards;
    }

    private Preferences.SortOrder getCurrentSortOrder() {
        Preferences.SortOrder sortOrder = Preferences.SortOrder.BY_NAME;
        Object module = getCurrentModule();

        if (module instanceof StackModule) {
            StackModule stackModule = (StackModule)module;
            if (stackModule.getSize() > 0) {
                module = stackModule.getModuleAtIndex(stackModule.getSize() - 1);
            }
        }

        if (module instanceof Sortable) {
            Sortable sortableModule = (Sortable)module;
            sortOrder = sortableModule.getSortOrder();
        }

        return sortOrder;
    }

    public boolean isLearnButtonEnabled() {
        return getReadyCards().size() > 0;
    }

    // App getters

    @NonNull
    private MainApplication getMainApplication() {
        return (MainApplication)view.getApplication();
    }

    @NonNull
    public TaskManager getTaskManager() {
        return getMainApplication().getTaskManager();
    }

    @NonNull
    public AccountStore getAccountStore() {
        return getMainApplication().getAccountStore();
    }

    @NonNull
    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    @NonNull
    public QuizletService getQuizletService() {
        return getMainApplication().getQuizletService();
    }

    // Module Getters

    @Nullable
    private PagerModuleItem getCurrentModule() {
        return getModule(pagerModule.getCurrentIndex());
    }

    @Nullable
    private StackModule getQuizletStackModule() {
        return getStackModule(0);
    }

    @Nullable
    private StackModule getCourseListStackModule() {
        return getStackModule(2);
    }

    private ListModuleInterface getCourseListModule() {
        StackModule stackModule = getCourseListStackModule();
        return stackModule != null && stackModule.getSize() > 0 ? (ListModuleInterface)stackModule.getModuleAtIndex(0) : null;
    }

    @Nullable
    private StackModule getStackModule(int position) {
        StackModule result = null;

        Object module = getModule(position);
        if (module instanceof StackModule) {
            result = (StackModule)module;
        }

        return result;
    }

    @Nullable
    private ListModuleInterface getTermListQuizletModule() {
        return (ListModuleInterface)getModule(1);
    }

    @Nullable
    private PagerModuleItem getModule(int i) {
        return pagerModule.getModuleAtIndex( i);
    }
}
