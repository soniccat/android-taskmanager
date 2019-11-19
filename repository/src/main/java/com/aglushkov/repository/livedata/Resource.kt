package com.aglushkov.repository.livedata

class Resource<T> {
    enum class State {
        Uninitialized, Restored, Loading,  // for restoring too
        Loaded
    }

    var state: State
    var data: T?
    var error: Throwable?

    constructor() {
        state = State.Uninitialized
        data = null
        error = null
    }

    constructor(status: State, data: T?, error: Throwable?) {
        state = status
        this.data = data
        this.error = error
    }

    fun resource(newState: State): Resource<T?> {
        return Resource(newState, data, null)
    }

    fun resource(newData: T): Resource<T> {
        return Resource(state, newData, null)
    }

    fun resource(newState: State, newError: Throwable): Resource<T?> {
        return Resource(newState, data, newError)
    }

    fun resource(newState: State, newData: T): Resource<T> {
        return Resource(newState, newData, null)
    }

    fun update(newData: T): Resource<T> {
        data = newData
        return this
    }

    fun update(newState: State, newData: T?, newError: Throwable?): Resource<T> {
        state = newState
        data = newData
        error = newError
        return this
    }
}