package org.scribe.model;

import org.scribe.oauth.OAuthWebClient;

import java.io.*;

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
  private final OutputStream debugStream;
  private final OAuthWebClient webClient;
  
  public OAuthConfig(String key, String secret)
  {
    this(key, secret, null, null, null, null, null);
  }

  public OAuthConfig(String key, String secret, String callback, SignatureType type, String scope, OAuthWebClient webClient, OutputStream stream)
  {
    this.apiKey = key;
    this.apiSecret = secret;
    this.callback = callback;
    this.signatureType = type;
    this.scope = scope;
    this.webClient = webClient;
    this.debugStream = stream;
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

  public OAuthWebClient getWebClient() {
    return webClient;
  }

  public boolean hasScope()
  {
    return scope != null;
  }

  public void log(String message)
  {
    if (debugStream != null)
    {
      message = message + "\n";
      try
      {
        debugStream.write(message.getBytes("UTF8"));
      }
      catch (Exception e)
      {
        throw new RuntimeException("there were problems while writing to the debug stream", e);
      }
    }
  }
}
