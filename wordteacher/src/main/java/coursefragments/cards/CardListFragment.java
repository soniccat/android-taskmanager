package coursefragments.cards;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.wordteacher.R;

import listmodule.view.SimpleListAdaptor;
import listmodule.view.SimpleListFragment;
import model.Card;

/**
 * Created by alexeyglushkov on 30.07.16.
 */
public class CardListFragment extends SimpleListFragment<Card> {

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
    protected SimpleListAdaptor createAdapter() {
        return createCardAdapter();
    }

    private CardListAdapter createCardAdapter() {
        CardListAdapter adapter = new CardListAdapter(new CardListAdapter.Listener() {
            @Override
            public void onCardClicked(View view, Card card) {
                CardListFragment.this.getPresenter().onRowClicked(card);
            }

            @Override
            public void onMenuClicked(View view, Card card) {
                CardListFragment.this.getPresenter().onRowMenuClicked(card, view);
            }

            @Override
            public void onCardViewDeleted(View view, Card card) {
                CardListFragment.this.getPresenter().onRowViewDeleted(card);
            }
        });

        return adapter;
    }
}
