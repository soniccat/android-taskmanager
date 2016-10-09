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

import listfragment.BaseListAdaptor;
import listfragment.BaseListFragment;
import listfragment.CompareStrategyFactory;
import listfragment.NullStorableListProvider;
import main.MainApplication;
import main.Preferences;
import tools.SortOrderCompareStrategy;
import tools.Sortable;

/**
 * Created by alexeyglushkov on 07.08.16.
 */
public class QuizletSetListFragment extends BaseListFragment<QuizletSet> implements Sortable, QuizletService.QuizletServiceListener {

    //// Creation, initialization, restoration

    private Bundle savedInstanceState;

    public static QuizletSetListFragment create() {
        QuizletSetListFragment fragment = new QuizletSetListFragment();
        fragment.initialize();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        getQuizletService().addListener(this);
        if (getQuizletService().getState() != QuizletService.State.Unitialized) {
            handleLoadedSets();
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
        getQuizletService().removeListener(this);
    }

    private void onQuizletServiceLoaded() {
        handleLoadedSets();
    }

    @Override
    public void onLoadError(QuizletService service, Error error) {

    }

    //// Actions

    private void handleLoadedSets() {
        restoreIfNeeded();
        reload();
    }

    //// Interface methods

    // QuizletService.QuizletServiceListener

    @Override
    public void onLoaded(QuizletService service) {
        onQuizletServiceLoaded();
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
    protected QuizletSetListFactory createProviderFactory() {
        return new QuizletSetListFactory(getQuizletService());
    }

    public CompareStrategyFactory<QuizletSet> createCompareStrategyFactory() {
        return new QuizletSetCompareStrategyFactory();
    }

    //// Setters

    public void setSortOrder(Preferences.SortOrder sortOrder) {
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
