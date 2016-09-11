package preferencestorage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.alexeyglushkov.cachemanager.StorageEntry;
import com.example.alexeyglushkov.cachemanager.StorageMetadata;
import com.example.alexeyglushkov.cachemanager.StorageProvider;
import com.example.alexeyglushkov.dropboxservice.ContextProvider;

import java.util.List;

/**
 * Created by alexeyglushkov on 04.09.16.
 */
public class PreferenceStorageProvider implements StorageProvider {
    private String name;
    private ContextProvider contextProvider;

    public PreferenceStorageProvider(String name, ContextProvider contextProvider) {
        this.name = name;
        this.contextProvider = contextProvider;
    }

    @Override
    public Error put(String key, Object value, StorageMetadata metadata) {
        SharedPreferences.Editor editor = getWritePreference();
        if (value instanceof Integer) {
            editor.putInt(key, (int)value);
        } else if (value instanceof Long) {
            editor.putLong(key, (long)value);
        }

        editor.commit();
        return null;
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
    public Error remove(String key) {
        SharedPreferences.Editor editor = getWritePreference();
        editor.remove(key);
        editor.commit();

        return null;
    }

    @Override
    public Error getError() {
        return null;
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
    public Error removeAll() {
        return null;
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
