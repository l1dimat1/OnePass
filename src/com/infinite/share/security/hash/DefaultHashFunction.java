package com.infinite.share.security.hash;

import com.infinite.share.tools.ByteTools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/********************************************************************************************************************************
 * A default implementation of the HashFunction interface.
 * To hash a byte array, this implementation basically:
 *  > XOR the byte array against a random but permanent key
 *  > Apply a Java build-in hashing algorithm on it
 ********************************************************************************************************************************/
public abstract class DefaultHashFunction
{
   private final MessageDigest m_msgDigest;
   private final byte[]        m_permanentKey;

   /**
    * Instantiate a new hashing function, using the specified algorithm and permanent key.
    * @param algorithm The MessageDigest algorithm.
    * @param permanentKey The key on which each input will be XORed before hashing it.
    */
   protected DefaultHashFunction(final String algorithm, final byte permanentKey[]) throws NoSuchAlgorithmException
   {
      m_msgDigest    = MessageDigest.getInstance(algorithm);
      m_permanentKey = permanentKey;
   }

   /**
    * Instantiate a new hashing function, using the specified algorithm and a default permanent key.
    * @param algorithm The MessageDigest algorithm.
    */
   protected DefaultHashFunction(final String algorithm) throws NoSuchAlgorithmException
   {
      this(algorithm, new byte[] { -2, -39, 122, 41, -14, 56, -113, 101, 27, -29, -110, 115, -64, -51, 39, -67, 9, 21, -78, 45, -20, 123, -42, 84, 9, -79, -40, 23, 94, -85, 62, -17, -24, 120, -55, -5, 57, 53, 57, 41, -33, 96, 85, -73, -101, 15, -31, 72, 59, -12, 0, -1, -44, 116, -37, 58, 86, -49, -114, 46, -62, 3, -63, -57 });
   }

   /**
    * Hashes the specified string, and convert the result into an hexadecimal string. 
    * @param source The string to hash.
    * @return The hash bytes, as an hexadecimal string.
    */
   public final String hashToHexaString(final String source)
   {
      return ByteTools.toHexaString(hash(source));
   }

   /**
    * Hashes the specified bytes, and convert the result into an hexadecimal string.
    * @param source The bytes to hash.
    * @return The hash bytes, as an hexadecimal string.
    */
   public final String hashToHexaString(final byte source[])
   {
      return ByteTools.toHexaString(hash(source));
   }

   /**
    * Hashes the specified string. 
    * @param source The string to hash.
    * @return The hash bytes.
    */
   public final byte[] hash(final String source)
   {
      return hash(ByteTools.toUTF8Bytes(source));
   }

   /**
    * Hashes the specified string.
    * @param source The bytes to hash.
    * @return The hash bytes.
    */
   public final byte[] hash(final byte source[])
   {
      final byte xorBytes[] = new byte[Math.max(source.length, m_permanentKey.length)];

      for (int i = 0; i < xorBytes.length; i++)
         xorBytes[i] = (byte)(source[i % source.length] ^ m_permanentKey[i % m_permanentKey.length]);

      m_msgDigest.reset();
      m_msgDigest.update(xorBytes);

      return m_msgDigest.digest();
   }
}
