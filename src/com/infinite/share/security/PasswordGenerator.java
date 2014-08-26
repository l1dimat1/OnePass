package com.infinite.share.security;

import java.security.SecureRandom;

/********************************************************************************************************************************
 * A utility class that allows generating random passwords, based on a list of eligible characters.
 ********************************************************************************************************************************/
public final class PasswordGenerator
{
   /**
    * The default set of eligible characters, as a string.
    */
   public static final String DEFAULT_ELIGIBLE_CHARACTERS_SET = "ABCDEFGHIJKLMNOPQRSTUVWXZYabcdefghijklmnopqrstuvwxzy0123456789_-@&%$#!?.<>";

   /**
    * Ensure that this class will never be instantiated / extended.
    */
   private PasswordGenerator()
   {
   }
   
   /**
    * Generates a 16 characters password, using the default set of eligible characters (DEFAULT_ELIGIBLE_CHARACTERS_SET.)
    * @return The randomly generated password.
    */
   public static String generatePassword()
   {
      return generatePassword(16);
   }
   
   /**
    * Generates a password of the specified length, using the default set of eligible characters (DEFAULT_ELIGIBLE_CHARACTERS_SET.)
    * @param length The number of characters of the password to generate.
    * @return The randomly generated password.
    */
   public static String generatePassword(final int length)
   {
      return generatePassword(DEFAULT_ELIGIBLE_CHARACTERS_SET, length);
   }
   
   /**
    * Generates a 16 characters password, using the provided set of eligible characters.
    * @param eligibleCharacters The set of eligible characters, provided as a string.
    * @return The randomly generated password.
    */
   public static String generatePassword(final String eligibleCharacters)
   {
      return generatePassword(eligibleCharacters, 16);
   }
   
   /**
    * Generates a password of the specified length, using the provided set of eligible characters.
    * @param eligibleCharacters The set of eligible characters, provided as a string.
    * @param length The number of characters of the password to generate.
    * @return The randomly generated password.
    */
   public static String generatePassword(final String eligibleCharacters, final int length)
   {
      return generatePassword(eligibleCharacters.toCharArray(), length);
   }
   
   /**
    * Generates a 16 characters password, using the provided set of eligible characters.
    * @param eligibleCharacters The set of eligible characters.
    * @return The randomly generated password.
    */
   public static String generatePassword(final char[] eligibleCharacters)
   {
      return generatePassword(eligibleCharacters, 16);
   }
   
   /**
    * Generates a password of the specified length, using the provided set of eligible characters.
    * @param eligibleCharacters The set of eligible characters.
    * @param length The number of characters of the password to generate.
    * @return The randomly generated password.
    */
   public static String generatePassword(final char[] eligibleCharacters, final int length)
   {
      final SecureRandom rnd = RandomKeyGenerator.newSecureRandom();
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < length; i++)
         sb.append(eligibleCharacters[(rnd.nextInt(eligibleCharacters.length))]);
      return sb.toString();
   }
}
