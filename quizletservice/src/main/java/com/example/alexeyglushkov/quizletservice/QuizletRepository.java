package com.example.alexeyglushkov.quizletservice;

import android.util.SparseArray;

import com.example.alexeyglushkov.authtaskmanager.BaseServiceTask;
import com.example.alexeyglushkov.cachemanager.Storage;
import com.example.alexeyglushkov.cachemanager.clients.RxCache;
import com.example.alexeyglushkov.cachemanager.clients.RxCacheAdapter;
import com.example.alexeyglushkov.cachemanager.clients.SimpleCache;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;
import com.example.alexeyglushkov.taskmanager.task.WeakRefList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
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

    private final static int LOAD_SETS_COMMAND_ID = 0;
    private final static int LOAD_TERMS_COMMAND_PREFIX = 1;
    private RepositoryCommandHolder commandHolder = new RepositoryCommandHolder();

    @NonNull
    private NonNullMutableLiveData<Resource<List<QuizletSet>>> sets
            = new NonNullMutableLiveData<>(new Resource<List<QuizletSet>>());

    public QuizletRepository(@NonNull QuizletService service, @NonNull Storage storage) {
        this.service = service;
        cache = new RxCacheAdapter(new SimpleCache(storage, 0));
        commandHolder.put(new BaseRepositoryCommand(LOAD_SETS_COMMAND_ID, sets));
    }

    //// Actions

    public RepositoryCommand loadSets(final ProgressListener progressListener) {
        Disposable disposable = loadSetsInternal(progressListener).subscribe(Functions.emptyConsumer(), Functions.emptyConsumer());
        return commandHolder.put(new DisposableRepositoryCommand(LOAD_SETS_COMMAND_ID, disposable, sets));
    }

    private Single<List<QuizletSet>> loadSetsInternal(final ProgressListener progressListener) {
        final Resource.State previousState = sets.getValue().state;
        setState(Resource.State.Loading);

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
                        setState(Resource.State.Loaded, sets);
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        setError(previousState, throwable);
                    }
                }).doOnDispose(new Action() {
                    @Override
                    public void run() {
                        setState(previousState);
                    }
                });
    }

    public RepositoryCommand restoreOrLoad(final ProgressListener progressListener) {
        Disposable disposable = restoreOrLoadInternal(progressListener).subscribe(Functions.emptyConsumer(), Functions.emptyConsumer());
        return commandHolder.put(new DisposableRepositoryCommand(LOAD_SETS_COMMAND_ID, disposable, sets));
    }

    private Single<List<QuizletSet>> restoreOrLoadInternal(final ProgressListener progressListener) {
        final Resource.State previousState = sets.getValue().state;
        setState(Resource.State.Loading);

        BaseServiceTask<List<QuizletSet>> task = BaseServiceTask.fromMaybe(cache.<List<QuizletSet>>getCachedValue("quizlet_sets"));
        return service.runCommand(task)
            .doOnSuccess(new Consumer<List<QuizletSet>>() {
                @Override
                public void accept(List<QuizletSet> quizletSets) {
                    setState(Resource.State.Restored, quizletSets);
                }
            }).onErrorResumeNext(new Function<Throwable, SingleSource<? extends List<QuizletSet>>>() {
                @Override
                public SingleSource<? extends List<QuizletSet>> apply(Throwable throwable) {
                    setState(previousState);
                    if (service.getAccount().isAuthorized()) {
                        return loadSetsInternal(progressListener);
                    }

                    return Single.error(throwable);
                }
            }).doOnDispose(new Action() {
                @Override
                public void run() throws Exception {
                    setState(previousState);
                }
            });
    }

    public RepositoryCommand loadTerms(int setId, final ProgressListener progressListener) {
        Disposable disposable = loadSetsInternal(progressListener).subscribe(Functions.emptyConsumer(), Functions.emptyConsumer());
        return commandHolder.put(new DisposableRepositoryCommand(LOAD_TERMS_COMMAND_PREFIX + setId, disposable, new Adapter(setId).getLiveData()));
    }

    //// Setters / Getters

    // Getters

    @NonNull
    public LiveData<Resource<List<QuizletSet>>> getLiveData() {
        return (LiveData<Resource<List<QuizletSet>>>)commandHolder.getLiveData(LOAD_SETS_COMMAND_ID);
    }

    @NonNull
    public LiveData<Resource<List<QuizletTerm>>> getTermListLiveData(int setId) {
        return (LiveData<Resource<List<QuizletTerm>>>)commandHolder.getLiveData(LOAD_TERMS_COMMAND_PREFIX + setId);
    }

    public List<QuizletSet> getSets() {
        return sets.getValue().data;
    }

    public List<QuizletTerm> getTerms() {
        List<QuizletTerm> terms = new ArrayList<>();
        for (QuizletSet set : getSets()) {
            terms.addAll(set.getTerms());
        }

        return terms;
    }

    // TODO: optimize
    public QuizletTerm getTerm(long termId) {
        QuizletTerm resultTerm = null;
        for (QuizletSet set : getSets()) {
            for (QuizletTerm term : set.getTerms()) {
                if (term.getId() == termId) {
                    resultTerm = term;
                    break;
                }
            }

            if (resultTerm != null) {
                break;
            }
        }

        return resultTerm;
    }

    public QuizletSet getSet(long id) {
        QuizletSet result = null;

        List<QuizletSet> setList = sets.getValue().data;
        if (setList != null) {
            for (QuizletSet set : setList) {
                if (set.getId() == id) {
                    result = set;
                    break;
                }
            }
        }

        return result;
    }

    // Setters

    private void setState(Resource.State newState) {
        sets.setValue(sets.getValue().resource(newState));
    }

    private void setState(Resource.State newState, List<QuizletSet> newSets) {
        sets.setValue(sets.getValue().resource(newState, newSets));
    }

    private void setError(Resource.State newState, Throwable newError) {
        sets.setValue(sets.getValue().resource(newState, newError));
    }

    // Inner Classes

    // QuizletSet liveData to QuizletTerm liveData
    private class Adapter {
        private static final long NO_ID = -1;

        private Resource<List<QuizletTerm>> aResource = new Resource<>();
        private long setId = NO_ID;

        public Adapter() {
        }

        public Adapter(long setId) {
            this.setId = setId;
        }

        public LiveData<Resource<List<QuizletTerm>>> getLiveData() {
            final MediatorLiveData<Resource<List<QuizletTerm>>> mediatorLiveData = new MediatorLiveData<>();
            mediatorLiveData.setValue(aResource);

            mediatorLiveData.addSource(QuizletRepository.this.getLiveData(), new Observer<Resource<List<QuizletSet>>>() {
                @Override
                public void onChanged(Resource<List<QuizletSet>> listResource) {
                    mediatorLiveData.setValue(buildFinalResource(listResource));
                }
            });

            return mediatorLiveData;
        }

        private Resource<List<QuizletTerm>> buildFinalResource(Resource<List<QuizletSet>> listResource) {
            ArrayList<QuizletTerm> terms = new ArrayList<>();
            if (listResource.data != null) {
                for (QuizletSet set : listResource.data) {
                    for (QuizletTerm term : set.getTerms()) {
                        long setId = term.getSetId();
                        if (setId == NO_ID || setId == this.setId) {
                            terms.add(term);
                        }
                    }
                }
            }

            aResource.update(terms);
            return aResource;
        }
    }

    interface RepositoryCommand<T> {
        int getCommandId();
        void cancel();
        @Nullable LiveData<T> getLiveData();
    }

    public static class BaseRepositoryCommand<T> implements RepositoryCommand<T> {
        private int id;
        private WeakReference<LiveData<T>> liveDataRef;

        public BaseRepositoryCommand(int id, LiveData<T> liveData) {
            this.id = id;
            this.liveDataRef = new WeakReference<>(liveData);
        }

        @Override
        public int getCommandId() {
            return id;
        }

        @Override
        public void cancel() {
        }

        @Override
        @Nullable
        public LiveData<T> getLiveData() {
            return liveDataRef.get();
        }
    }

    public static class DisposableRepositoryCommand<T> extends BaseRepositoryCommand<T> {
        private @NonNull Disposable disposable;

        public DisposableRepositoryCommand(int id, @NonNull Disposable disposable, LiveData<T> liveData) {
            super(id, liveData);
            this.disposable = disposable;
        }

        @Override
        public void cancel() {
            disposable.dispose();
        }
    }

    public static class RepositoryCommandHolder {
        private SparseArray<RepositoryCommand> map = new SparseArray<>();

        public RepositoryCommand put(@NonNull RepositoryCommand<?> cmd) {
            int cmdId = cmd.getCommandId();

            RepositoryCommand oldCmd = get(cmdId);
            if (oldCmd != null) {
                cancel(cmdId);
            }

            map.put(cmdId, cmd);
            return cmd;
        }

        @Nullable
        public RepositoryCommand<?> get(int id) {
            return map.get(id);
        }

        @NonNull
        public LiveData<?> getLiveData(int id) {
            RepositoryCommand<?> cmd = get(id);
            return cmd != null ? cmd.getLiveData() : null;
        }

        public void cancel(int id) {
            RepositoryCommand cmd = get(id);
            if (cmd != null) {
                cmd.cancel();
                map.remove(id);
            }
        }
    }
}
