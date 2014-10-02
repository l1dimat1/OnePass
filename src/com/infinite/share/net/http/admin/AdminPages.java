package com.infinite.share.net.http.admin;

import com.infinite.share.net.http.HomePages;

/********************************************************************************************************************************
 * Helper class for administration pages.
 ********************************************************************************************************************************/
public final class AdminPages
{
   private static final String PAGE_ADMIN   = "/admin/admin.jsp";
   private static final String PAGE_MESSAGE = "/admin/message.jsp";
   private static       String EXT_PAGE_HOST_ADMIN_CONSOLE = "";
   private static       String EXT_PAGE_DATASTORE_VIEWER   = "";

   /**
    * Ensure that this class will never be instantiated / extended. 
    */
   private AdminPages()
   {
   }

   /**
    * Sets the URL to the hosting service's administration console.
    * @param url The URL to the hosting service's console's URL.
    */
   public static void setHostConsoleUrl(final String url)
   {
      EXT_PAGE_HOST_ADMIN_CONSOLE = url;
   }
   
   public static void setDatastoreViewerUrl(final String url)
   {
      EXT_PAGE_DATASTORE_VIEWER = url;
   }

   /**
    * Return the URL to the administration page.
    * @return The URL to the administration page.
    */
   public static String admin()
   {
      return PAGE_ADMIN;
   }

   /**
    * Return the URL to the administration page.
    * @return The URL to the administration page.
    */
   public static String message()
   {
      return PAGE_MESSAGE;
   }

   /**
    * Return the URL to the hosting service's administration console.
    * @return The URL to the hosting service's administration console.
    */
   public static String extHostAdminConsole()
   {
      return EXT_PAGE_HOST_ADMIN_CONSOLE;
   }

   /**
    * Return the URL to the expired sessions viewer.
    * @return The URL to the expired sessions viewer.
    */
   public static String extHostViewExpiredSessions()
   {
      return HomePages.home() + "_ah/sessioncleanup";
   }

   /**
    * Return the URL to servlet cleaning expired sessions.
    * @return The URL to servlet cleaning expired sessions.
    */
   public static String extHostClearUpExpiredSessions()
   {
      return HomePages.home() + "_ah/sessioncleanup?clear";
   }

   /**
    * Return the URL to the datastore viewer.
    * @return The URL to the datastore viewer.
    */
   public static String extDatastoreViewer()
   {
      return EXT_PAGE_DATASTORE_VIEWER;
   }
}
