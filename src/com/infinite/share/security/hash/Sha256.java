package com.infinite.share.security.hash;

import java.security.NoSuchAlgorithmException;

/********************************************************************************************************************************
 * An implementation of the Sha256 hashing function, based on the DefaultHashFunction's logic.
 ********************************************************************************************************************************/
final class Sha256 extends DefaultHashFunction
{
   private static final String ALGORITHM = "SHA-256";
   private static final byte[] PERMANENT_KEY_256_S = { 98, -3, -89, 33, -105, -25, 37, -113, 72, 67, 0, -63, 119, -1, 7, 42, -9, -42, 85, 99, 18, -45, 49, -32, 44, -111, 87, -94, -83, 30, 120, 91, 9, 113, 17, -60, 101, 104, 72, -68, 117, 66, 30, -80, -95, -20, 98, 5, -77, -26, 94, -111, -10, -106, 16, -101, -16, 58, 98, -27, -114, 78, 67, 14, 25, 8, -102, -2, -88, -123, 49, -30, 99, -21, 31, -53, 65, -70, -24, -113, 39, 121, 47, 10, 81, 73, -38, -92, 31, 77, -82, -27, -116, -9, -76, 113, 24, 104, 6, -110, -86, 1, 15, 62, 88, -13, 113, -15, -45, 74, -38, 113, 19, 96, -81, 127, 49, 17, -71, -86, -128, -114, 90, 57, -24, 33, 62, 50, -105, -105, 120, 100, -119, 54, 68, 73, 6, 25, -4, 51, 109, 32, -46, -51, -20, -100, 107, 34, 20, -75, 89, 123, -42, -124, 29, -40, 60, -32, 58, 120, 53, -119, 92, 112, -35, -74, 1, -17, 105, -42, 83, 70, 21, -60, -39, -24, -111, 12, 59, 31, 111, 53, 85, 6, 60, -22, -17, 90, 68, -102, 24, -109, -49, 107, 122, -113, 64, -19, -126, 63, -83, -12, 96, -1, -93, 78, 74, -54, -97, -2, -17, -66, 55, -97, -45, 106, 92, -107, 58, -105, 14, -3, -9, 72, 38, 41, 116, 83, 121, -39, 99, -111, 113, 105, 115, 97, -57, 29, 82, 55, 99, 95, 51, -40, 45, -82, -46, 93, 29, -116, 15, -104, 104, 102, -21, -114 };

   private static final Sha256 _hasher;

   /* ******************************************************************************************************************************** */
   static
   {
      try
      {
         _hasher = new Sha256();
      }
      catch (final NoSuchAlgorithmException e)
      {
         throw new RuntimeException(e); // Never happens, as per JRE's specifications
      }
   }

   /**
    * Return the single instance of the Sha256 hasher singleton.
    * @return The single instance of the Sha256 hasher singleton.
    */
   static Sha256 getInstance()
   {
      return _hasher;
   }

   /**
    * Instantiates the Sha256 singleton.
    */
   private Sha256() throws NoSuchAlgorithmException
   {
      super(ALGORITHM, PERMANENT_KEY_256_S);
   }

}
