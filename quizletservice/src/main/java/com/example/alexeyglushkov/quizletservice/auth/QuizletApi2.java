package com.example.alexeyglushkov.quizletservice.auth;

import android.support.annotation.NonNull;
import android.util.Base64;

import com.example.alexeyglushkov.authorization.Api.DefaultApi20;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.OAuth.OAuthConfig;
import com.example.alexeyglushkov.authorization.OAuth.OAuthConstants;
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials;
import com.example.alexeyglushkov.authorization.OAuth.Token;
import com.example.alexeyglushkov.authorization.Tools.JsonTokenExtractor;
import com.example.alexeyglushkov.authorization.Tools.TokenExtractor;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.authorization.requestbuilder.Verb;
import com.example.alexeyglushkov.tools.TimeTools;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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

        Assert.assertNotNull("Callback mustn't be null", config.getCallback());
        Assert.assertNotNull("ApiKey mustn't be null", config.getApiKey());
        Assert.assertNotNull("ApiSecret mustn't be null", config.getApiSecret());

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
    public OAuthCredentials createCredentials(String response) {
        OAuthCredentials credentials = null;

        try {
            credentials = parseResponse(response);
        } catch (JSONException ex) {

        }

        return credentials;
    }

    @NonNull
    private OAuthCredentials parseResponse(String response) throws JSONException {
        OAuthCredentials credentials;
        credentials = new OAuthCredentials();
        JSONObject jsonObject = new JSONObject(response);
        credentials.setAccessToken(jsonObject.getString("access_token"));
        credentials.setUserId(jsonObject.getString("user_id"));

        String scope = jsonObject.getString("scope");
        credentials.setScopes(scope.split(" "));

        int expiresIn = jsonObject.getInt("expires_in");
        credentials.setExpireTime(TimeTools.currentTimeSeconds() + expiresIn);
        return credentials;
    }

    @Override
    public void signCommand(ServiceCommand command, OAuthCredentials credentials) {
        String authString = "Bearer " + credentials.getAccessToken();
        command.getConnectionBulder().addHeader("Authorization", authString);
    }
}
