package com.example.alexeyglushkov.wordteacher.listmodule;

import android.os.Bundle;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface StorableListLiveDataProvider<T> extends ListLiveDataProvider<T> {
    void store(Bundle bundle);
    void restore(Bundle bundle, Object context);
}
