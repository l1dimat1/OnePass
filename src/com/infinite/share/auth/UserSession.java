package com.infinite.share.auth;

import javax.servlet.http.HttpSession;

/********************************************************************************************************************************
 * This utility class provides helper functions to read / write into any Infinite HTTP session.
 ********************************************************************************************************************************/
public class UserSession
{
   private static final String ATTRIBUTE_USER = "User";

   /**
    * Ensure that this class will never be instantiated / extended. 
    */
   private UserSession()
   {
   }

   /**
    * Return the number of second after which any page is automatically redirected to the sign out page.
    * @return
    */
   public static int getAutoSignOutDelayS()
   {
      return 10 * 60; // 10 minutes
   }
   
   /**
    * Sign-in the user received in argument.
    * @param session The HTTP session.
    * @param user The user to sign in.
    * @return True if sign in was successful, false otherwise.
    */
   public static boolean signIn(final HttpSession session, final User user)
   {
      if (!user.isSignedIn())
      {
         signOut(session);
         return false;
      }
      session.setAttribute(ATTRIBUTE_USER, user);
      return true;
   }

   /**
    * Check whether a user is signed in this session.
    * @param session The HTTP session.
    * @return True if a user is signed in this session, false otherwise.
    */
   public static boolean isSignedIn(final HttpSession session)
   {
      return getUser(session) != null;
   }

   /**
    * In case a user is signed in this session, sign this user out.
    * @param session The HTTP session.
    */
   public static void signOut(final HttpSession session)
   {
      session.setAttribute(ATTRIBUTE_USER, null);
      //session.invalidate();
   }

   /**
    * Return the user that is signed in this session, or null if no user is signed in.
    * @param session The HTTP session.
    * @return The signed-in user.
    */
   public static User getUser(final HttpSession session)
   {
      return (User)session.getAttribute(ATTRIBUTE_USER);
   }
}
