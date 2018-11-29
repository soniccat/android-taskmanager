package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.wordteacher.listmodule.ListLiveDataProviderFromResource;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableListLiveDataProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableListLiveDataProviderFactory;

import org.junit.Assert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class QuizletSetListLiveDataProviderFactory implements StorableListLiveDataProviderFactory<QuizletSet> {
    private @NonNull QuizletRepository repository;
    private @Nullable ListLiveDataProviderFromResource.Filter<QuizletSet> filter;

    public QuizletSetListLiveDataProviderFactory(@NonNull QuizletRepository repository) {
        Assert.assertNotNull(repository);
        this.repository = repository;
    }

    @Override
    public StorableListLiveDataProvider<QuizletSet> restore(Bundle bundle) {
        if (bundle != null) {
            return new QuizletSetListLiveDataProvider(bundle, repository);

        } else {
            return createDefault(bundle);
        }
    }

    public void setFilter(ListLiveDataProviderFromResource.Filter<QuizletSet> filter) {
        this.filter = filter;
    }

    public StorableListLiveDataProvider<QuizletSet> createDefault(Bundle bundle) {
        QuizletSetListLiveDataProvider provider = new QuizletSetListLiveDataProvider(repository);
        provider.setFilter(filter);
        return provider;
    }
}
