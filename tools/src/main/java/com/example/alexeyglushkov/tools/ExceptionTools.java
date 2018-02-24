package com.example.alexeyglushkov.tools;

/**
 * Created by alexeyglushkov on 24.02.18.
 */

public class ExceptionTools {
    public static void throwIfNull(Object obj, String message) throws NullPointerException {
        if(obj == null) {
            throw new NullPointerException(message);
        }
    }
}
