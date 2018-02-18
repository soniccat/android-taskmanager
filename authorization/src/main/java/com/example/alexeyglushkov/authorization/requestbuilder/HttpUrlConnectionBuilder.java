package com.example.alexeyglushkov.authorization.requestbuilder;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Represents an HTTP Request object
 * 
 * @author Pablo Fernandez
 */
public class HttpUrlConnectionBuilder
{
  private static final String TAG = "HttpUrlCon...Builder";
  private static final String CONTENT_LENGTH = "Content-Length";
  private static final String CONTENT_TYPE = "Content-Type";
  public static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";

  private String url;
  private Verb verb;
  private ParameterList querystringParams;
  private ParameterList bodyParams;
  private Map<String, String> headers;
  private String payload = null;
  private String charset;
  private byte[] bytePayload = null;
  private boolean connectionKeepAlive = false;
  private boolean followRedirects = true;
  private Long connectTimeout = null;
  private Long readTimeout = null;

  public HttpUrlConnectionBuilder()
  {
    this.verb = Verb.GET;
    this.querystringParams = new ParameterList();
    this.bodyParams = new ParameterList();
    this.headers = new HashMap<String, String>();
  }

  public HttpUrlConnectionBuilder setUrl(String url) {
    this.url = url;
    return this;
  }

  public HttpUrlConnectionBuilder setVerb(Verb verb) {
        this.verb = verb;
        return this;
    }

    public HttpURLConnection build()
    {
      String completeUrl = getCompleteUrl();
      System.setProperty("http.keepAlive", connectionKeepAlive ? "true" : "false");
      HttpURLConnection connection = null;

      try {
        connection = (HttpURLConnection) new URL(completeUrl).openConnection();
        connection.setInstanceFollowRedirects(followRedirects);

        connection.setRequestMethod(this.verb.name());

        if (connectTimeout != null) {
          connection.setConnectTimeout(connectTimeout.intValue());
        }

        if (readTimeout != null) {
          connection.setReadTimeout(readTimeout.intValue());
        }
        addHeaders(connection);
        if (verb.equals(Verb.PUT) || verb.equals(Verb.POST)) {
            addBody(connection, getByteBodyContents());
        }

      } catch (Exception ex) {
        Log.d(TAG, "HttpUrlConnectionBuilder build exception: " + ex.getMessage());
      }

        return connection;
    }

  /**
   * Returns the complete url (host + resource + encoded querystring parameters).
   *
   * @return the complete url.
   */
  public String getCompleteUrl()
  {
    return querystringParams.appendTo(url);
  }

    HttpUrlConnectionBuilder addHeaders(HttpURLConnection conn)
    {
        for (String key : headers.keySet()) {
            conn.setRequestProperty(key, headers.get(key));
        }
        return this;
    }

