package com.example.alexeyglushkov.authorization.Tools;

import com.example.alexeyglushkov.authorization.OAuth.Token;

import org.junit.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonTokenExtractor implements TokenExtractor
{
  public Token extract(String response)
  {
    Token result = null;
    try {
      JSONObject jsonObject = new JSONObject(response);
      result = new Token(jsonObject.getString("access_token"), "", response);
    } catch (JSONException ex) {

    }

    return result;
  }

}