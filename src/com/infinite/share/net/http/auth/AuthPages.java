package com.infinite.share.net.http.auth;

import com.infinite.share.net.http.HomePages;
import com.infinite.share.net.http.InfiniteServlet;

/********************************************************************************************************************************
 * Helper class for authentication pages.
 ********************************************************************************************************************************/
public final class AuthPages
{
   public static final String INPUT_USERID         = "userid";
   public static final String INPUT_DISPLAY_NAME   = "displayName";
   public static final String INPUT_EMAIL          = "email";  
   public static final String INPUT_PASSWORD       = "password";
   public static final String INPUT_PASSWORD1      = "password1";
   public static final String INPUT_PASSWORD2      = "password2";
   public static final String INPUT_INVITER_ID     = "inviterid";
   public static final String INPUT_INVITATION_KEY = "invitationkey";
   
   private static final String PAGE_CHANGE_PASSWORD = "/auth/changepassword.jsp";
   private static final String PAGE_INVITE          = "/auth/invite.jsp";
   private static final String PAGE_SIGN_IN         = "/auth/signin.jsp";
   private static final String PAGE_SIGN_OUT        = "/auth/signout.jsp";
   private static final String PAGE_SIGN_UP         = "/auth/signup.jsp";
   private static final String PAGE_PWD_GENERATOR   = "/auth/pwdgen.jsp";

   /**
    * Ensure that this class will never be instantiated / extended. 
    */
   private AuthPages()
   {
   }

   /**
    * Return the URL to the password change page.
    * @return The URL to the password change page.
    */
   public static String changePassword()
   {
      return PAGE_CHANGE_PASSWORD;
   }

   /**
    * Return the URL to the invitation page.
    * @return The URL to the invitation page.
    */
   public static String invite()
   {
      return PAGE_INVITE;
   }

   /**
    * Return the URL to the sign in page.
    * @return The URL to the sign in page.
    */
   public static String signIn()
   {
      return PAGE_SIGN_IN;
   }

   /**
    * Return the URL to the sign in page.
    * @param message The message to be displayed on the sign in page.
    * @return The URL to the sign in page.
    */
   public static String signIn(final String errorMessage)
   {
      return PAGE_SIGN_IN + "?" + InfiniteServlet.ATTRIBUTE_ERROR_MESSAGE + "=" + errorMessage;
   }

   /**
    * Return the URL to the sign out page.
    * @return The URL to the sign out page.
    */
   public static String signOut()
   {
      return PAGE_SIGN_OUT;
   }

   /**
    * Return the URL to the sign up page.
    * @return The URL to the sign up page.
    */
   public static String signUp()
   {
      return PAGE_SIGN_UP;
   }
   
   /**
    * Create and return the sign-up URL given an invitation.
    * @param invitationKey The invitation key.
    * @param inviterName The name of the user sending the invitation.
    * @param inviteeEmailAddress The email address of the person being invited.
    * @param absolutePath If true, the method returns the full, absolute URL. Otherwise, returns the URL relative to the site's root. 
    * @return The sign-up URL. 
    */
   public static String signUp(final String invitationKey, final String inviterName, final String inviteeEmailAddress, final boolean absolutePath)
   {
      StringBuilder urlBuilder = new StringBuilder();
      if (absolutePath)
         urlBuilder.append(HomePages.root());
      urlBuilder.append(PAGE_SIGN_UP);
      urlBuilder.append("?").append(AuthPages.INPUT_EMAIL         ).append("=").append(inviteeEmailAddress);
      urlBuilder.append("&").append(AuthPages.INPUT_INVITER_ID    ).append("=").append(inviterName);
      urlBuilder.append("&").append(AuthPages.INPUT_INVITATION_KEY).append("=").append(invitationKey);
      return urlBuilder.toString();
   }
   
   /**
    * Create and return the sign-up (relative) URL given an invitation.
    * @param invitationKey The invitation key.
    * @param inviterId The id of the user sending the invitation.
    * @param inviteeEmailAddress The email address of the person being invited. 
    * @return The sign-up URL. 
    */
   public static String signUp(final String invitationKey, final String inviterId, final String inviteeEmailAddress)
   {
      return signUp(invitationKey, inviterId, inviteeEmailAddress, false);
   }

   /**
    * Return the URL to the password generator.
    * @return The URL to the password generator.
    */
   public static String passwordGenerator()
   {
      return PAGE_PWD_GENERATOR;
   }
}
