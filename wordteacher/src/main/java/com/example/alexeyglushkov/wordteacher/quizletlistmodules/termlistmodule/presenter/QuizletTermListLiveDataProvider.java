package com.example.alexeyglushkov.wordteacher.quizletlistmodules.termlistmodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.listmodule.ResourceListLiveDataProviderImp;


public class QuizletTermListLiveDataProvider extends ResourceListLiveDataProviderImp<QuizletTerm> {
    private long setId = -1;

    public QuizletTermListLiveDataProvider(Bundle bundle, QuizletRepository repository, long setId) {
        super(bundle, null);
        this.setId = setId;
        setResourceLiveDataProvider(repository.createQuizletTermAdapter(setId));
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
