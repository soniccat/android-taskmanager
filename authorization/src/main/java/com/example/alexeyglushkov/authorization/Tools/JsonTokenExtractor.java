package com.example.alexeyglushkov.authorization.Tools;

import com.example.alexeyglushkov.authorization.OAuth.Token;

import junit.framework.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonTokenExtractor implements AccessTokenExtractor
{
  private Pattern accessTokenPattern = Pattern.compile("\"access_token\":\\s*\"(\\S*?)\"");

  public Token extract(String response)
  {
    Assert.assertNotNull(response);
    Matcher matcher = accessTokenPattern.matcher(response);
    if(matcher.find()) {
      return new Token(matcher.group(1), "", response);
    } else {
      return null;
    }
  }

}