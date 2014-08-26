package com.infinite.share.security;

import com.infinite.share.tools.ByteTools;

import java.util.Random;

/**
 * Utility class that handles padding.
 */
public class RandomPadding
{
   private static final byte FIRST_BYTE = (byte)0xff;
   
   /**
    * Adds a random padding to the input bytes array. Returns the padded bytes array.
    * Padding is done by adding a random number of random bytes at the head of the bytes array, then adding, on 2 bytes, the number of padding bytes, again at
    * the head of the bytes array.
    * The returned bytes arrays follows the format 0xFF[len][padding][original bytes], where [len] is on 2 bytes and [padding] is on len> bytes.
    * Note that the minLen and maxLen are casted to short.
    * @bytes Input bytes, to be padded.
    * @minLen The minimum length after padding
    * @maxLen The maximum length after padding
    * @return The padded bytes array.
    */
   public static byte[] pad(final byte[] bytes, final int minLen, final int maxLen)
   {
      if (maxLen < minLen)
         throw new IllegalArgumentException("maxLen must be equal or greater than minLen.");
      if (minLen < (bytes.length + 3))
         throw new IllegalArgumentException("minLen and maxLen must be longer than the input bytes array.");
      
      final Random rnd = RandomKeyGenerator.newSecureRandom();
      final short len = (short)((minLen + rnd.nextInt(maxLen - minLen + 1)) - 1 - 2 - bytes.length);
      
      final byte[] padding = new byte[len];
      rnd.nextBytes(padding);
      
      final byte[] output = new byte[1 + 2 + len + bytes.length];
      
      output[0] = FIRST_BYTE;
      System.arraycopy(ByteTools.shortToBytes(len), 0, output, 1,           2);
      System.arraycopy(padding,                     0, output, 1 + 2,       len);
      System.arraycopy(bytes,                       0, output, 1 + 2 + len, bytes.length);

      return output;
   }

   public static byte[] unpad(final byte[] paddedBytes)
   {
      if ((paddedBytes != null) && (paddedBytes.length >= 3) && (paddedBytes[0] == FIRST_BYTE))
      {         
         final byte[] lenBytes = new byte[2];
         System.arraycopy(paddedBytes, 1, lenBytes, 0, 2);
         
         final short len = ByteTools.shortFromBytes(lenBytes);
   
         final byte[] bytes = new byte[paddedBytes.length - 1 - 2 - len];
   
         System.arraycopy(paddedBytes, 1 + 2 + len, bytes, 0, paddedBytes.length - 1 - 2 - len);
         
         return bytes;
      }
      return paddedBytes;
   }
}
