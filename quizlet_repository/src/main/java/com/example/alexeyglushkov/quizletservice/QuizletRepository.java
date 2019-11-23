package com.example.alexeyglushkov.quizletservice;

import com.aglushkov.repository.livedata.NonNullMutableLiveData;
import com.aglushkov.repository.livedata.Resource;
import com.aglushkov.repository.livedata.ResourceLiveDataProvider;
import com.example.alexeyglushkov.authtaskmanager.BaseServiceTask;
import com.example.alexeyglushkov.cachemanager.Storage;
import com.example.alexeyglushkov.cachemanager.clients.RxCache;
import com.example.alexeyglushkov.cachemanager.clients.RxCacheAdapter;
import com.example.alexeyglushkov.cachemanager.clients.SimpleCache;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;
import com.aglushkov.repository.command.DisposableRepositoryCommand;
import com.aglushkov.repository.command.RepositoryCommand;
import com.aglushkov.repository.RepositoryCommandHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.Functions;

// TODO: base class for repository with service and cache
public class QuizletRepository implements ResourceLiveDataProvider<List<QuizletSet>> {
    private @NonNull QuizletService service;
    private @NonNull RxCache cache;

    private final static long LOAD_SETS_COMMAND_ID = 0;
    private final static long LOAD_TERMS_COMMAND_PREFIX = 2; // it's 2 to support -1 set id
    private RepositoryCommandHolder commandHolder = new RepositoryCommandHolder();

    public QuizletRepository(@NonNull QuizletService service, @NonNull Storage storage) {
        this.service = service;
        cache = new RxCacheAdapter(new SimpleCache(storage, 0));
    }

    //// Actions

    public RepositoryCommand<Resource<List<QuizletSet>>> loadSets(final ProgressListener progressListener) {
        Disposable disposable = loadSetsInternal(progressListener).subscribe(Functions.emptyConsumer(), Functions.emptyConsumer());
        return commandHolder.putCommand(new DisposableRepositoryCommand<>(LOAD_SETS_COMMAND_ID, disposable, getSetsLiveData()));
    }

    private Single<List<QuizletSet>> loadSetsInternal(final ProgressListener progressListener) {
        final Resource<List<QuizletSet>> previousState = getSetsLiveData().getValue();
        getSetsLiveData().setValue(getSetsLiveData().getValue().toLoading());

        return service.loadSets(progressListener)
                .flatMap(new Function<List<QuizletSet>, SingleSource<? extends List<QuizletSet>>>() {
                    @Override
                    public SingleSource<? extends List<QuizletSet>> apply(final List<QuizletSet> sets) {
                        BaseServiceTask<List<QuizletSet>> task = BaseServiceTask.fromSingle(cache.putValue("quizlet_sets", sets).toSingleDefault(sets));
                        return service.runCommand(task);
                    }
                })
                .doOnSuccess(new Consumer<List<QuizletSet>>() {
                    @Override
                    public void accept(List<QuizletSet> sets) {
                        getSetsLiveData().setValue(getSetsLiveData().getValue().toLoaded(sets));
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        getSetsLiveData().setValue(getSetsLiveData().getValue().toError(throwable, true, previousState.data(), previousState.getCanLoadNextPage()));
                    }
                }).doOnDispose(new Action() {
                    @Override
                    public void run() {
                        getSetsLiveData().setValue(previousState);
                    }
                });
    }

    @NonNull
    public RepositoryCommand<Resource<List<QuizletSet>>> restoreOrLoad(final ProgressListener progressListener) {
        Disposable disposable = restoreOrLoadInternal(progressListener).subscribe(Functions.emptyConsumer(), Functions.emptyConsumer());
        return commandHolder.putCommand(new DisposableRepositoryCommand<>(LOAD_SETS_COMMAND_ID, disposable, getSetsLiveData()));
    }

