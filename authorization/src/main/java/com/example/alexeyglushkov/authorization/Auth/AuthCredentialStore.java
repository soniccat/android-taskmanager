package com.example.alexeyglushkov.authorization.Auth;

import java.util.List;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public interface AuthCredentialStore {
    Error putCredentials(AuthCredentials credentials);
    AuthCredentials getCredentials(String key);
    List<AuthCredentials> getCredentials();
    Error removeCredentials(int id);
    Error getError();
}