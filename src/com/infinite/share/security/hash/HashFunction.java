package com.infinite.share.security.hash;

/********************************************************************************************************************************
 * The interface describing the operations that should be implemented by all Hash functions.
 ********************************************************************************************************************************/
public interface HashFunction
{
   /**
    * Hashes the specified string, and convert the result into an hexadecimal string. 
    * @param source The string to hash.
    * @return The hash bytes, as an hexadecimal string.
    */
   public String hashToHexaString(final String source);

   /**
    * Hashes the specified bytes, and convert the result into an hexadecimal string.
    * @param source The bytes to hash.
    * @return The hash bytes, as an hexadecimal string.
    */
   public String hashToHexaString(final byte source[]);

   /**
    * Hashes the specified string. 
    * @param source The string to hash.
    * @return The hash bytes.
    */
   public byte[] hash(final String source);

   /**
    * Hashes the specified string. 
    * @param source The bytes to hash.
    * @return The hash bytes.
    */
   public byte[] hash(final byte source[]);
}