    private Single<List<QuizletSet>> restoreOrLoadInternal(final ProgressListener progressListener) {
        final Resource previousState = getSetsLiveData().getValue();
        getSetsLiveData().setValue(previousState.toLoading());

        BaseServiceTask<List<QuizletSet>> task = BaseServiceTask.fromMaybe(cache.<List<QuizletSet>>getCachedValue("quizlet_sets"));
        return service.runCommand(task)
            .doOnSuccess(new Consumer<List<QuizletSet>>() {
                @Override
                public void accept(List<QuizletSet> quizletSets) {
                    getSetsLiveData().setValue(getSetsLiveData().getValue().toLoaded(quizletSets));
                }
            }).onErrorResumeNext(new Function<Throwable, SingleSource<? extends List<QuizletSet>>>() {
                @Override
                public SingleSource<? extends List<QuizletSet>> apply(Throwable throwable) {
                    getSetsLiveData().setValue(previousState);
                    if (service.getAccount().isAuthorized()) {
                        return loadSetsInternal(progressListener);
                    }

                    return Single.error(throwable);
                }
            }).doOnDispose(new Action() {
                @Override
                public void run() throws Exception {
                    getSetsLiveData().setValue(previousState);
                }
            });
    }

    //// Setters / Getters

    // Getters

    @NonNull
    public LiveData<Resource<List<QuizletSet>>> getLiveData() {
        return getSetsLiveData();
    }

    @NonNull
    public MutableLiveData<Resource<List<QuizletTerm>>> getTermListLiveData(long setId) {
        long liveDataId = LOAD_TERMS_COMMAND_PREFIX + setId;
        MutableLiveData<Resource<List<QuizletTerm>>> liveData = commandHolder.getLiveData(liveDataId);
        if (liveData == null) {
            liveData = new QuizletTermAdapter(setId).getLiveData();
            commandHolder.putLiveData(liveDataId, liveData);
        }
        return liveData;
    }

    @NonNull
    private NonNullMutableLiveData<Resource<List<QuizletSet>>> getSetsLiveData() {
        NonNullMutableLiveData<Resource<List<QuizletSet>>> liveData = commandHolder.getLiveData(LOAD_SETS_COMMAND_ID);
        if (liveData == null) {
            liveData = new NonNullMutableLiveData<>((Resource<List<QuizletSet>>)new Resource.Uninitialized<List<QuizletSet>>());
            commandHolder.putLiveData(LOAD_SETS_COMMAND_ID, liveData);
        }
        return liveData;
    }

    // Inner Classes

    // QuizletSet liveData to QuizletTerm liveData
    private class QuizletTermAdapter implements ResourceLiveDataProvider<List<QuizletTerm>> {
        private static final long NO_ID = -1;
        private long setId;

        public QuizletTermAdapter(long setId) {
            this.setId = setId;
        }

        public MutableLiveData<Resource<List<QuizletTerm>>> getLiveData() {
            final MediatorLiveData<Resource<List<QuizletTerm>>> mediatorLiveData = new MediatorLiveData<>();
            mediatorLiveData.setValue(new Resource.Uninitialized<List<QuizletTerm>>());

            mediatorLiveData.addSource(QuizletRepository.this.getLiveData(), new Observer<Resource<List<QuizletSet>>>() {
                @Override
                public void onChanged(Resource<List<QuizletSet>> listResource) {
                    mediatorLiveData.setValue(buildFinalResource(listResource));
                }
            });

            return mediatorLiveData;
        }

        private Resource<List<QuizletTerm>> buildFinalResource(Resource<List<QuizletSet>> listResource) {
            List<QuizletTerm> terms = new ArrayList<>();
            List<QuizletSet> data = listResource.data();
            if (data != null) {
                for (QuizletSet set : data) {
                    for (QuizletTerm term : set.getTerms()) {
                        long setId = term.getSetId();
                        if (this.setId == NO_ID || setId == this.setId) {
                            terms.add(term);
                        }
                    }
                }
            }

            return listResource.copyWith(terms);
        }
    }
}
