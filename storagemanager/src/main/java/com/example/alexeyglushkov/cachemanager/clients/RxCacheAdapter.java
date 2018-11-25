package com.example.alexeyglushkov.cachemanager.clients;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;

public class RxCacheAdapter implements RxCache {
    private Cache cache;

    public RxCacheAdapter(Cache cache) {
        this.cache = cache;
    }

    @Override
    public Completable putValue(@NonNull final String key, @NonNull final Object value) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                try {
                    cache.putValue(key, value);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    @Override
    public <T> Maybe<T> getCachedValue(@NonNull final String cacheKey) {
        return Maybe.create(new MaybeOnSubscribe<T>() {
            @Override
            public void subscribe(MaybeEmitter<T> emitter) {
                try {
                    T obj = cache.getCachedValue(cacheKey);
                    if (obj != null) {
                        emitter.onSuccess(obj);
                    } else {
                        emitter.onComplete();
                    };
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    @Override
    public Cache.CacheMode getCacheMode() {
        return cache.getCacheMode();
    }

    @Override
    public void setCacheMode(Cache.CacheMode mode) {
        cache.setCacheMode(mode);
    }
}
