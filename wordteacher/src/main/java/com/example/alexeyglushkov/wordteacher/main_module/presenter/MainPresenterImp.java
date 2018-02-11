package com.example.alexeyglushkov.wordteacher.main_module.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProxy;
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.service.HttpCacheableTransport;
import com.example.alexeyglushkov.streamlib.CancelError;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.alexeyglushkov.wordteacher.R;
import com.example.alexeyglushkov.wordteacher.courselistmodules.cardlistmodule.presenter.CardListPresenterMenuListener;
import com.example.alexeyglushkov.wordteacher.courselistmodules.cardlistmodule.presenter.CardListPresenter;
import com.example.alexeyglushkov.wordteacher.courselistmodules.courselistmodule.presenter.CourseListPresenter;
import com.example.alexeyglushkov.wordteacher.courselistmodules.courselistmodule.presenter.CourseListPresenterMenuListener;
import com.example.alexeyglushkov.wordteacher.learningmodule.presenter.LearnPresenterImp;
import com.example.alexeyglushkov.wordteacher.listmodule.ListModuleInterface;
import com.example.alexeyglushkov.wordteacher.main.MainApplication;
import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.wordteacher.main_module.view.MainView;
import com.example.alexeyglushkov.wordteacher.main_module.router.MainRouter;
import com.example.alexeyglushkov.wordteacher.model.Card;
import com.example.alexeyglushkov.wordteacher.model.Course;
import com.example.alexeyglushkov.wordteacher.model.CourseHolder;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.PagerModule;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.PagerModuleItem;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.PagerModuleListener;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.presenter.PagerPresenter;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.presenter.StatePagerPresenter;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.view.PagerView;
import com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter.QuizletSetPresenterMenuListener;
import com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter.QuizletSetListPresenter;
import com.example.alexeyglushkov.wordteacher.quizletlistmodules.termlistmodule.presenter.QuizletTermPresenterMenuListener;
import com.example.alexeyglushkov.wordteacher.quizletlistmodules.termlistmodule.presenter.QuizletTermListPresenter;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.StackModule;
import com.example.alexeyglushkov.uimodulesandclasses.stackmodule.StackModuleListener;
import com.example.alexeyglushkov.wordteacher.tools.Sortable;

/**
 * Created by alexeyglushkov on 07.01.17.
 */

