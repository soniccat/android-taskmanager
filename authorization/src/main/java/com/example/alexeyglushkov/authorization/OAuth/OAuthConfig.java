package com.example.alexeyglushkov.authorization.OAuth;

import java.io.OutputStream;

/**
 * Parameter object that groups OAuth config values
 * 
 * @author Pablo Fernandez
 */
public class OAuthConfig
{
  private final String apiKey;
  private final String apiSecret;
  private final String callback;
  private final SignatureType signatureType;
  private final String scope;
  
  public OAuthConfig(String key, String secret)
  {
    this(key, secret, null, null, null);
  }

  public OAuthConfig(String key, String secret, String callback, SignatureType type, String scope)
  {
    this.apiKey = key;
    this.apiSecret = secret;
    this.callback = callback;
    this.signatureType = type;
    this.scope = scope;
  }

  public String getApiKey()
  {
    return apiKey;
  }

  public String getApiSecret()
  {
    return apiSecret;
  }

  public String getCallback()
  {
    return callback;
  }

  public SignatureType getSignatureType()
  {
    return signatureType;
  }

  public String getScope()
  {
    return scope;
  }

  public boolean hasScope()
  {
    return scope != null;
  }
}
