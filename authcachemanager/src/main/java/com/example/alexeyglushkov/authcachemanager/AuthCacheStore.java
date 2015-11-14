package com.example.alexeyglushkov.authcachemanager;

import com.example.alexeyglushkov.authorization.Auth.AuthCredentialStore;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.cachemanager.CacheEntry;
import com.example.alexeyglushkov.cachemanager.DiskCacheProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 07.11.15.
 */
public class AuthCacheStore extends DiskCacheProvider implements AuthCredentialStore {

    public AuthCacheStore(File directory) {
        super(directory);
    }

    @Override
    public Error putCredentials(AuthCredentials credentials) {
        return put(credentials.getId(), credentials, null);
    }

    @Override
    public AuthCredentials getCredentials(String key) {
        return (AuthCredentials)getValue(key);
    }

    @Override
    public List<AuthCredentials> getCredentials() {
        List<CacheEntry> entries = getEntries();
        List<AuthCredentials> credentials = new ArrayList<>();

        for (CacheEntry entry : entries) {
            credentials.add((AuthCredentials)entry.getObject());
        }

        return credentials;
    }

    @Override
    public Error removeCredentials(String id) {
        return remove(id);
    }
}
