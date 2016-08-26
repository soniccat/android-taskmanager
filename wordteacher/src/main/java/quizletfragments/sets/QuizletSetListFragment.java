package quizletfragments.sets;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import listfragment.BaseListAdaptor;
import listfragment.BaseListFragment;
import listfragment.CompareStrategyFactory;
import main.MainApplication;
import main.Preferences;
import tools.SortOrderCompareStrategy;
import tools.Sortable;
import tools.LongTools;

/**
 * Created by alexeyglushkov on 07.08.16.
 */
public class QuizletSetListFragment extends BaseListFragment<QuizletSet> implements Sortable {
    //// Creation, initialization, restoration

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        getMainApplication().addQuizletServiceListener(new MainApplication.ReadyListener() {
            @Override
            public void onReady() {
                onQuizletServiceLoaded(savedInstanceState);
            }
        });

        reload();
    }

    //// Events

    private void onQuizletServiceLoaded(Bundle savedInstanceState) {
        factory = createFactory(getQuizletService());
        restoreProviderIfNeeded(savedInstanceState);
        reload();
    }

    //// Creation Methods

    @Override
    protected BaseListAdaptor createAdapter() {
        return createSetAdapter();
    }

    private QuizletSetAdapter createSetAdapter() {
        QuizletSetAdapter adapter = new QuizletSetAdapter(new QuizletSetAdapter.Listener() {
            @Override
            public void onSetClicked(View view, QuizletSet set) {
                QuizletSetListFragment.this.getListener().onRowClicked(set);
            }

            @Override
            public void onMenuClicked(View view, QuizletSet set) {
                QuizletSetListFragment.this.getListener().onRowMenuClicked(set, view);
            }
        });

        return adapter;
    }

    @NonNull
    private QuizletSetListFactory createFactory(QuizletService service) {
        return new QuizletSetListFactory(service);
    }

    public CompareStrategyFactory<QuizletSet> createCompareStrategyFactory() {
        return new QuizletSetCompareStrategyFactory();
    }

    //// Setters

    public void setSortOrder(Preferences.SortOrder sortOrder) {
        Preferences.setQuizletSetSortOrder(sortOrder);
        setCompareStrategy(getCompareStrategyFactory().createStrategy(sortOrder));

        reload();
    }

    //// Getters

    // App Getters

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public QuizletService getQuizletService() {
        return getMainApplication().getQuizletService();
    }

    // Data Getters

    public Preferences.SortOrder getSortOrder() {
        return getCompareStrategy().getSortOrder();
    }

    private QuizletSetAdapter getSetAdapter() {
        return (QuizletSetAdapter)adapter;
    }

    // Cast Getters

    private QuizletSetCompareStrategyFactory getCompareStrategyFactory() {
        return (QuizletSetCompareStrategyFactory)compareStrategyFactory;
    }

    private SortOrderCompareStrategy getCompareStrategy() {
        return (SortOrderCompareStrategy)compareStrategy;
    }
}
