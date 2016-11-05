package coursefragments.cards;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.wordteacher.R;

import java.util.List;

import listfragment.listmodule.view.BaseListAdaptor;
import listfragment.listmodule.view.BaseListFragment;
import listfragment.CompareStrategyFactory;
import listfragment.NullStorableListProvider;
import main.MainApplication;
import main.Preferences;
import model.Card;
import model.Course;
import model.CourseHolder;
import tools.SortOrderCompareStrategy;
import tools.Sortable;

/**
 * Created by alexeyglushkov on 30.07.16.
 */
public class CardListFragment extends BaseListFragment<Card> {

    //// Creation, initialization, restoration

    public static CardListFragment create() {
        CardListFragment fragment = new CardListFragment();
        fragment.initialize();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
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
}
