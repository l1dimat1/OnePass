package com.infinite.share.net.mail;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.InternetAddress;

/********************************************************************************************************************************
 * A factory to create email addresses.
 ********************************************************************************************************************************/
public class EmailAddressFactory
{
   /**
    * 
    * @param address The address string
    * @return True if the string represents a valid email address.
    */
   public static boolean verifyAddressFormat(final String address)
   {
      if (address != null)
         return address.matches(".+\\@.+\\..+");
      return false;
   }
   
   /**
    * Instantiates a new email address object, given an email address string and a name.
    * @param address The email address as a String (*@*.*)
    * @param name The name to map on this email address.
    * @return A new email address object.
    */
   public static InternetAddress createAddress(final String address, final String name)
   {
      InternetAddress emailAddress = null;
      try
      {
         emailAddress = new InternetAddress(address, name, "UTF-8");
      }
      catch (final UnsupportedEncodingException e)
      {
         // Never happens
      }
      return emailAddress;
   }

   /**
    * Instantiates a new email address object, given an email address string.
    * @param address The email address as a String (*@*.*)
    * @return A new email address object.
    */
   public static InternetAddress createAddress(final String address)
   {
      InternetAddress emailAddress = null;
      try
      {
         emailAddress = new InternetAddress(address, "UTF-8");
      }
      catch (final UnsupportedEncodingException e)
      {
         // Never happens
      }
      return emailAddress;
   }
}
