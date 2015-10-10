package com.example.alexeyglushkov.streamlib.handlers;

import com.example.alexeyglushkov.streamlib.convertors.Convertor;

/**
 * Created by alexeyglushkov on 28.12.14.
 */
public interface StringHandler extends Convertor {
    Object handleString(String data);
}
