package com.example.alexeyglushkov.streamlib.convertors;

/**
 * Created by alexeyglushkov on 03.10.15.
 */
public interface Converter<F, T> {
    T convert(F object);
}