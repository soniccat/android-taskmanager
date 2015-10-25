package org.scribe.services;

public class DatatypeConverterEncoder extends Base64Encoder
{
  @Override
  public String encode(byte[] bytes)
  {
    return new String(bytes);
  }

  @Override
  public String getType()
  {
    return "DatatypeConverter";
  }
}
