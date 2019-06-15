package com.aglushkov.repository.command;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import io.reactivex.disposables.Disposable;

public class DisposableRepositoryCommand<T> extends BaseRepositoryCommand<T> {
    private @NonNull Disposable disposable;

    public DisposableRepositoryCommand(long id, @NonNull Disposable disposable, @NonNull LiveData<T> liveData) {
        super(id, liveData);
        this.disposable = disposable;
    }

    @Override
    public void cancel() {
        disposable.dispose();
    }
}
