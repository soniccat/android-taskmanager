package quizletfragments.terms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.R;

import listmodule.view.BaseListAdaptor;
import listmodule.view.BaseListFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizletTermListFragment extends BaseListFragment<QuizletTerm> {

    //// Creation, initialization, restoration

    public static QuizletTermListFragment create() {
        QuizletTermListFragment fragment = new QuizletTermListFragment();
        fragment.initialize();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    //// Creation Methods

    @Override
    protected BaseListAdaptor createAdapter() {
        return createTermAdapter();
    }

    private QuizletTermAdapter createTermAdapter() {
        QuizletTermAdapter adapter = new QuizletTermAdapter(new QuizletTermAdapter.Listener() {
            @Override
            public void onTermClicked(View view, QuizletTerm card) {
                QuizletTermListFragment.this.getPresenter().onRowClicked(card);
            }

            @Override
            public void onMenuClicked(View view, QuizletTerm card) {
                QuizletTermListFragment.this.getPresenter().onRowMenuClicked(card, view);
            }
        });

        return adapter;
    }
}
