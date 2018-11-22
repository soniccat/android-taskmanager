package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import org.junit.Assert;

import java.util.List;

import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProviderFactory;

/**
 * Created by alexeyglushkov on 20.08.16.
 */
public class QuizletSetListProviderFactory implements StorableListProviderFactory<QuizletSet> {
    private QuizletRepository repository;

    //// Initialization

    public QuizletSetListProviderFactory(QuizletRepository repository) {
        Assert.assertNotNull(repository);
        this.repository = repository;
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
            result = new QuizletSetListProvider(bundle, repository);

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
                return repository.getSets();
            }
        };
    }
}
