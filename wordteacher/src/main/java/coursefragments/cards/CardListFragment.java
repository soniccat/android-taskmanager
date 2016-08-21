package coursefragments.cards;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.wordteacher.R;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import coursefragments.courses.CourseListProvider;
import listfragment.BaseListAdaptor;
import listfragment.BaseListFragment;
import listfragment.NullStorableListProvider;
import listfragment.StorableListProvider;
import listfragment.StorableListProviderFactory;
import main.MainApplication;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 30.07.16.
 */
public class CardListFragment extends BaseListFragment<Card> {

    //// Creation, initialization, restoration

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        MainApplication.instance.addCourseHolderListener(new MainApplication.ReadyListener() {
            @Override
            public void onReady() {
                onHolderLoaded(savedInstanceState);
            }
        });
    }

    private void restoreProviderIfNeeded(Bundle savedInstanceState) {
        if (provider instanceof NullStorableListProvider) {
            provider = factory.restore(savedInstanceState);
        }
    }

    //// Events

    private void onHolderLoaded(Bundle savedInstanceState) {
        factory = createFactory();
        restoreProviderIfNeeded(savedInstanceState);
        reload();
    }

    //// Actions

    public void reload() {
        setAdapterCards(getCards());
    }

    private List<Card> sortCards(List<Card> cards) {
        Collections.sort(cards, new Comparator<Card>() {
            @Override
            public int compare(Card lhs, Card rhs) {
                return lhs.getTerm().compareToIgnoreCase(rhs.getTerm());
            }
        });

        return cards;
    }

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
    private CardListFactory createFactory() {
        return new CardListFactory(getCourseHolder());
    }

    //// Setters

    // Data Setters

    public void setParentCourse(Course course) {
        provider = factory.createFromObject(course);
    }

    // UI Setters

    private void setAdapterCards(List<Card> inCards) {
        ArrayList<Card> cards = new ArrayList<>();

        if (inCards != null) {
            cards.addAll(inCards);
            sortCards(cards);
        }

        getCardAdapter().setCards(cards);
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

    public Course getParentCourse() {
        Course result = null;
        if (provider instanceof CourseCardListProvider) {
            CourseCardListProvider courseProvider = (CourseCardListProvider)provider;
            result = courseProvider.getCourse();
        }

        return result;
    }

    private List<Card> getCards() {
        return provider != null ? provider.getList() : null;
    }

    // Statuses

    public boolean hasCards() {
        List<Card> cards = getCards();
        int count = cards != null ? cards.size() : 0;
        return count > 0;
    }

    // Cast Getters

    private CardListAdapter getCardAdapter() {
        return (CardListAdapter)adapter;
    }
}
