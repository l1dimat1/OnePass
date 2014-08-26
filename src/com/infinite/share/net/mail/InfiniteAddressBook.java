package com.infinite.share.net.mail;

import javax.mail.internet.InternetAddress;

/********************************************************************************************************************************
 * A class containing constants representing the different email addresses used to send emails.
 ********************************************************************************************************************************/
public final class InfiniteAddressBook
{
   private static InternetAddress NO_REPLY_ADDRESS = EmailAddressFactory.createAddress("no-reply@infinite.appspotmail.com", "Infinite");

   /**
    * Customize the default email address used to send emails.
    * @param address The default email address used to send emails.
    */
   public static void setNoReplyAddress(final InternetAddress address)
   {
      NO_REPLY_ADDRESS = address;
   }
   
   /**
    * Return the default email address used to send emails.
    * @return  The default email address used to send emails.
    */
   public static InternetAddress noReplyAddress()
   {
      return NO_REPLY_ADDRESS;
   }
}
