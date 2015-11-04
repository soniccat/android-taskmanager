package com.example.alexeyglushkov.authorization.Auth;

import java.util.Date;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public interface AuthCredentials {
    // to support multiple credentials for an authorizer
    int getId();
    void setId(int id);

    boolean isValid();
    long getExpireTime();
    boolean isExpired();
}
