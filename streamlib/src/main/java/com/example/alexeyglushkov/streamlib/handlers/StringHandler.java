package com.example.alexeyglushkov.streamlib.handlers;

import com.example.alexeyglushkov.streamlib.convertors.Converter;

/**
 * Created by alexeyglushkov on 28.12.14.
 */
public interface StringHandler extends Converter {
    Object handleString(String data);
}
