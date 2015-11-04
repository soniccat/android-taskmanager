package com.example.alexeyglushkov.authorization.Tools;

import com.example.alexeyglushkov.authorization.OAuth.Token;

/**
 * Simple command object that extracts a {@link Token} from a String
 * 
 * @author Pablo Fernandez
 */
public interface RequestTokenExtractor
{
  /**
   * Extracts the request token from the contents of an Http Response
   *  
   * @param response the contents of the response
   * @return OAuth access token
   */
  public Token extract(String response);
}
