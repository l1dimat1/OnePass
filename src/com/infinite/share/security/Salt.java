package com.infinite.share.security;


/********************************************************************************************************************************
 * A utility class that aims at making use of SALTs easier.
 ********************************************************************************************************************************/
public final class Salt
{
   /**
    * Ensure that this class will never be instantiated / extended.
    */
   private Salt()
   {
   }

   /**
    * Generates a new, securely random SALT (byte array) of the specified length.
    * @param lengthInBytes The length (in bytes) of the SALT to generate.
    * @return A new, securely random SALT.
    */
   public static byte[] generateSalt(final int lengthInBytes)
   {
      return RandomKeyGenerator.generateKey(lengthInBytes);
   }

   /**
    * Generates a new, securely random 64bytes salt (byte array.)
    * @return A new, securely random SALT.
    */
   public static byte[] generateSalt()
   {
      return generateSalt(64);
   }

   /**
    * Applies a SALT on a given byte array.
    * @param bytes The byte array to be SALTed.
    * @param salt The SALT.
    * @return the SALTed byte array.
    */
   public static byte[] salt(final byte[] bytes, final byte[] salt)
   {
      final byte[] saltedBytes = new byte[bytes.length + salt.length];
      System.arraycopy(bytes, 0, saltedBytes, 0,            bytes.length);
      System.arraycopy(salt,  0, saltedBytes, bytes.length, salt.length);
      return saltedBytes;
   }
}
