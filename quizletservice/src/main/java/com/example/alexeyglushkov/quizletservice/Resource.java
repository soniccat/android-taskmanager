package com.example.alexeyglushkov.quizletservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Resource<T> {
    public enum State {
        Uninitialized,
        Restored,
        Loading, // for restoring too
        Loaded
    }

    @NonNull public final State state;
    @Nullable public final T data;
    @Nullable public final Throwable error;

    public Resource() {
        this.state = State.Uninitialized;
        this.data = null;
        this.error = null;
    }

    public Resource(@NonNull State status, @Nullable T data, @Nullable Throwable error) {
        this.state = status;
        this.data = data;
        this.error = error;
    }

    Resource<T> resource(@NonNull State newState) {
        return new Resource<>(newState, data, null);
    }

    Resource<T> resource(@NonNull State newState, @NonNull Throwable newError) {
        return new Resource<>(newState, data, newError);
    }

    Resource<T> resource(@NonNull State newState, @NonNull T newData) {
        return new Resource<>(newState, newData, null);
    }
}