public class MainPresenterImp implements
        MainPresenter,
        PagerModuleListener,
        QuizletService.QuizletServiceListener,
        CourseHolder.CourseHolderListener {
    private MainRouter router;
    private PagerModule pagerModule;
    private MainView view;

    //// Creation and initialization

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getCourseHolder().addListener(this);
        getQuizletService().addListener(this);

        router = createRouter();
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
        outState.putString("routerClassName", router.getClass().getName());

        PagerPresenter pagerPresenter = (PagerPresenter)pagerModule;
        pagerPresenter.getView().onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        StatePagerPresenter pagerPresenter = (StatePagerPresenter)pagerModule;
        pagerPresenter.getView().onRestoreInstanceState(savedInstanceState);
        MainPagerFactory factory = (MainPagerFactory)pagerPresenter.getFactory();
        setFactoryListeners(factory);

        pagerModule.reload();
        updateToolbarBackButton();
    }

    private @NonNull MainRouter createRouter() {
        MainRouter result = null;
        String name = view.getContext().getString(R.string.main_router_class);
        try {
            result = (MainRouter) view.getContext().getClassLoader().loadClass(name).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    //// Events

    // Course events

    private void onCourseHolderChanged() {
        view.invalidateToolbar();
    }

    private void onCourseChanged(Course course, @Nullable Exception exception) {
        if (exception != null) {
            view.showException(exception);
        }

        onCourseHolderChanged();
    }

    public void onCourseClicked(@NonNull Course course) {
        List<Card> cards = course.getReadyToLearnCards();
        if (cards.size() > 0) {
            showLearning(cards);

        } else if (course.getCards().size() > 0){
            showLearning(course.getCards());
        }
    }

    // Buttons press

    @Override
    public void onFabPressed() {
        loadQuizletSets();
    }

    @Override
    public void onStartPressed() {
        showLearning(getReadyCards());
    }

    @Override
    public void onDropboxPressed() {
        syncWithDropbox();
    }

    public void onLearnNewWordsClick(@NonNull Course course) {
        startLearnNewWords(course);
    }

    public void onSortOrderSelected(Preferences.SortOrder order) {
        if (getCurrentSortOrder() == order) {
            order = order.getInverse();
        }

        setSortOrder(order);
    }

    private void onSortOrderChanged(Preferences.SortOrder sortOrder, Sortable module) {
        if (module instanceof QuizletTermListPresenter) {
            Preferences.setQuizletTermSortOrder(sortOrder);

        } else if (module instanceof QuizletSetListPresenter) {
            Preferences.setQuizletSetSortOrder(sortOrder);

        } else if (module instanceof CourseListPresenter) {
            Preferences.setCourseListSortOrder(sortOrder);

        } else if (module instanceof CardListPresenter) {
            Preferences.setCardListSortOrder(sortOrder);
        }

        view.invalidateToolbar();
    }

    // Activity

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LearnPresenterImp.ACTIVITY_RESULT) {
        }
    }

    //// Actions

    private void handleLoadedQuizletSets() {
        forceLoadSetsIfNeeded();
    }

    private void forceLoadSetsIfNeeded() {
        if (getQuizletService().getState() == QuizletService.State.Restored) {
            loadQuizletSets();
        }
    }

    private void loadQuizletSets() {
        HttpCacheableTransport.CacheMode cacheMode = HttpCacheableTransport.CacheMode.ONLY_STORE_TO_CACHE;
        final ServiceCommandProxy[] cmdWrapper = new ServiceCommandProxy[1];

        ProgressListener progressListener = view.startProgress(new MainView.ProgressCallback() {
            @Override
            public void onCancelled() {
                // while authorization it could be empty
                if (cmdWrapper[0] != null && !cmdWrapper[0].isEmpty()) {
                    getQuizletService().cancel(cmdWrapper[0].getServiceCommand());
                }
            }
        });

        ServiceCommandProxy commandProxy = getQuizletService().loadSets(cacheMode, progressListener);
        cmdWrapper[0] = commandProxy;
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
        showLearning(course.getNotStartedCards());
    }

    private void showLearning(@NonNull List<Card> cards) {
        router.showLearningModule(view.getContext(), cards);
    }

    private void syncWithDropbox() {
        getMainApplication().getDropboxService().sync(getCourseHolder().getDirectory().getPath(), "/CoursesTest/", new ServiceCommand.CommandCallback() {
            @Override
            public void onCompleted(Error error) {
                getTaskManager().addTask(getCourseHolder().getLoadCourseListTask());
            }
        });
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
            view.showLoadError(error);
        }

        view.stopProgress();
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
            setFactoryListeners(factory);

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

    private StackModuleListener createStackModuleListener() {
        return new StackModuleListener() {
            @Override
            public void onBackStackChanged() {
                MainPresenterImp.this.onBackStackChanged();
            }
        };
    }

    // TODO: move that in appropriate list class
    @NonNull
    private QuizletSetPresenterMenuListener createSetMenuListener() {
        return new QuizletSetPresenterMenuListener(view.getContext(), getCourseHolder(), new QuizletSetPresenterMenuListener.Listener<QuizletSet>() {
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
                return (ViewGroup) view.getRootView();
            }

            @Override
            public void onCourseChanged(Course course) {
            }
        });
    }

    // TODO: move that in appropriate list class
    @NonNull
    private QuizletTermPresenterMenuListener createTermMenuListener() {
        return new QuizletTermPresenterMenuListener(view.getContext(), getCourseHolder(), new QuizletTermPresenterMenuListener.Listener<QuizletTerm>() {
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
                return (ViewGroup) view.getRootView();
            }

            @Override
            public void onCourseChanged(@NonNull Course course) {
                MainPresenterImp.this.onCourseChanged(course, null);
            }
        });
    }

    // TODO: move that in appropriate list class
    private CourseListPresenterMenuListener createMenuCourseListener() {
        return new CourseListPresenterMenuListener(view.getContext(), getCourseHolder(), new CourseListPresenterMenuListener.Listener() {
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
                MainPresenterImp.this.onCourseClicked(course);
            }

            @Override
            public void onLearnNewWordsClick(Course course) {
                MainPresenterImp.this.onLearnNewWordsClick(course);
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
                return view.getRootView();
            }
        });
    }

    // TODO: move that in appropriate list class
    private CardListPresenterMenuListener createMenuCardListener() {
        return new CardListPresenterMenuListener(view.getContext(), getCourseHolder(), new CardListPresenterMenuListener.Listener() {
            @Override
            public void onCardDeleteClicked(Card card) {
                ListModuleInterface listModule = getCourseListModule();
                if (listModule != null) {
                    listModule.delete(card);
                }
            }

            @Override
            public void onRowClicked(Card data) {
            }

            @Override
            public void onDataDeletionCancelled(Card data) {
                ListModuleInterface listModule = getCardListModule();
                if (listModule != null) {
                    listModule.reload();
                }
            }

            @Override
            public void onDataDeleted(Card data, Exception exception) {
            }

            @Override
            public View getSnackBarViewContainer() {
                return view.getRootView();
            }
        });
    }

    //// Setters

    private void setFactoryListeners(MainPagerFactory factory) {
        factory.setStackModuleListener(createStackModuleListener());
        factory.setQuizletSetListener(createSetMenuListener());
        factory.setQuizletTermListener(createTermMenuListener());
        factory.setCourseListListener(createMenuCourseListener());
        factory.setCardListListener(createMenuCardListener());
    }

    private void setSortOrder(Preferences.SortOrder sortOrder) {
        Object module = getCurrentModule();

        if (module instanceof StackModule) {
            StackModule stackModule = (StackModule)module;
            module = stackModule.getModuleAtIndex(stackModule.getSize()-1);
        }

        if (module instanceof Sortable) {
            Sortable sortable = (Sortable)module;
            sortable.setSortOrder(sortOrder);
            onSortOrderChanged(sortOrder, sortable);
        }
    }

    public void setView(MainView view) {
        this.view = view;
    }

    public void setRouter(MainRouter router) {
        this.router = router;
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

    public Preferences.SortOrder getCurrentSortOrder() {
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

    private ListModuleInterface getCardListModule() {
        StackModule stackModule = getCourseListStackModule();
        return stackModule != null && stackModule.getSize() > 1 ? (ListModuleInterface)stackModule.getModuleAtIndex(1) : null;
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
