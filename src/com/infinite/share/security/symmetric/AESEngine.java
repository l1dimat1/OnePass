package com.infinite.share.security.symmetric;

import com.infinite.share.tools.ByteTools;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/********************************************************************************************************************************
 * The AES implementation of the SymmetricEncryptionEngine interface.
 * This implementation uses the "AES/CTR/PKCS5Padding" algorithm provided by "SunJCE".
 * This encryption engine accepts keys of type SecretKey.
 ********************************************************************************************************************************/
final class AESEngine implements SymmetricEncryptionEngine
{
   private static final int    BLOCK_SIZE_IN_BITS = 128;
   private static final int    KEY_FACT_NB_PASSES = 65536;
   private static final String KEY_FACT_ALGORITHM = "PBKDF2WithHmacSHA1";
   private static final String KEY_SPEC_ALGORITHM = "AES";
   private static final String CIPHER_ALGORITHM   = "AES/CTR/PKCS5Padding";
   private static final String CIPHER_PROVIDER    = "SunJCE";
   
   private static final AESEngine _engine = new AESEngine();

   /**
    * Return the single instance of the AESEngine singleton.
    * @return The single instance of the AESEngine singleton.  
    */
   static AESEngine getInstance()
   {
      return _engine;
   }
   
   /**
    * Instantiates and initializes the cipher used by this engine.
    * @param opMode The operation mode (either Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE) to which the cipher must be initialized.
    * @param aesSecretKey A <b>valid</b> secret key to be used by this engine / the returned cipher.
    * @return An initialized cipher, ready to be used by this engine.
    * @throws GeneralSecurityException In case any error occurs during the cipher instantiation or initialization.
    */
   private static Cipher getInitializedCipher(final int opMode, final SecretKey aesSecretKey) throws GeneralSecurityException
   {   
      final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, CIPHER_PROVIDER);

      cipher.init(opMode, aesSecretKey, new IvParameterSpec(aesSecretKey.getEncoded()));

      return cipher;
   }

   /**
    * Instantiates the AESEngine singleton.
    * Ensure that this class will not be extended / instantiated except by itself.
    */
   private AESEngine()
   {  
   }

   /**
    * {@inheritDoc}
    */
   public SecretKey generateSecretKey(final char[] password, final byte[] salt) throws GeneralSecurityException
   {
      if ((password == null) || (password.length == 0))
         throw new GeneralSecurityException("Password is null or empty.");
      if ((salt == null) || (salt.length == 0))
         throw new GeneralSecurityException("Salt is null or empty.");

      final SecretKeyFactory secretKeyFact = SecretKeyFactory.getInstance(KEY_FACT_ALGORITHM);
      final KeySpec pbeKeySpec = new PBEKeySpec(password, ByteTools.shuffle(salt), KEY_FACT_NB_PASSES, BLOCK_SIZE_IN_BITS);
      final SecretKey pbeSecretKey = secretKeyFact.generateSecret(pbeKeySpec);
      final SecretKey aesSecretKeySpec = new SecretKeySpec(pbeSecretKey.getEncoded(), KEY_SPEC_ALGORITHM);
      
      return aesSecretKeySpec;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CipherOutputStream getSecureOutputStream(final OutputStream os, final SecretKey secretKey) throws GeneralSecurityException
   {
      return new CipherOutputStream(os, getInitializedCipher(Cipher.ENCRYPT_MODE, secretKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CipherInputStream getSecureInputStream(final InputStream is, final SecretKey secretKey) throws GeneralSecurityException
   {
      return new CipherInputStream(is, getInitializedCipher(Cipher.DECRYPT_MODE, secretKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public byte[] encrypt(final byte[] input, final SecretKey secretKey) throws GeneralSecurityException
   {
      final Cipher cipher = getInitializedCipher(Cipher.ENCRYPT_MODE, secretKey);
      
      final byte cryptedBytes[] = new byte[cipher.getOutputSize(input.length)];
      
      @SuppressWarnings("unused")
      final int resultLength = cipher.doFinal(input, 0, input.length, cryptedBytes);
      
      return cryptedBytes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public byte[] decrypt(final byte[] input, final SecretKey secretKey) throws GeneralSecurityException
   {
      final Cipher cipher = getInitializedCipher(Cipher.DECRYPT_MODE, secretKey);

      final byte decryptedBytes[] = new byte[cipher.getOutputSize(input.length)];
      
      @SuppressWarnings("unused")
      final int resultLength = cipher.doFinal(input, 0, input.length, decryptedBytes);
      
      return decryptedBytes;
   }
}
