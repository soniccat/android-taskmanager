package com.example.alexeyglushkov.authorization;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public interface AuthCredentialStore {
    Error putCredentials(String key, AuthCredentials credentials);
    AuthCredentials getCredentials(String key);
}
