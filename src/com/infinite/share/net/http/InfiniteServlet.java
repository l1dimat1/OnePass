package com.infinite.share.net.http;

import com.infinite.share.auth.User;
import com.infinite.share.auth.UserSession;
import com.infinite.share.net.http.auth.AuthPages;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/********************************************************************************************************************************
 * A default HttpServlet implementation, designed to be extended.
 * This implementation provides:
 *    > A default implementation for doGet and doPost (avoiding servlet exceptions.)
 *    > Default navigation features:
 *       - forward back to the previous page in case an error occurs and/or the servlet aborts.
 *       - redirect to the next page in case the servlet terminates properly.
 *       - logging out the user in case the servlet specifically asks for that.
 ********************************************************************************************************************************/
@SuppressWarnings("serial")
public abstract class InfiniteServlet extends HttpServlet
{
   /**
    * The http attribute name used to pass an information message to
    * the request or response page.
    */
   public static final String ATTRIBUTE_INFO_MESSAGE  = "successmsg";

   /**
    * The http attribute name used to pass an error message to the
    * request or response page.
    */
   public static final String ATTRIBUTE_ERROR_MESSAGE = "errormsg";

   private final String m_requestPage;
   private final String m_responsePage;

   /**
    * Instantiates a new InfiniteServlet, redirecting to the previous and next pages passed in argument.
    * @param requestPage The page to which the servlet will forward the request + error message in case of an "abort"
    * @param responsePage The page to which the servlet will send the response in case it terminates properly. 
    */
   protected InfiniteServlet(final String requestPage, final String responsePage)
   {
      m_requestPage  = requestPage;
      m_responsePage = responsePage;
   }

   /**
    * Default doGet implementation, not throwing ServletException.
    * Redirect the response to the application's homepage.
    * @param req The request
    * @param resp The HTTP response
    * @throws ServletException Never
    * @throws IOException In case an input or output error occurs during the handling of the request
    */
   public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, ServletException
   {
      resp.sendRedirect(HomePages.home());
   }

   /**
    * Default doPost implementation, not throwing ServletException.
    * Redirect the response to the application's homepage.
    * @param req The request
    * @param resp The HTTP response
    * @throws ServletException Never
    * @throws IOException In case an input or output error occurs during the handling of the request
    */
   public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, ServletException
   {
      resp.sendRedirect(HomePages.home());
   }

   /**
    * Ensure both that the new password is valid and that it matches the password confirmation. In case it does not, forward the request back to the sign-up page
    * with an error message.
    * @param password1 The password.
    * @param password2 The password confirmation.
    * @param req
    * @param resp
    * @return True if both that the password is valid and that it matches the password confirmation, false otherwise.
    * @throws IOException
    */
   protected final boolean isAValidPassword(final String password1, final String password2, final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
      if (!password1.equals(password2))
      {
         terminateAbort("Password and verification do not match.", req, resp);
         return false;
      }
      if (!User.isAValidPassword(password1))
      {
         terminateAbort("Password is not valid. Password must be at least 8 characters.", req, resp);
         return false;
      }
      return true;
   }

   /**
    * Verify that the password provided matches the user. In case it is not, abort with an error message.
    * @param user The user
    * @param password The password
    * @param req The http request
    * @param resp The http response
    * @return True if the password is correct
    * @throws IOException
    */
   protected boolean confirmSignin(final User user, final String password, final HttpServletRequest req, final HttpServletResponse resp, boolean signOutOnFailure) throws IOException
   {
      if (!user.isSignedIn())
      {
         if (signOutOnFailure)
            UserSession.signOut(req.getSession());
         terminateAbort("No user signed in.", req, resp);
         return false;
      }
      if (!user.isPasswordCorrect(password))
      {
         if (signOutOnFailure)
         {
            UserSession.signOut(req.getSession());
            terminateAbort("Password not correct. Session ended.", req, resp);
         }
         else
         {
            terminateAbort("Password not correct.", req, resp);
         }
         return false;
      }
      return true;
   }
   
