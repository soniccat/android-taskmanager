package com.example.alexeyglushkov.authorization.Api;

import com.example.alexeyglushkov.authorization.OAuth.OAuthConstants;
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials;
import com.example.alexeyglushkov.authorization.Tools.JsonTokenExtractor;
import com.example.alexeyglushkov.authorization.OAuth.OAuthConfig;
import com.example.alexeyglushkov.authorization.Tools.TokenExtractor;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.authorization.requestbuilder.Verb;

import org.junit.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

// TODO: refactor as QuizletApi
public class Foursquare2Api extends DefaultApi20
{
  private static final String AUTHORIZATION_URL = "https://foursquare.com/oauth2/authenticate?client_id=%s&response_type=code&redirect_uri=%s";

  @Override
  public String getAuthorizationUrl(OAuthConfig config)
  {
    String callback = getEncodedCallback(config);

    Assert.assertNotNull(config.getCallback(), "Must provide a valid url as callback.");

    return String.format(AUTHORIZATION_URL, config.getApiKey(), callback);
  }

  public void fillAccessTokenConnectionBuilder(HttpUrlConnectionBuilder builder, OAuthConfig config, String code) {
    builder.setUrl("https://foursquare.com/oauth2/access_token?grant_type=authorization_code");

    builder.addQuerystringParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
    builder.addQuerystringParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
    builder.addQuerystringParameter(OAuthConstants.CODE, code);
    builder.addQuerystringParameter(OAuthConstants.REDIRECT_URI, config.getCallback());

    if (config.hasScope()) {
      builder.addQuerystringParameter(OAuthConstants.SCOPE, config.getScope());
    }
  }

  @Override
  public OAuthCredentials createCredentials(String response) {
    // TODO: implement
    return null;
  }
}
