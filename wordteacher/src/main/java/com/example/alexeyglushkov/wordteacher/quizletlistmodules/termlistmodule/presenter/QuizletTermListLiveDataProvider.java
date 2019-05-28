package com.example.alexeyglushkov.wordteacher.quizletlistmodules.termlistmodule.presenter;

import android.os.Bundle;

import androidx.arch.core.util.Function;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.quizletservice.Resource;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategy;
import com.example.alexeyglushkov.wordteacher.listmodule.ResourceListLiveDataProviderImp;

import java.util.List;


public class QuizletTermListLiveDataProvider extends ResourceListLiveDataProviderImp<QuizletTerm> {
    private long setId = -1;

    public QuizletTermListLiveDataProvider(Bundle bundle, QuizletRepository repository, long setId) {
        super(bundle, null);
        this.setId = setId;
        setResourceLiveDataProvider(repository.createQuizletTermAdapter(setId));
    }

    public QuizletTermListLiveDataProvider(Bundle bundle, QuizletRepository repository) {
        super(bundle, null);
        this.setId = -1;
        setResourceLiveDataProvider(repository.createQuizletTermAdapter(-1));
    }

    @Override
    public void store(Bundle bundle) {
        super.store(bundle);
        bundle.putLong("setId", this.setId);
    }

    @Override
    public void restore(Bundle bundle) {
        super.restore(bundle);
        setId = bundle.getLong("setId");
    }
}
