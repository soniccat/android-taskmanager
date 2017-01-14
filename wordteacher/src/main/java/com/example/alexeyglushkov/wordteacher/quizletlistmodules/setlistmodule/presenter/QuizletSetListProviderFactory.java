package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import junit.framework.Assert;

import java.util.List;

import listmodule.StorableListProvider;
import listmodule.StorableListProviderFactory;

/**
 * Created by alexeyglushkov on 20.08.16.
 */
public class QuizletSetListProviderFactory implements StorableListProviderFactory<QuizletSet> {
    private QuizletService service;

    //// Initialization

    public QuizletSetListProviderFactory(QuizletService service) {
        Assert.assertNotNull(service);
        this.service = service;
    }

    //// Interface methods

    // StorableListProviderFactory

    @Override
    public StorableListProvider<QuizletSet> createFromList(List<QuizletSet> list) {
        return new QuizletSetListProvider(list);
    }

    @Override
    public StorableListProvider<QuizletSet> createFromObject(Object obj) {
        Assert.fail("Not supported");
        return null;
    }

    @Override
    public StorableListProvider<QuizletSet> restore(Bundle bundle) {
        StorableListProvider<QuizletSet> result = null;

        if (QuizletSetListProvider.canRestore(bundle)) {
            result = new QuizletSetListProvider(bundle, service);

        } else {
            result = createDefault();
        }

        return result;
    }

    @Override
    public StorableListProvider<QuizletSet> createDefault() {
        return new StorableListProvider<QuizletSet>() {
            @Override
            public void store(Bundle bundle) {
            }

            @Override
            public void restore(Bundle bundle, Object context) {
            }

            @Override
            public List<QuizletSet> getList() {
                return service.getSets();
            }
        };
    }
}
