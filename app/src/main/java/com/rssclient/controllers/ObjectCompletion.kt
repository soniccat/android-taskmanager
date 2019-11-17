package com.rssclient.controllers

interface ObjectCompletion<T> {
    fun completed(result: T)
}