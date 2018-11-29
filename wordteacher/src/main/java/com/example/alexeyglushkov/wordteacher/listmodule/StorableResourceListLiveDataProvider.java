package com.example.alexeyglushkov.wordteacher.listmodule;

import android.os.Bundle;

public interface StorableResourceListLiveDataProvider<T> extends ResourceListLiveDataProvider<T> {
    void store(Bundle bundle);
    void restore(Bundle bundle);
}
