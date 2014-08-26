package com.infinite.share.security.symmetric;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;

/********************************************************************************************************************************
 * An interface describing the minimum set of methods that any symmetric encryption engine must implement.
 ********************************************************************************************************************************/
public interface SymmetricEncryptionEngine
{
   /**
    * Generates a secret key (compatible with this encryption engine) given a password and a salt.
    * @param password The password.
    * @param salt The salt.
    * @return The generated secret key.
    * @exception GeneralSecurityException
    */
   public SecretKey generateSecretKey(final char[] password, final byte[] salt) throws GeneralSecurityException;
   
   /**
    * Given any output stream and a valid secret key, provide a secure CipherOutputStream writing to that output stream.
    * The returned CipherOutputStream can be used to write encrypted data in to the provided output stream.
    * @param os The output stream the CipherOutputStream should write to.
    * @param secretKey The secret key used by the cipher.
    * @return A CipherOutputStream writing to the provided output stream.
    * @exception GeneralSecurityException
    */
   public CipherOutputStream getSecureOutputStream(final OutputStream os, final SecretKey secretKey) throws GeneralSecurityException;

   /**
    * Given any input stream and a valid secret key, provide a secure CipherInputStream read from that input stream.
    * The returned CipherInputStream can be used decrypt encrypted data read from the provided output stream.
    * @param is The input stream the CipherInputStream should read from.
    * @param secretKey The secret key used by the cipher.
    * @return A CipherInputStream reading from the provided input stream.
    * @exception GeneralSecurityException
    */
   public CipherInputStream  getSecureInputStream (final InputStream  is, final SecretKey secretKey) throws GeneralSecurityException;

   /**
    * Encrypts a single array of bytes, using the provided secret key.
    * @param input The byte array to encrypt.
    * @param secretKey The secret to use for encryption.
    * @return The encrypted array of bytes. 
    * @exception GeneralSecurityException
    */
   public byte[] encrypt(final byte[] input, final SecretKey secretKey) throws GeneralSecurityException;

   /**
    * Decrypts a single array of encrypted bytes, using the provided secret key.
    * @param input The encrypted byte array to decrypt.
    * @param secretKey The secret to use for decryption.
    * @return The decrypted array of bytes.
    * @exception GeneralSecurityException
    */
   public byte[] decrypt(final byte[] input, final SecretKey secretKey) throws GeneralSecurityException;
}
