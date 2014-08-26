package com.infinite.share.net.mail.auth;

import com.infinite.share.Application;
import com.infinite.share.auth.Invitation;
import com.infinite.share.auth.User;
import com.infinite.share.net.http.HomePages;
import com.infinite.share.net.mail.EmailAddressFactory;
import com.infinite.share.net.mail.InfiniteAddressBook;
import com.infinite.share.net.mail.MimeMessageBuilder;

import java.security.GeneralSecurityException;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

/********************************************************************************************************************************
 * A helper class that implements the authorization workflows.
 ********************************************************************************************************************************/
public class AuthAssistant
{
   /**
    * Send an invitation to sign up.
    * @param inviter The newUser sending the invitation.
    * @param emailAddress The email address to which the invitation is send.
    * @return True if the invitation was sent successfully, false otherwise. 
    */
   public static boolean sendInvitation(final User inviter, final String emailAddress)
   {
      boolean sent = false;
      try
      {
         final Invitation invitation = new Invitation(inviter, emailAddress);
         invitation.persistInsert();
         sent = invitation.send();
      }
      catch (final GeneralSecurityException e)
      {
      }
      return sent;
   }

   /**
    * The message to be displayed on screen after successfully sending an invitation. 
    * @param emailAddress The email address to which the invitation was sent
    * @return The message to displayed on screen after successfully sending an invitation.
    */
   public static String msgInvitationSent(final String emailAddress)
   {
      return "Sign-up invitation succesfully sent to: " + emailAddress;
   }

   /**
    * 
    * @param newUser The newly signed up user.
    * @return
    */
   public static boolean sendWelcomeMessage(final User newUser)
   {
      try
      {
         MimeMessage msg = MimeMessageBuilder.newInstance()
                                 .setSender(InfiniteAddressBook.noReplyAddress())
                                 .addToRecipient(EmailAddressFactory.createAddress(newUser.getEmailAddress()))
                                 .setSubject("[" + Application.getFullApplicationName() + "] Welcome to " + Application.getFullApplicationName())
                                 .setHtmlContent("Hello<br>"
                                               + "Congratulations, you have successfully sign-up to " + Application.getFullApplicationName() + ".<br><br>"
                                               + "Your user id is: " + newUser.getImmutableUserId() + "<br><br>"
                                               + "See you soon on <a href=\"" + HomePages.home(true) + "\">" + Application.getFullApplicationName() + "</a>!<br><br>"
                                               + "The " + Application.getFullApplicationName() + " team")
                                 .build();
         Transport.send(msg);
         return true;
      }
      catch (final GeneralSecurityException e)
      {         
      }
      catch (final MessagingException e) // Including SendFailedException
      {
      }
      return false;
   }
   
   /**
    * The message to be displayed on screen after successfully signing up a new newUser.
    * @param newUser The newly signed up newUser
    * @return The message to be displayed on screen after successfully signing up a new newUser.
    */
   public static String msgUserSignedUp(final User newUser, final boolean emailSent)
   {
      if (emailSent)
      {
         try
         {
            return "User " + newUser.getImmutableUserId() + " successfully registered. An email has been sent to " + newUser.getEmailAddress() + ".";
         }
         catch (final GeneralSecurityException e)
         {
            return "User " + newUser.getImmutableUserId() + " successfully registered. Unexception error while retrieving newUser's email address.";
         }
      }
      return "User " + newUser.getImmutableUserId() + " successfully registered.";
   }
}
