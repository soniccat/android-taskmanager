package com.example.alexeyglushkov.authorization.Api;

import android.util.Base64;

import com.example.alexeyglushkov.authorization.OAuth.OAuthConfig;
import com.example.alexeyglushkov.authorization.Tools.JsonTokenExtractor;
import com.example.alexeyglushkov.authorization.Tools.TokenExtractor;
import com.example.alexeyglushkov.authorization.requestbuilder.Verb;

import junit.framework.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by alexeyglushkov on 20.03.16.
 */
public class QuizletApi2 extends DefaultApi20 {

    private static final String AUTHORIZATION_URL = "https://quizlet.com/authorize?client_id=%s&response_type=code&scope=read%%20write_set&state=%d&redirect_uri=%s";

    @Override
    public String getAuthorizationUrl(OAuthConfig config)
    {
        Assert.assertNotNull(config.getCallback(), "Must provide a valid url as callback. Foursquare2 does not support OOB");
        String callback = null;
        try {
            callback = URLEncoder.encode(config.getCallback(), "UTF-8");
        } catch (UnsupportedEncodingException exception) {
            return null;
        }

        return String.format(AUTHORIZATION_URL, config.getApiKey(), new Random().nextInt() % 10000, callback);
    }

    @Override
    public String getAccessTokenEndpoint(OAuthConfig config) {
        return "https://api.quizlet.com/oauth/token";
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    public Map<String, String> getAccessTokenPostParameters(OAuthConfig config) {
        Map<String, String> parameters = new HashMap<>();
        String stringToEncode = config.getApiKey() + ":" + config.getApiSecret();
        String encodedString = Base64.encodeToString(stringToEncode.getBytes(), Base64.DEFAULT);
        parameters.put("Basic Authorization", encodedString);
        parameters.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        return parameters;
    }

    @Override
    public TokenExtractor getAccessTokenExtractor()
    {
        return new JsonTokenExtractor();
    }
}
