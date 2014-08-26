package com.infinite.share.net.http.auth;

import com.infinite.share.auth.User;
import com.infinite.share.auth.UserSession;
import com.infinite.share.net.http.HomePages;
import com.infinite.share.net.http.InfiniteServlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet handles the password change requests.
 */
@SuppressWarnings("serial")
public class ChangePasswordServlet extends InfiniteServlet
{
   /**
    * {@inheritDoc}
    */
   public ChangePasswordServlet()
   {
      super(AuthPages.changePassword(), HomePages.welcome());
   }
   
   /**
    * Handle a password change request.
    * {@inheritDoc}
    */
   public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
      final String oldPassword  = req.getParameter(AuthPages.INPUT_PASSWORD);
      final String newPassword1 = req.getParameter(AuthPages.INPUT_PASSWORD1);
      final String newPassword2 = req.getParameter(AuthPages.INPUT_PASSWORD2);

      final User user = UserSession.getUser(req.getSession());

      if ((user != null) && user.isSignedIn())
      {
         if (confirmSignin(user, oldPassword, req, resp, false) && isAValidPassword(newPassword1, newPassword2, req, resp))
         {
            if (user.changePassword(newPassword1))
            {
               UserSession.signOut(req.getSession());  
               terminateForward("Password changed. Please sign in again.", req, resp);
            }
            else
            {
               terminateAbort("Unexpected error: password not changed.", req, resp);
            }
         }
      }
      else
      {
         terminateAbort("User not logged in.", req, resp);
      }
   }
}
