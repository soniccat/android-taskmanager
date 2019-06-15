package com.example.alexeyglushkov.wordteacher.quizletlistmodules.termlistmodule.presenter;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.List;

import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategy;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProviderFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableResourceListLiveDataProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.presenter.SimpleListPresenter;
import com.example.alexeyglushkov.wordteacher.main.MainApplication;
import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.uimodulesandclasses.pagermodule.PagerModuleItemWithTitle;
import com.example.alexeyglushkov.wordteacher.tools.Sortable;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public class QuizletTermListPresenter extends SimpleListPresenter<QuizletTerm>
        implements Sortable,
        PagerModuleItemWithTitle {
    private final static String TAG = "QuizletTerm..Presenter";

    public static String DEFAULT_TITLE = "Cards";
    @Nullable private QuizletSet set;

    //// Overrides

    @NonNull
    protected StorableListProviderFactory<QuizletTerm> createProviderFactory() {
        return null;
    }

    @Override
    protected StorableResourceListLiveDataProvider<QuizletTerm> createLiveDataProvider(Bundle bundle) {
        long setId = this.set != null ? this.set.getId() : -1;
        return new QuizletTermListLiveDataProvider(bundle, getQuizletRepository(), setId);
    }

    @Override
    protected CompareStrategy<QuizletTerm> createSortStrategy(Preferences.SortOrder order) {
        return new QuizletTermCompareStrategy(order);
    }


    //// Interface

    // PagerModuleItemWithTitle

    @Override
    public String getTitle() {
        QuizletSet set = getParentSet();
        return set != null ? set.getTitle() : DEFAULT_TITLE;
    }

    //// Setters

    // Set Data

    public void setTermSet(QuizletSet set) {
        this.set = set;
        if (liveDataProvider != EMPTY_LIST_DATA_PROVIDER) {
            Log.e(TAG, "setTermSet is called when liveDataProvider is not EMPTY_LIST_DATA_PROVIDER");
            liveDataProvider = createLiveDataProvider(null);
        }
    }

    public void setTerms(List<QuizletTerm> terms) {
    }

    //// Getters

    @Nullable private QuizletSet getParentSet() {
        return set;
    }

    // Get App Data

    private MainApplication getMainApplication() {
        return MainApplication.Companion.getInstance();
    }

    private QuizletRepository getQuizletRepository() {
        return getMainApplication().getQuizletRepository();
    }
}
