package com.example.alexeyglushkov.authorization.Api;

import android.util.Base64;

import com.example.alexeyglushkov.authorization.OAuth.OAuthConfig;
import com.example.alexeyglushkov.authorization.OAuth.OAuthConstants;
import com.example.alexeyglushkov.authorization.Tools.JsonTokenExtractor;
import com.example.alexeyglushkov.authorization.Tools.TokenExtractor;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.authorization.requestbuilder.Verb;

import junit.framework.Assert;
import java.util.Locale;
import java.util.Random;

/**
 * Created by alexeyglushkov on 20.03.16.
 */
public class QuizletApi2 extends DefaultApi20 {
    @Override
    public String getAuthorizationUrl(OAuthConfig config)
    {
        String callback = getEncodedCallback(config);

        Assert.assertNotNull(config.getCallback(), "Callback mustn't be null");
        Assert.assertNotNull(config.getApiKey(), "ApiKey mustn't be null");
        Assert.assertNotNull(config.getApiSecret(), "ApiSecret mustn't be null");

        StringBuilder urlBuilder = new StringBuilder("https://quizlet.com/authorize");
        urlBuilder.append(String.format(Locale.US, "?client_id=%s",config.getApiKey()));
        urlBuilder.append("&response_type=code");
        urlBuilder.append("&scope=read%20write_set");
        urlBuilder.append(String.format(Locale.US, "&state=%d", getAuthUrlState()));
        urlBuilder.append(String.format(Locale.US, "&redirect_uri=%s", callback));

        return urlBuilder.toString();
    }

    private int getAuthUrlState() {
        return new Random().nextInt() % 10000;
    }

    public void fillAccessTokenConnectionBuilder(HttpUrlConnectionBuilder builder, OAuthConfig config, String code) {
        builder.setUrl("https://api.quizlet.com/oauth/token")
        .setVerb(Verb.POST);

        builder.addBodyParameter("grant_type", "authorization_code");
        builder.addBodyParameter(OAuthConstants.CODE, code);
        builder.addBodyParameter(OAuthConstants.REDIRECT_URI, config.getCallback());

        builder.addHeader("Authorization", getAuthHeader(config.getApiKey(), config.getApiSecret()));
    }

    private String getAuthHeader(String clentId, String secretKey) {
        String stringToEncode = clentId + ":" + secretKey;
        String encodedString = Base64.encodeToString(stringToEncode.getBytes(), Base64.DEFAULT);
        return "Basic " + encodedString;
    }

    @Override
    public TokenExtractor getAccessTokenExtractor()
    {
        return new JsonTokenExtractor();
    }
}
