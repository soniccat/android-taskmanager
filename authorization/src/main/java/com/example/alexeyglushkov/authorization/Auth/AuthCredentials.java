package com.example.alexeyglushkov.authorization.Auth;

import java.util.Date;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public interface AuthCredentials {
    boolean isValid();
    long getExpireTime();
    boolean isExpired();
}