package com.example.alexeyglushkov.streamlib.progress;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public interface ProgressInfo {
    float getCurrentValue();
    float getNormalizedValue();
    boolean isCancelled();
}
