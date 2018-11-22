package com.example.alexeyglushkov.wordteacher.quizletlistmodules.termlistmodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import org.junit.Assert;

import java.util.List;

import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProviderFactory;

/**
 * Created by alexeyglushkov on 20.08.16.
 */
public class QuizletTermListProviderFactory implements StorableListProviderFactory<QuizletTerm> {
    private QuizletRepository service;

    //// Initialization

    public QuizletTermListProviderFactory(QuizletService service) {
        Assert.assertNotNull(service);
        this.service = service;
    }

    //// Overridden methods

    // StorableListProviderFactory

    @Override
    public StorableListProvider<QuizletTerm> createFromList(List<QuizletTerm> list) {
        return new QuizletTermListProvider(list);
    }

    @Override
    public StorableListProvider<QuizletTerm> createFromObject(Object obj) {
        StorableListProvider<QuizletTerm> result = null;

        if (obj instanceof QuizletSet) {
            QuizletSet set = (QuizletSet)obj;
            result = new QuizletSetTermListProvider(set);

        } else {
            Assert.fail("Unknown object");
        }

        return result;
    }

    @Override
    public StorableListProvider<QuizletTerm> restore(Bundle bundle) {
        StorableListProvider<QuizletTerm> result = null;

        if (QuizletTermListProvider.canRestore(bundle)) {
            result = new QuizletTermListProvider(bundle, service);

        } else if (QuizletSetTermListProvider.canRestore(bundle)) {
            result = new QuizletSetTermListProvider(bundle, service);

        } else {
            result = createDefault();
        }

        return result;
    }

    @Override
    public StorableListProvider<QuizletTerm> createDefault() {
        return new StorableListProvider<QuizletTerm>() {
            @Override
            public void store(Bundle bundle) {

            }

            @Override
            public void restore(Bundle bundle, Object context) {

            }

            @Override
            public List<QuizletTerm> getList() {
                return service.getTerms();
            }
        };
    }
}
