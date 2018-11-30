package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.wordteacher.listmodule.ResourceListLiveDataProviderImp;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableResourceListLiveDataProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableResourceListLiveDataProviderFactory;

import org.junit.Assert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class QuizletSetListLiveDataProviderFactory implements StorableResourceListLiveDataProviderFactory<QuizletSet> {
    private @NonNull QuizletRepository repository;
    private @Nullable ResourceListLiveDataProviderImp.Filter<QuizletSet> filter;

    public QuizletSetListLiveDataProviderFactory(@NonNull QuizletRepository repository) {
        Assert.assertNotNull(repository);
        this.repository = repository;
    }

    @Override
    public StorableResourceListLiveDataProvider<QuizletSet> restore(Bundle bundle) {
        if (bundle != null) {
            return new QuizletSetListLiveDataProvider(bundle, repository);

        } else {
            return createDefault(bundle);
        }
    }

    public void setFilter(ResourceListLiveDataProviderImp.Filter<QuizletSet> filter) {
        this.filter = filter;
    }

    public StorableResourceListLiveDataProvider<QuizletSet> createDefault(Bundle bundle) {
        QuizletSetListLiveDataProvider provider = new QuizletSetListLiveDataProvider(repository);
        provider.setFilter(filter);
        return provider;
    }
}
