package com.infinite.share.net.http.auth;

import com.infinite.share.auth.User;
import com.infinite.share.auth.UserSession;
import com.infinite.share.net.http.HomePages;
import com.infinite.share.net.http.InfiniteServlet;
import com.infinite.share.net.mail.EmailAddressFactory;
import com.infinite.share.net.mail.auth.AuthAssistant;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet handles the invitation process.
 */
@SuppressWarnings("serial")
public class InviteServlet extends InfiniteServlet
{
   /**
    * {@inheritDoc}
    */
   public InviteServlet()
   {
      super(AuthPages.invite(), HomePages.welcome());
   }

   /**
    * Handle a request to send an invitation email.
    * {@inheritDoc}
    */
   public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
      final String password     = req.getParameter(AuthPages.INPUT_PASSWORD);
      final String emailAddress = req.getParameter(AuthPages.INPUT_EMAIL);
      
      final User user = UserSession.getUser(req.getSession());
      if ((user != null) && user.isSignedIn())
      {
         if (isEmailAddressValid(emailAddress, req, resp) && (confirmSignin(user, password, req, resp, false)))
         {
            final boolean sent = AuthAssistant.sendInvitation(user, emailAddress);
            if (sent)
               terminateForward(AuthAssistant.msgInvitationSent(emailAddress), req, resp);
            else
               terminateAbort("Unexpected error: invitation not sent.", req, resp);
         }
      }
      else
      {
         terminateSignOut(resp);
      }
   }

   /**
    * Verify whether the email address is valid. In case it is not, abort with an error message.
    * Only verifies the email address complies with the RFC822 format, and does not guarantee that the email address exists, in particular. 
    * @param emailAddress The email address to verify
    * @param req The http request
    * @param resp The http response
    * @return True if the email address is valid.
    * @throws IOException
    */
   private boolean isEmailAddressValid(final String emailAddress, final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
      if (!EmailAddressFactory.verifyAddressFormat(emailAddress))
      {
         terminateAbort("Email address not valid: " + emailAddress, req, resp);
         return false;
      }
      return true;
   }
}
