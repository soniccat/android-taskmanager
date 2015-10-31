package com.example.alexeyglushkov.authorization;

import java.util.Date;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public class OAuthCredentials implements AuthCredentials {
    private String accessToken;
    private String requestToken;
    private String refreshToken;
    private String consumerKey;
    private String secretKey;
    private long expireTime;

    // AuthCredentials implementation

    @Override
    public boolean isValid() {
        return accessToken != null;
    }

    @Override
    public long getExpireTime() {
        return expireTime;
    }

    @Override
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis() / 1000L;
        return currentTime > expireTime;
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

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
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

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
