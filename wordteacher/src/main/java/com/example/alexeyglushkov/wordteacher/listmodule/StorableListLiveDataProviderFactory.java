package com.example.alexeyglushkov.wordteacher.listmodule;

import android.os.Bundle;

/**
 * Created by alexeyglushkov on 20.08.16.
 */
public interface StorableListLiveDataProviderFactory<T> {
    StorableResourceListLiveDataProvider<T> restore(Bundle bundle);
}
