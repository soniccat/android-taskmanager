package com.example.alexeyglushkov.streamlib.progress;

/**
 * Created by alexeyglushkov on 23.10.16.
 */

public interface ProgressListener {
    void onProgressChanged(Object sender, ProgressInfo progressInfo);
}
