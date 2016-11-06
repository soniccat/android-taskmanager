package quizletfragments.sets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.wordteacher.R;

import listmodule.view.BaseListAdaptor;
import listmodule.view.BaseListFragment;

/**
 * Created by alexeyglushkov on 07.08.16.
 */
public class QuizletSetListFragment extends BaseListFragment<QuizletSet> {

    //// Creation, initialization, restoration

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

    //// Getter

    private QuizletSetAdapter getSetAdapter() {
        return (QuizletSetAdapter)adapter;
    }
}
