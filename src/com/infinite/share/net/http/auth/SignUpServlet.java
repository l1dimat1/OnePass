package com.infinite.share.net.http.auth;

import com.infinite.share.auth.IncorrectKeyException;
import com.infinite.share.auth.Invitation;
import com.infinite.share.auth.User;
import com.infinite.share.net.http.InfiniteServlet;
import com.infinite.share.net.mail.EmailAddressFactory;
import com.infinite.share.net.mail.auth.AuthAssistant;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet handles the sign-up requests.
 */
@SuppressWarnings("serial")
public class SignUpServlet extends InfiniteServlet
{
   /**
    * {@inheritDoc}
    */
   public SignUpServlet()
   {
      super(AuthPages.signUp(), AuthPages.signIn());
   }

   /**
    * Handle a sign-up request.
    * {@inheritDoc}
    */
   public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
      final String inviterId     = req.getParameter(AuthPages.INPUT_INVITER_ID);
      final String emailAddress  = req.getParameter(AuthPages.INPUT_EMAIL);
      final String displayName   = req.getParameter(AuthPages.INPUT_DISPLAY_NAME);
      final String invitationKey = req.getParameter(AuthPages.INPUT_INVITATION_KEY);
      final String userId        = req.getParameter(AuthPages.INPUT_USERID);
      final String password1     = req.getParameter(AuthPages.INPUT_PASSWORD1);
      final String password2     = req.getParameter(AuthPages.INPUT_PASSWORD2);
      
      if (isAValidUserId(userId, req, resp) && isAValidPassword(password1, password2, req, resp) && isPublicInfoValid(displayName, emailAddress, req, resp) &&
                              (User.isAdministratorUserId(userId) || isInvitationKeyCorrect(inviterId, emailAddress, invitationKey, req, resp)))
      {
         try
         {
            User user = new User(userId, password1, displayName, emailAddress);
            boolean inserted = user.persistInsert();
            
            if (inserted)
            {
               boolean welcomeEmailSent = AuthAssistant.sendWelcomeMessage(user);
               terminateForward(AuthAssistant.msgUserSignedUp(user, welcomeEmailSent), req, resp);
            }
            else
            {
               terminateAbort("Unexception error: user account not created.", req, resp);
            }
         }
         catch (EntityExistsException e)
         {
            terminateAbort("User id \"" + userId + "\" is already in use.", req, resp);
         }
         catch (final GeneralSecurityException e)
         {
            terminateAbort("Unexception error: user account not created.", req, resp);
         }
      }
   }

   /**
    * Verify whether the user id is both valid and available. In case it is not, abort with an error message.
    * @param userId The user id.
    * @param req The http request.
    * @param resp The http answer.
    * @return True if the user id is both valid and available, false otherwise.
    * @throws IOException
    */
   private boolean isAValidUserId(final String userId, final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
      if (!User.isAValidUserId(userId))
      {
         terminateAbort("User id is not valid. User id must be at least 4 characters and be made exclusively of alphanumeric characters, '.' or '_'.", req, resp);
         return false;
      }
      if (!User.isUserIdAvailable(userId))
      {
         terminateAbort("User id \"" + userId + "\" is already in use.", req, resp);
         return false;
      }
      return true;
   }

   /**
    * Verify that the user's public information (display name and email address) is valid 
    * @param displayName The display name.
    * @param emailAddress The email address.
    * @param req The http request
    * @param resp The http answer
    * @return True if the public information is valid.
    * @throws IOException
    */
   private boolean isPublicInfoValid(final String displayName, final String emailAddress, final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
      if ((displayName == null) || displayName.isEmpty())
      {
         terminateAbort("Display name not set.", req, resp);
         return false;
      }
      if ((emailAddress == null) || emailAddress.isEmpty())
      {
         terminateAbort("Email address not set.", req, resp);
         return false;
      }
      if (!EmailAddressFactory.verifyAddressFormat(emailAddress))
      {
         terminateAbort("Email address not valid.", req, resp);
         return false;
      }
      return true;
   }

   /**
    * Verify that the invitation key is correct, i.e. that it corresponds to a non-expired invitation key stored in DB for this email address and inviter's id.
    * In case it is not, abort with an error message 
    * @param inviterId The inviter id.
    * @param emailAddress The email address.
    * @param invitationKey The invitation key.
    * @param req The http request
    * @param resp The http answer
    * @return True if the invitation key is correct.
    * @throws IOException
    */
   private boolean isInvitationKeyCorrect(final String inviterId, final String emailAddress, final String invitationKey, final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
      if ((inviterId == null) || inviterId.isEmpty())
      {
         terminateAbort("Inviter id not set.", req, resp);
         return false;
      }
      if ((invitationKey == null) || invitationKey.isEmpty())
      {
         terminateAbort("Invitation key not set.", req, resp);
         return false;
      }
      
      try
      {
         final Invitation invitation = Invitation.restore(invitationKey, inviterId, emailAddress);
         if (invitation == null)
         {
            terminateAbort("Invitation key not valid.", req, resp);
            return false;
         }
         else if (invitation.hasExpired())
         {
            terminateAbort("Invitation key expired.", req, resp);
            return false;
         }  
         return true;
      }
      catch (GeneralSecurityException e)
      {
         terminateAbort("Invitation key not valid.", req, resp);
         return false;  
      }
      catch (IncorrectKeyException e)
      {
         terminateAbort("Invitation key not valid.", req, resp);
         return false;
      }
   }
}
