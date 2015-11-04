package com.example.alexeyglushkov.authorization.OAuth;

import junit.framework.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link AccessTokenExtractor}. Conforms to OAuth 2.0
 */
public class TokenExtractor20Impl implements AccessTokenExtractor, RequestTokenExtractor
{
  private static final Pattern TOKEN_REGEX = Pattern.compile("access_token=([^&]+)");
  private static final String EMPTY_SECRET = "";

  private Pattern customPattern = null;

  public TokenExtractor20Impl() {
  }

  public TokenExtractor20Impl(String tokenName) {
    customPattern = Pattern.compile(tokenName + "=([^&]+)");
  }

  /**
   * {@inheritDoc} 
   */
  public Token extract(String response)
  {
    Assert.assertNotNull(response, "Response body is incorrect. Can't extract a token from an empty string");

    Pattern pattern = customPattern == null ? TOKEN_REGEX : customPattern;
    Matcher matcher = pattern.matcher(response);
    if (matcher.find())
    {
      //String token = OAuthEncoder.decode(matcher.group(1));
      String token = null;
      try {
        token = URLDecoder.decode(matcher.group(1), "UTF-8");
      } catch(UnsupportedEncodingException ex) {

      }
      return new Token(token, EMPTY_SECRET, response);
    } 
    else
    {
      return null;
    }
  }
}