  private void addBody(HttpURLConnection conn, byte[] content) throws IOException
  {
    conn.setRequestProperty(CONTENT_LENGTH, String.valueOf(content.length));

    // Set default content type if none is set.
    if (conn.getRequestProperty(CONTENT_TYPE) == null) {
      conn.setRequestProperty(CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
    }

    conn.setDoOutput(true);
    OutputStream os = conn.getOutputStream();
    os.write(content);
    os.close();
  }

  /**
   * Add an HTTP Header to the Request
   * 
   * @param key the header name
   * @param value the header value
   */
  public HttpUrlConnectionBuilder addHeader(String key, String value)
  {
    this.headers.put(key, value);
      return this;
  }

  /**
   * Add a body Parameter (for POST/ PUT Requests)
   * 
   * @param key the parameter name
   * @param value the parameter value
   */
  public HttpUrlConnectionBuilder addBodyParameter(String key, String value)
  {
      this.bodyParams.add(key, value);
      return this;
  }

  /**
   * Add a QueryString parameter
   *
   * @param key the parameter name
   * @param value the parameter value
   */
  public HttpUrlConnectionBuilder addQuerystringParameter(String key, String value)
  {
    this.querystringParams.add(key, value);
      return this;
  }

  /**
   * Add body payload.
   * 
   * This method is used when the HTTP body is not a form-url-encoded string,
   * but another thing. Like for example XML.
   * 
   * Note: The contents are not part of the OAuth signature
   * 
   * @param payload the body of the request
   */
  public HttpUrlConnectionBuilder addPayload(String payload)
  {
    this.payload = payload;
      return this;
  }

  /**
   * Overloaded version for byte arrays
   *
   * @param payload
   */
  public HttpUrlConnectionBuilder addPayload(byte[] payload)
  {
    this.bytePayload = payload.clone();
      return this;
  }

  /**
   * Get a {@link ParameterList} with the query string parameters.
   * 
   * @return a {@link ParameterList} containing the query string parameters.
   */
  public ParameterList getQueryStringParams()
  {
    try
    {
      ParameterList result = new ParameterList();
      String queryString = new URL(url).getQuery();
      result.addQuerystring(queryString);
      result.addAll(querystringParams);
      return result;
    }
    catch (MalformedURLException mue)
    {
      return null;
    }
  }

  /**
   * Obtains a {@link ParameterList} of the body parameters.
   * 
   * @return a {@link ParameterList}containing the body parameters.
   */
  public ParameterList getBodyParams()
  {
    return bodyParams;
  }

  /**
   * Obtains the URL of the HTTP Request.
   * 
   * @return the original URL of the HTTP Request
   */
  public String getStringUrl()
  {
    return url;
  }

  public URL getUrl() {
    try
    {
      return new URL(url);
    }
    catch (MalformedURLException mue)
    {
      return null;
    }
  }

  /**
   * Returns the URL without the default port and the query string part.
   * 
   * @return the OAuth-sanitized URL
   */
  public String getSanitizedUrl()
  {
	 if(url.startsWith("http://") && (url.endsWith(":80") || url.contains(":80/"))){
	   return url.replaceAll("\\?.*", "").replaceAll(":80", "");
	 }
	 else  if(url.startsWith("https://") && (url.endsWith(":443") || url.contains(":443/"))){
	   return url.replaceAll("\\?.*", "").replaceAll(":443", "");
	 }
	 else{
	   return url.replaceAll("\\?.*", "");
	 }
   }

  /**
   * Returns the body of the request
   * 
   * @return form encoded string
   */
  public String getBodyContents()
  {
    //try
    {
      return new String(getByteBodyContents(),getCharset());
    }
    /*catch(UnsupportedEncodingException uee)
    {
      return null;
    }*/
  }

  byte[] getByteBodyContents()
  {
    if (bytePayload != null) return bytePayload;
    String body = (payload != null) ? payload : bodyParams.asFormUrlEncodedString();
    //try
    {
      return body.getBytes(getCharset());
    }
    /*catch(UnsupportedEncodingException uee)
    {
      uee.printStackTrace();
      return null;
    }*/
  }

  /**
   * Returns the HTTP Verb
   * 
   * @return the verb
   */
  public Verb getVerb()
  {
    return verb;
  }
  
  /**
   * Returns the connection headers as a {@link Map}
   * 
   * @return map of headers
   */
  public Map<String, String> getHeaders()
  {
    return headers;
  }

  /**
   * Returns the connection charset. Defaults to {@link Charset} defaultCharset if not set
   *
   * @return charset
   */
  public Charset getCharset()
  {
    return charset == null ? Charset.forName("UTF-8") :  Charset.forName(charset);
  }

  /**
   * Sets the connect timeout for the underlying {@link HttpURLConnection}
   * 
   * @param duration duration of the timeout
   * 
   * @param unit unit of time (milliseconds, seconds, etc)
   */
  public HttpUrlConnectionBuilder setConnectTimeout(int duration, TimeUnit unit)
  {
    this.connectTimeout = unit.toMillis(duration);
      return this;
  }

  /**
   * Sets the read timeout for the underlying {@link HttpURLConnection}
   * 
   * @param duration duration of the timeout
   * 
   * @param unit unit of time (milliseconds, seconds, etc)
   */
  public HttpUrlConnectionBuilder setReadTimeout(int duration, TimeUnit unit)
  {
    this.readTimeout = unit.toMillis(duration);
      return this;
  }

  /**
   * Set the charset of the body of the request
   *
   * @param charsetName name of the charset of the request
   */
  public HttpUrlConnectionBuilder setCharset(String charsetName)
  {
    this.charset = charsetName;
      return this;
  }

  /**
   * Sets whether the underlying Http Connection is persistent or not.
   *
   * @see http://download.oracle.com/javase/1.5.0/docs/guide/net/http-keepalive.html
   * @param connectionKeepAlive
   */
  public HttpUrlConnectionBuilder setConnectionKeepAlive(boolean connectionKeepAlive)
  {
    this.connectionKeepAlive = connectionKeepAlive;
      return this;
  }

  /**
   * Sets whether the underlying Http Connection follows redirects or not.
   *
   * Defaults to true (follow redirects)
   *
   * @see http://docs.oracle.com/javase/6/docs/api/java/net/HttpURLConnection.html#setInstanceFollowRedirects(boolean)
   * @param followRedirects
   */
  public HttpUrlConnectionBuilder setFollowRedirects(boolean followRedirects)
  {
    this.followRedirects = followRedirects;
      return this;
  }

  @Override
  public String toString()
  {
    return String.format("@Request(%s %s)", getVerb(), getStringUrl());
  }
}
