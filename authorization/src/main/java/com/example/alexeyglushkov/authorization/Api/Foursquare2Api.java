package com.example.alexeyglushkov.authorization.Api;

import com.example.alexeyglushkov.authorization.Tools.AccessTokenExtractor;
import com.example.alexeyglushkov.authorization.Tools.JsonTokenExtractor;
import com.example.alexeyglushkov.authorization.OAuth.OAuthConfig;

import junit.framework.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Foursquare2Api extends DefaultApi20
{
  private static final String AUTHORIZATION_URL = "https://foursquare.com/oauth2/authenticate?client_id=%s&response_type=code&redirect_uri=%s";

  @Override
  public String getAccessTokenEndpoint()
  {
    return "https://foursquare.com/oauth2/access_token?grant_type=authorization_code";
  }

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

    return String.format(AUTHORIZATION_URL, config.getApiKey(), callback);
  }

  @Override
  public AccessTokenExtractor getAccessTokenExtractor()
  {
    return new JsonTokenExtractor();
  }
}
