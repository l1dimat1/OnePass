package com.infinite.share.net.http.admin;

import com.infinite.share.auth.Invitation;
import com.infinite.share.auth.User;
import com.infinite.share.auth.UserSession;
import com.infinite.share.net.http.InfiniteServlet;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
@SuppressWarnings("serial")
public class InvitationCleanUpServlet extends InfiniteServlet
{
   /**
    * {@inheritDoc}
    */
   public InvitationCleanUpServlet()
   {
      super(AdminPages.admin(), AdminPages.message());
   }

   /**
    * Cleans up expired invitations.
    * {@inheritDoc}
    */
   public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
      final User user = UserSession.getUser(req.getSession());
      if ((user != null) && user.isSignedIn())
      {
         if (user.hasAdministrationRights())
         {
            try
            {
               final int nCleanedUp = Invitation.deleteExpiredInvitations();
               terminateForward(nCleanedUp + " invitation(s) cleaned up.", req, resp);
            }
            catch (GeneralSecurityException e)
            {
               terminateAbort("Unexpected error: expired invitations not cleaned up.", req, resp);  
            }
         }
         else
         {
            terminateAbort("Not administrator.", req, resp);
         }
      }
      else
      {
         terminateSignOut(resp);
      }
   }
}