   /**
    * Sends back a plain text response.
    * @param response The response plain text
    * @param resp The HTTP response
    * @throws IOException In case an input or output error occurs during the handling of the request
    */
   protected final void terminatePlainTextResponse(final String response, final HttpServletResponse resp) throws IOException
   {
      resp.setContentType("text/plain");
      PrintWriter out = resp.getWriter();
      out.println(response);
      out.close();
   }

   /**
    * Sends the response to the response page.
    * @param resp The HTTP response
    * @throws IOException In case an input or output error occurs during the handling of the request
    */
   protected final void terminateRedirect(final HttpServletResponse resp) throws IOException
   {
      resp.sendRedirect(m_responsePage);
   }

   /**
    * Forwards the request to the response page.
    * @param req The request
    * @param resp The HTTP response
    * @throws IOException In case an input or output error occurs during the handling of the request
    */
   protected final void terminateForward(final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
      try
      {
         final RequestDispatcher rd = req.getRequestDispatcher(m_responsePage);
         rd.forward(req, resp);
      }
      catch (final ServletException e)
      {
         resp.sendError(ServletErrors.ERR_CODE_00100_SERVLET_EXCEPTION);
      }
   }

   /**
    * Forwards the request + an information message to the response page.
    * @param infoMessage The information message to be forwarded to the response page.
    * @param req The request
    * @param resp The HTTP response
    * @throws IOException In case an input or output error occurs during the handling of the request
    */
   protected final void terminateForward(final String infoMessage, final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
      try
      {
         req.setAttribute(InfiniteServlet.ATTRIBUTE_INFO_MESSAGE, infoMessage);
         final RequestDispatcher rd = req.getRequestDispatcher(m_responsePage);
         rd.forward(req, resp);
      }
      catch (final ServletException e)
      {
         resp.sendError(ServletErrors.ERR_CODE_00100_SERVLET_EXCEPTION);
      }
   }
   
   /**
    * Forwards the request + an error message back to the request page.
    * @param errorMessage The error message to be forwarded back to the request page.
    * @param req The request
    * @param resp The HTTP response
    * @throws IOException In case an input or output error occurs during the handling of the request
    */
   protected final void terminateAbort(final String errorMessage, final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
      try
      {
         req.setAttribute(InfiniteServlet.ATTRIBUTE_ERROR_MESSAGE, errorMessage);
         final RequestDispatcher rd = req.getRequestDispatcher(m_requestPage);
         rd.forward(req, resp);
      }
      catch (final ServletException e)
      {
         resp.sendError(ServletErrors.ERR_CODE_00100_SERVLET_EXCEPTION);
      }
   }
   
   /**
    * Redirects to the log out page in order to terminate the current session.
    * @param resp The HTTP response
    * @throws IOException In case an input or output error occurs during the handling of the request
    */
   protected final void terminateSignOut(final HttpServletResponse resp) throws IOException
   {
      resp.sendRedirect(AuthPages.signOut());
   }
   
   /**
    * Returns the value of a request parameter as a String, or "" if the parameter does not exist.
    * @param req The Http request to read parameter from.
    * @param paramName The parameter name.
    * @return The value of a request parameter as a String, or "" if the parameter does not exist.
    */
   protected final String getNonNullParameter(final HttpServletRequest req, final String paramName)
   {
      return getNonNullParameter(req, paramName, "");
   }

   /**
    * Returns the value of a request parameter as a String, or the specified default value if the parameter does not exist.
    * @param req The Http request to read parameter from.
    * @param paramName The parameter name.
    * @paran defaultValue The value returned in case the parameter does not exit.
    * @return The value of a request parameter as a String, or specified default value if the parameter does not exist.
    */
   protected final String getNonNullParameter(final HttpServletRequest req, final String paramName, final String defaultValue)
   {
      final String paramValue = req.getParameter(paramName);
      return (paramValue != null) ? (paramValue) : (defaultValue);
   }
}
