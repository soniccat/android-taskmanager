package com.example.alexeyglushkov.authorization.OAuth;

import org.junit.Assert;

import java.io.Serializable;

import androidx.annotation.NonNull;

/**
 * Represents an OAuth token (either request or access token) and its secret
 *
 * @author Pablo Fernandez
 */
public class Token implements Serializable
{
  private static final long serialVersionUID = 715000866082812683L;

  private final String token;
  private final String secret;
  private final String rawResponse;

  /**
   * Default constructor
   *
   * @param token token value. Can't be null.
   * @param secret token secret. Can't be null.
   */
  public Token(String token, String secret) {
    this(token, secret, null);
  }

  public Token(String token, String secret, String rawResponse) {
    Assert.assertNotNull(token);
    Assert.assertNotNull(secret);

    this.token = token;
    this.secret = secret;
    this.rawResponse = rawResponse;
  }

  public String getToken() {
    return token;
  }

  public String getSecret() {
    return secret;
  }

  public String getRawResponse() {
    if (rawResponse == null) {
      throw new IllegalStateException("This token object was not constructed by scribe and does not have a rawResponse");
    }
    return rawResponse;
  }

  public String getParameter(String parameter) {
    String value = null;
    for (String str : this.getRawResponse().split("&"))
    {
      if (str.startsWith(parameter + '='))
      {
        String [] part = str.split("=");
        if (part.length > 1) {
          value = part[1].trim();
        }
        break;
      }
    }
    return value;
  }

  @Override @NonNull
  public String toString() {
    return String.format("Token[%s , %s]", token, secret);
  }

  /**
   * Returns true if the token is empty (token = "", secret = "")
   */
  public boolean isEmpty() {
    return "".equals(this.token) && "".equals(this.secret);
  }

  /**
   * Factory method that returns an empty token (token = "", secret = "").
   *
   * Useful for two legged OAuth.
   */
  public static Token empty() {
    return new Token("", "");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Token that = (Token) o;
    return token.equals(that.token) && secret.equals(that.secret);
  }

  @Override
  public int hashCode() {
    return 31 * token.hashCode() + 31 * secret.hashCode();
  }
}
