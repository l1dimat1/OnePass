package com.infinite.share.net.http.auth;

import com.infinite.share.auth.IncorrectKeyException;
import com.infinite.share.auth.User;
import com.infinite.share.auth.UserSession;
import com.infinite.share.net.http.HomePages;
import com.infinite.share.net.http.InfiniteServlet;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet handles the sign-in requests.
 */
@SuppressWarnings("serial")
public class SignInServlet extends InfiniteServlet
{
   /**
    * {@inheritDoc}
    */
   public SignInServlet()
   {
      super(AuthPages.signIn(), HomePages.welcome());
   }

   /**
    * Handle a sign-in request.
    * {@inheritDoc}
    */
   public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
      final String userId   = req.getParameter(AuthPages.INPUT_USERID);
      final String password = req.getParameter(AuthPages.INPUT_PASSWORD);
      
      if ((userId != null) && !userId.isEmpty() && (password != null) && !password.isEmpty())
      {
         try
         {
            final User user = User.restore(userId, password);
            if (user == null)
            {
               UserSession.signOut(req.getSession());
               terminateAbort("Credentials not correct.", req, resp);
            }
            else if (!user.isSignedIn())
            {
               UserSession.signOut(req.getSession());
               terminateAbort("Unexpected error: sign in aborted.", req, resp);
            }
            else
            {
               UserSession.signIn(req.getSession(), user);
               terminateRedirect(resp);
            }
         }
         catch (final IncorrectKeyException e)
         {
            UserSession.signOut(req.getSession());
            terminateAbort("Credentials not correct.", req, resp);
         }
         catch (final GeneralSecurityException e)
         {
            UserSession.signOut(req.getSession());
            terminateAbort("Unexpected error: sign in aborted.", req, resp);
         }
      }
      else
      {
         UserSession.signOut(req.getSession());
         terminateAbort("Credentials missing.", req, resp);         
      }
   }
}
