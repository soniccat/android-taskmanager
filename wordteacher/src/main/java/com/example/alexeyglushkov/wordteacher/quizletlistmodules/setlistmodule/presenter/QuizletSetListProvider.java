package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import java.util.ArrayList;
import java.util.List;

import com.example.alexeyglushkov.wordteacher.listmodule.SimpleStorableListProvider;

/**
 * Created by alexeyglushkov on 20.08.16.
 */
public class QuizletSetListProvider extends SimpleStorableListProvider<QuizletSet> {
    private static final String STORE_SET_IDS = "STORE_SET_IDS";

    //// Initialization

    public QuizletSetListProvider(List<QuizletSet> items) {
        super(items);
    }

    public QuizletSetListProvider(Bundle bundle, QuizletService service) {
        super(null);
        restore(bundle, service);
    }

    //// Overridden methods

    // SimpleStorableListProvider

    @Override
    public void store(Bundle bundle) {
        bundle.putLongArray(STORE_SET_IDS, getIdArray());
    }

    public static boolean canRestore(Bundle bundle) {
        return bundle != null && bundle.containsKey(STORE_SET_IDS);
    }

    @Override
    public void restore(Bundle bundle, Object context) {
        long[] ids = bundle.getLongArray(STORE_SET_IDS);
        items = getQuizletSets(ids, (QuizletService)context);
    }

    //// Getters

    private long[] getIdArray() {
        List<QuizletSet> list = getList();
        long[] ids = new long[list.size()];

        for (int i=0; i<list.size(); ++i) {
            ids[i] = list.get(i).getId();
        }

        return ids;
    }

    private List<QuizletSet> getQuizletSets(long[] ids, QuizletService service) {
        List<QuizletSet> sets = new ArrayList<>();
        for (long id : ids) {
            QuizletSet set = service.getSet(id);
            sets.add(set);
        }

        return sets;
    }
}
