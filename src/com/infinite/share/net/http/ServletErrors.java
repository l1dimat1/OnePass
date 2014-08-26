package com.infinite.share.net.http;

/********************************************************************************************************************************
 * This class defines error codes constants to be used anywhere in the application.
 ********************************************************************************************************************************/
public final class ServletErrors
{
   /**
    * Ensure that this class will never be instantiated / extended.
    */
   private ServletErrors()
   {
   }

   /**
    * Error 00100: an ServletException was thrown by a servlet. 
    */
   public static final int ERR_CODE_00100_SERVLET_EXCEPTION = 00100;
}
