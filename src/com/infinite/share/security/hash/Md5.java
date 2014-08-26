package com.infinite.share.security.hash;

import java.security.NoSuchAlgorithmException;

/********************************************************************************************************************************
 * An implementation of the Md5 hashing function, based on the DefaultHashFunction's logic.
 ********************************************************************************************************************************/
final class Md5 extends DefaultHashFunction
{
   private static final String ALGORITHM = "MD5";
   private static final byte[] PERMANENT_KEY_256_S = { -40, -87, -87, -71, -88, 24, -26, 49, -17, 48, -20, -80, -50, -3, -69, -124, -53, -118, 83, 106, -114, 58, -25, 110, -44, -3, -112, -116, 41, -103, -101, 106, -24, -112, -100, 45, 38, -66, 71, -115, -25, -62, -69, -39, 71, 96, -3, -118, -24, -26, -37, 22, 15, 0, 9, -128, 26, -59, -76, -10, 44, 11, 29, 18, -32, 99, -56, 12, 125, -17, -106, -113, -95, -111, 78, 35, 31, 73, 48, -81, 126, -35, 17, -31, 77, 78, -8, 74, -80, 64, 64, -30, -79, -9, -117, -113, -35, -120, 3, -81, -5, -24, -7, 16, -89, -41, -36, -24, 69, 45, -3, -48, 1, 109, 99, -83, 127, -51, -56, 31, 8, -78, -40, -111, -127, 76, 62, 63, 37, -45, -10, 58, 94, 75, 18, 5, 49, -13, -16, -52, -15, -87, 95, 79, -111, 80, -14, 14, 121, 71, 122, -85, -83, 75, -127, -24, -49, -106, 47, 127, 95, -62, -45, 116, 57, 73, 61, 32, -35, -92, 82, -12, 19, -123, -65, 67, 113, 28, -20, -54, -101, -114, -49, -61, 14, 125, 62, -6, -43, 109, 124, -50, 114, -56, 102, 43, -38, -123, -37, -72, 18, 25, 8, 53, -34, 53, -48, -87, 21, -6, 59, -30, 67, 101, 65, 6, 68, 86, 26, 119, -41, -15, 58, 13, 101, -41, -103, -103, 14, 9, 79, 66, -119, -19, 121, 69, 104, 3, 110, 78, 33, 79, 83, -40, -28, 5, -9, 37, -83, 43, -108, -95, -65, -11, 98, 56 };

   private static final Md5 _hasher;

   /* ******************************************************************************************************************************** */
   static
   {
      try
      {
         _hasher = new Md5();
      }
      catch (final NoSuchAlgorithmException e)
      {
         throw new RuntimeException(e); // Never happens, as per JRE's specifications
      }
   }

   /**
    * Return the single instance of the Md5 hasher singleton.
    * @return The single instance of the Md5 hasher singleton.
    */
   static Md5 getInstance()
   {
      return _hasher;
   }

   /**
    * Instantiates the Md5 singleton
    */
   private Md5() throws NoSuchAlgorithmException 
   {
      super(ALGORITHM, PERMANENT_KEY_256_S);
   }
}
