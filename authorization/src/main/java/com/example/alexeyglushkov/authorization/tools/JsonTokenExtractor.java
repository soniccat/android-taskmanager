package com.example.alexeyglushkov.authorization.tools;

import com.example.alexeyglushkov.authorization.OAuth.Token;

import org.json.JSONException;
import org.json.JSONObject;

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