package quizletfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.R;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import listfragment.BaseListAdaptor;
import listfragment.BaseListFragment;
import listfragment.StorableListProvider;
import listfragment.StorableListProviderFactory;
import main.MainApplication;
import main.Preferences;
import model.Course;

/**
 * Created by alexeyglushkov on 07.08.16.
 */
public class QuizletSetListFragment extends BaseListFragment<QuizletSet> implements QuizletSortable {
    public static final String STORE_SET_IDS = "STORE_SET_IDS";

    private Preferences.SortOrder sortOrder = Preferences.getQuizletSetSortOrder();

    private StorableListProviderFactory<QuizletSet> factory;
    private StorableListProvider<QuizletSet> provider;

    //// Creation, initialization, restoration

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        factory = new QuizletSetListFactory(getQuizletService());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveProvider(outState);
    }

    private void saveProvider(Bundle outState) {
        provider.store(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null || provider == null) {
            getMainApplication().addQuizletServiceListener(new MainApplication.ReadyListener() {
                @Override
                public void onReady() {
                    onQuizletServiceLoaded(savedInstanceState);
                }
            });
        } else {
            reload();
        }
    }

    //// Events

    private void onQuizletServiceLoaded(Bundle savedInstanceState) {
        provider = factory.restore(savedInstanceState);
        reload();
    }

    //// Actions

    public void reload() {
        setAdapterTerms(getSets());
    }

    private List<QuizletSet> sortSets(List<QuizletSet> sets) {
        Collections.sort(sets, new Comparator<QuizletSet>() {
            @Override
            public int compare(QuizletSet lhs, QuizletSet rhs) {
                return compareSets(lhs, rhs);
            }
        });

        return sets;
    }

    private int compareSets(QuizletSet lhs, QuizletSet rhs) {
        switch (sortOrder) {
            case BY_NAME: return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
            case BY_NAME_INV: return rhs.getTitle().compareToIgnoreCase(lhs.getTitle());
            case BY_CREATE_DATE: return compare(lhs.getCreateDate(), rhs.getCreateDate());
            case BY_CREATE_DATE_INV: return compare(rhs.getCreateDate(), lhs.getCreateDate());
        }

        return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
    }

    public static int compare(long lhs, long rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }

    //// Creation methods

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

    //// Setters

    public void setSortOrder(Preferences.SortOrder sortOrder) {
        Preferences.setQuizletSetSortOrder(sortOrder);

        this.sortOrder = sortOrder;
        reload();
    }

    public void setSets(List<QuizletSet> sets) {
        provider = factory.createFromList(sets);
    }

    private void setAdapterTerms(List<QuizletSet> inSets) {
        List<QuizletSet> sets = new ArrayList<>();
        if (inSets != null) {
            sets.addAll(inSets);
            sortSets(sets);
        }

        getSetAdapter().setSets(sets);
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
        return sortOrder;
    }

    private QuizletSetAdapter getSetAdapter() {
        return (QuizletSetAdapter)adapter;
    }

    public List<QuizletSet> getSets() {
        return provider != null ? provider.getList() : null;
    }

    // Statuses

    public boolean hasSets() {
        List<QuizletSet> sets = getSets();
        int count = sets != null ? sets.size() : 0;
        return count > 0;
    }
}
