package com.example.alexeyglushkov.wordteacher.listmodule;

import android.os.Bundle;

import java.util.List;

/**
 * Created by alexeyglushkov on 20.08.16.
 */
public interface StorableListLiveDataProviderFactory<T> {
    StorableListLiveDataProvider<T> restore(Bundle bundle);
}
