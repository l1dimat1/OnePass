package com.infinite.share.security.symmetric;

/********************************************************************************************************************************
 * A helper class, providing access to the default symmetric encryption solution implemented by this API. 
 ********************************************************************************************************************************/
public final class SymmetricEncryption
{
   /**
    * Ensure that this class will never be instantiated / extended.
    */
   private SymmetricEncryption()
   {
   }
   
   /**
    * Return the single instance of the default symmetric encryption engine.
    * @return The single instance of the default symmetric encryption engine.
    */
   public static SymmetricEncryptionEngine getEngine()
   {
      return AESEngine.getInstance();
   }
}
