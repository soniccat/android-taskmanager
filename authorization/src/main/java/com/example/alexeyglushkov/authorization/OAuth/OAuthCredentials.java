package com.example.alexeyglushkov.authorization.OAuth;

import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.tools.TimeTools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public class OAuthCredentials implements AuthCredentials, Serializable {

    private static final long serialVersionUID = 165145426084589963L;

    private String accessToken;
    private String requestToken;
    private String refreshToken;
    private String userId;
    private String[] scopes = new String[]{};
    private long expireTime;

    // AuthCredentials implementation

    @Override
    public boolean isValid() {
        return accessToken != null && !isExpired();
    }

    @Override
    public long getExpireTime() {
        return expireTime;
    }

    @Override
    public boolean isExpired() {
        return expireTime != 0 && TimeTools.currentTimeSeconds() > expireTime;
    }

    // Getters / Setters

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String[] getScopes() {
        return scopes;
    }

    public void setScopes(String[] scopes) {
        this.scopes = scopes;
    }
}
