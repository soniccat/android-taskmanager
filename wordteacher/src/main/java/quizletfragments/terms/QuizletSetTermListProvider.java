package quizletfragments.terms;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.List;

import listfragment.SimpleListProvider;
import listfragment.StorableListProvider;
import listfragment.StorableListProviderFactory;

/**
 * Created by alexeyglushkov on 07.08.16.
 */
public class QuizletSetTermListProvider implements StorableListProvider<QuizletTerm> {
    private static final String PARENT_SET_ID = "PARENT_SET_ID";

    private QuizletSet set;

    //// Initialization

    public QuizletSetTermListProvider(QuizletSet set) {
        this.set = set;
    }

    public QuizletSetTermListProvider(Bundle bundle, QuizletService service) {
        restore(bundle, service);
    }

    //// Interfaces methods

    // StorableListProvider

    @Override
    public void store(Bundle bundle) {
        bundle.putLong(PARENT_SET_ID, set.getId());
    }

    static boolean canRestore(Bundle bundle) {
        return bundle != null && bundle.containsKey(PARENT_SET_ID);
    }

    @Override
    public void restore(Bundle bundle, Object context) {
        long setId = bundle.getLong(PARENT_SET_ID);

        QuizletService service = (QuizletService)context;
        set = service.getSet(setId);
    }

    // ListProvider

    @Override
    public List<QuizletTerm> getList() {
        return set.getTerms();
    }

    //// Getters

    public QuizletSet getSet() {
        return set;
    }
}
