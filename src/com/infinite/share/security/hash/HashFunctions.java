package com.infinite.share.security.hash;

/********************************************************************************************************************************
 * A helper class, providing access to the default hashing functions implemented by this API.
 ********************************************************************************************************************************/
public final class HashFunctions
{
   /**
    * Ensure that this class will never be instantiated / extended.
    */
   private HashFunctions()
   {
   }

   /**
    * Return an instance of a fast hashing function. The returned hashing function might not be fully secured, and finding collision
    * might be easier than with a secure hashing function (provided by getSecureHasherInstance().)
    * @return A fast hasher.
    */
   public static DefaultHashFunction getFastHashInstance()
   {
      return Md5.getInstance();
   }

   /**
    * Return an instance of a secure hashing function. THe returned hasher might be slow, but finding collision will be more difficult
    * than with a fast hasher (provided by getFastHasherInstance().)
    * @return A secure hasher.
    */
   public static DefaultHashFunction getSecureHashInstance()
   {
      return Sha256.getInstance();
   }
}
