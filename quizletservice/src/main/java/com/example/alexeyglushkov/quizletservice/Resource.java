package com.example.alexeyglushkov.quizletservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


// TODO: create MutableResource
public class Resource<T> {
    public enum State {
        Uninitialized,
        Restored,
        Loading, // for restoring too
        Loaded
    }

    // TODO: add getters
    @NonNull public State state;
    @Nullable public T data;
    @Nullable public Throwable error;

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

    // TODO: think about implementing recycle
    public Resource<T> resource(@NonNull State newState) {
        return new Resource<>(newState, data, null);
    }

    public Resource<T> resource(@NonNull T newData) {
        return new Resource<>(state, newData, null);
    }

    public Resource<T> resource(@NonNull State newState, @NonNull Throwable newError) {
        return new Resource<>(newState, data, newError);
    }

    public Resource<T> resource(@NonNull State newState, @NonNull T newData) {
        return new Resource<>(newState, newData, null);
    }

    public Resource<T> update(@NonNull T newData) {
        data = newData;
        return this;
    }

    public Resource<T> update(@NonNull State newState, @Nullable T newData, @Nullable Throwable newError) {
        state = newState;
        data = newData;
        error = newError;
        return this;
    }
}
