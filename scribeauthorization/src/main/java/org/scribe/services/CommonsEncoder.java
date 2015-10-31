package org.scribe.services;

import android.util.Base64;

public class CommonsEncoder extends Base64Encoder
{

  @Override
  public String encode(byte[] bytes)
  {
    return Base64.encodeToString(bytes,Base64.DEFAULT);
  }

  @Override
  public String getType()
  {
    return "CommonsCodec";
  }

  public static boolean isPresent()
  {
    try
    {
      Class.forName("org.apache.commons.codec.binary.Base64");
      return true;
    }
    catch (ClassNotFoundException e)
    {
      return false;
    }
  }
}
