package com.example.alexeyglushkov.wordteacher.tools;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public final class LongTools {
    public static int compare(long lhs, long rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }
}
