package com.example.alexeyglushkov.cachemanager.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.alexeyglushkov.cachemanager.StorageEntry;
import com.example.alexeyglushkov.cachemanager.StorageMetadata;
import com.example.alexeyglushkov.cachemanager.Storage;
import com.example.alexeyglushkov.tools.ContextProvider;

import java.util.List;

/**
 * Created by alexeyglushkov on 04.09.16.
 */
public class PreferenceStorage implements Storage {
    private String name;
    private ContextProvider contextProvider;

    public PreferenceStorage(String name, ContextProvider contextProvider) {
        this.name = name;
        this.contextProvider = contextProvider;
    }

    @Override
    public void put(String key, Object value, StorageMetadata metadata) throws Exception {
        SharedPreferences.Editor editor = getWritePreference();
        if (value instanceof Integer) {
            editor.putInt(key, (int)value);
        } else if (value instanceof Long) {
            editor.putLong(key, (long)value);
        }

        editor.commit();
    }

    @Override
    public Object getValue(String key) {
        return getReadPreference().getAll().get(key);
    }

    @Override
    public StorageMetadata createMetadata() {
        return null;
    }

    @Override
    public StorageMetadata getMetadata(String key) {
        return null;
    }

    @Override
    public void remove(String key) throws Exception {
        SharedPreferences.Editor editor = getWritePreference();
        editor.remove(key);
        editor.commit();
    }

    @Override
    public StorageEntry getEntry(String key) {
        return null;
    }

    @Override
    public List<StorageEntry> getEntries() {
        return null;
    }

    @Override
    public void removeAll() throws Exception {
        SharedPreferences.Editor editor = getWritePreference();
        editor.clear();
        editor.commit();
    }

    ////

    private SharedPreferences.Editor getWritePreference() {
        return getContext().getSharedPreferences(getName(), Context.MODE_PRIVATE).edit();
    }

    private SharedPreferences getReadPreference() {
        return getContext().getSharedPreferences(getName(), Context.MODE_PRIVATE);
    }

    private Context getContext() {
        return contextProvider.getContext();
    }

    private String getName() {
        return name;
    }
}
