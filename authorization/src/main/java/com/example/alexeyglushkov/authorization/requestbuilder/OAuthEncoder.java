package com.example.alexeyglushkov.authorization.requestbuilder;

import org.junit.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Pablo Fernandez
 */
public class OAuthEncoder
{
  private static String CHARSET = "UTF-8";
  private static final Map<String, String> ENCODING_RULES;

  static
  {
    Map<String, String> rules = new HashMap<String, String>();
    rules.put("*", "%2A");
    rules.put("+", "%20");
    rules.put("%7E", "~");
    ENCODING_RULES = Collections.unmodifiableMap(rules);
  }

  private OAuthEncoder(){}

  public static String encode(String plain)
  {
    Assert.assertNotNull(plain, "Cannot encode null object");
    String encoded = "";
    try
    {
      encoded = URLEncoder.encode(plain, CHARSET);
    }
    catch (UnsupportedEncodingException uee)
    {
      return null;
    }
    for(Map.Entry<String, String> rule : ENCODING_RULES.entrySet())
    {
      encoded = applyRule(encoded, rule.getKey(), rule.getValue());
    }
    return encoded;
  }

  private static String applyRule(String encoded, String toReplace, String replacement)
  {
    return encoded.replaceAll(Pattern.quote(toReplace), replacement);
  }

  public static String decode(String encoded)
  {
    Assert.assertNotNull(encoded, "Cannot decode null object");
    try
    {
      return URLDecoder.decode(encoded, CHARSET);
    }
    catch(UnsupportedEncodingException uee)
    {
      return null;
    }
  }
}
