package com.infinite.share.tools;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/********************************************************************************************************************************
 * A utility class that provides helper functions to manipulate and convert byte arrays.
 ********************************************************************************************************************************/
public class ByteTools          
{
   /**
    * Converts a short to a byte array.
    * @param s The short to convert.
    * @return The byte array.
    */
   public static byte[] shortToBytes(final short s)
   {
      return ByteBuffer.allocate(2).putShort(s).array();
   }

   /**
    * Converts a byte array to a short value.
    * @param b The byte array.
    * @return The short value.
    */
   public static short shortFromBytes(final byte[] b)
   {
      return ByteBuffer.wrap(b).getShort();
   }
   
   /**
    * Converts a long to a byte array.
    * @param l The long to convert.
    * @return The byte array.
    */
   public static byte[] longToBytes(final long l)
   {
      return ByteBuffer.allocate(8).putLong(l).array();
   }

   /**
    * Converts a byte array to a long value.
    * @param b The byte array.
    * @return The long value.
    */
   public static long longFromBytes(final byte[] b)
   {
      return ByteBuffer.wrap(b).getLong();
   }

   /**
    * Converts a byte array into its hexadecimal representation.
    * @param bytes The original byte array.
    * @return The hexadecimal representation of the byte array.
    */
   public static String toHexaString(final byte bytes[])
   {
      final StringBuffer sb = new StringBuffer();

      for (final byte b: bytes)
         sb.append(String.format("%02X", b));

      return sb.toString();
   }

   /**
    * Converts and hexadecimal string to a byte array.
    * @param h The hexadecimal representation of the byte array.
    * @return The original byte array.
    */
   public static byte[] fromHexaString(final String h)
   {
      if ((h.length() % 2) != 0)
         throw new IllegalArgumentException("Invalid hexadecimal representation of a byte array: " + h);
      
      final byte[] bytes = new byte[h.length() / 2];
      for (int i = 0; i < bytes.length; i++)
         bytes[i] = (byte) ((Character.digit(h.charAt(2 * i), 16) << 4) + Character.digit(h.charAt(2 * i + 1), 16));
      
      return bytes;
   }

   /**
    * Converts a string into its UTF8 bytes representation.
    * @param s The original string.
    * @return The UTF8 bytes representation of the string.
    */
   public static byte[] toUTF8Bytes(final String s)
   {
      byte utf8Bytes[] = null;
      try
      {
         utf8Bytes = s.getBytes("UTF-8");
      }
      catch (final UnsupportedEncodingException e)
      {
         throw new RuntimeException(e); // Never happens, as per JRE's specifications
      }
      return utf8Bytes;
   }

   /**
    * Converts an array of UTF8 bytes into a string.
    * @param bytes The UTF bytes.
    * @return The original string.
    */
   public static String fromUTF8Bytes(final byte bytes[])
   {
      String text = null;
      try
      {
         text = new String(bytes, "UTF-8");
      }
      catch (final UnsupportedEncodingException e)
      {
         throw new RuntimeException(e); // Never happens, as per JRE's specifications
      }
      return text;
   }

   
   /*-------------------------------------------------------------*/
   public static int nextPrime(final int i)
   /*-------------------------------------------------------------*/
   {  
      int primeNb = 0, counter = i;
      while (primeNb == 0)
      {
         counter++;
         if (BigInteger.valueOf(counter).isProbablePrime(10))
            primeNb = counter;
      }
      
      return primeNb;
   }
   
   /*-------------------------------------------------------------*/
   public static byte[] shuffle(final byte originalBytes[])
   /*-------------------------------------------------------------*/
   {
      final int length = originalBytes.length;
      final int nextPrime = nextPrime(length);
      
      final byte shuffledBytes[] = new byte[length];

      for (int j = 1; j <= length; j++)
      {
         final long k = ((long)j * (long)nextPrime) % (long)length;
         shuffledBytes[j - 1] = originalBytes[(int)k];
      }
      
      return shuffledBytes;
   }
   
   /*-------------------------------------------------------------*/
   public static byte[] unshuffle(final byte shuffledBytes[])
   /*-------------------------------------------------------------*/
   {
      final int length = shuffledBytes.length;
      final int nextPrime = nextPrime(length);
      
      final byte unShuffeledBytes[] = new byte[length];
      
      for (int j = 1; j <= length; j++)
      {
         final long k = ((long)j * (long)nextPrime) % (long)length;
         unShuffeledBytes[(int)k] = shuffledBytes[j - 1];
      }
      
      return unShuffeledBytes;
   }

   /**
    * Compute the Levenshtein distance between 2 bytes arrays.
    * @param bytes1 The first bytes array
    * @param bytes2 The second  bytes array
    * @return The Levenshtein distance between the 2 bytes arrays.
    */
   public static double computeLevenshteinDistance(final byte[] bytes1, final byte[] bytes2)
   {
      if (bytes1.length == 0)
         return bytes2.length;
      if (bytes2.length == 0)
         return bytes1.length;

      final int[][] matrix = new int[bytes1.length + 1][bytes2.length + 1];

      for (int i = 0; i <= bytes1.length; i++)
         matrix[i][0] = i;
      for (int j = 0; j <= bytes2.length; j++)
         matrix[0][j] = j;

      for (int i = 1; i <= bytes1.length; i++)
      {
         for (int j = 1; j <= bytes2.length; j++)
         {
            matrix[i][j] = Math.min(matrix[i    ][j - 1], matrix[i - 1][j    ]) + 1;
            if (matrix[i - 1][j - 1] < matrix[i][j])
               matrix[i][j] = matrix[i - 1][j - 1] + ((bytes1[i - 1] == bytes2[j - 1]) ? (0) : (1));
         }
      }
      return matrix[bytes1.length][bytes2.length];
   }

   /**
    * Compute the correlation between 2 bytes array. Return the correlation as a percentage (1.0 = 100% = the 2 arrays are identical)
    * The correlation is computed as the Levensstein distance between the 2 arrays, divided by the length of the longer one.
    * Two empty arrays are considered to have a correlation of 1.0 (100%)
    * @param bytes1 The first bytes array
    * @param bytes2 The second  bytes array
    * @return The correlation as a percentage (1.0 = 100% = the 2 arrays are identical)
    */
   public static double computeLevenshteinCorrelation(final byte[] bytes1, final byte[] bytes2)
   {
      if ((bytes1.length == 0) && (bytes2.length == 0))
         return 1.0;
      return 1.0 - computeLevenshteinDistance(bytes1, bytes2) / (double)Math.max(bytes1.length, bytes2.length);
   }
}
