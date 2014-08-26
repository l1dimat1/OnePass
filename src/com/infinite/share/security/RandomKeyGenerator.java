package com.infinite.share.security;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

/********************************************************************************************************************************
 * A utility class that allows generating securely random keys (bytes arrays.)
 ********************************************************************************************************************************/
public final class RandomKeyGenerator
{
   /**
    * Ensure that this class will never be instantiated / extended.
    */
   private RandomKeyGenerator()
   {
   }

   /**
    * Instantiate a new secure random, using the SHA1 algorithm from the SUN provider.
    * @return A new secure random.
    */
   public static SecureRandom newSecureRandom()
   {
      SecureRandom sr = null;
      try
      {
         sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
      }
      catch (final NoSuchProviderException e)
      {
         throw new RuntimeException(e); // Never happens, as per JRE's specifications
      }
      catch (final NoSuchAlgorithmException e)
      {
         throw new RuntimeException(e); // Never happens, as per JRE's specifications
      }
      return sr;
   }
   
   /**
    * Generates a new random key of the specified length (in bytes), using a new secure random instantiated with newSecureRandom().
    * @param lengthInBytes The length, in bytes, of the key to generate.
    * @return A new random key.
    */
   public static byte[] generateKey(final int lengthInBytes)
   {
      final byte[] key = new byte[lengthInBytes];
      newSecureRandom().nextBytes(key);

      return key;
   }
}
