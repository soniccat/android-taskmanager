package com.example.alexeyglushkov.wordteacher.quizletlistmodules.termlistmodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.ArrayList;
import java.util.List;

import listmodule.SimpleStorableListProvider;

/**
 * Created by alexeyglushkov on 07.08.16.
 */
public class QuizletTermListProvider extends SimpleStorableListProvider<QuizletTerm> {
    public static final String STORE_TERM_IDS = "STORE_TERM_IDS";

    //// Initialization

    public QuizletTermListProvider(List<QuizletTerm> items) {
        super(items);
    }

    public QuizletTermListProvider(Bundle bundle, Object context) {
        super(null);
        restore(bundle, context);
    }

    //// Interface methods

    // SimpleListProvider

    @Override
    public void store(Bundle bundle) {
        bundle.putLongArray(STORE_TERM_IDS, getIdArray());
    }

    static boolean canRestore(Bundle bundle) {
        return bundle != null && bundle.containsKey(STORE_TERM_IDS);
    }

    @Override
    public void restore(Bundle bundle, Object context) {
        long[] ids = bundle.getLongArray(STORE_TERM_IDS);
        items = getQuizletTerms(ids, (QuizletService) context);
    }

    //// Getters

    private long[] getIdArray() {
        List<QuizletTerm> terms = getList();
        long[] ids = new long[terms.size()];

        for (int i=0; i<terms.size(); ++i) {
            ids[i] = terms.get(i).getId();
        }

        return ids;
    }

    private List<QuizletTerm> getQuizletTerms(long[] ids, QuizletService service) {
        List<QuizletTerm> terms = new ArrayList<>();
        for (long id : ids) {
            QuizletTerm term = service.getTerm(id);
            terms.add(term);
        }

        return terms;
    }
}
