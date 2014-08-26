package com.infinite.share.net.http;

/********************************************************************************************************************************
 * Helper class for root / home pages.
 ********************************************************************************************************************************/
public class HomePages
{
   private static       String PAGE_ROOT      = "/";
   private static final String PAGE_HOME      = "/";
   private static       String PAGE_WELCOME   = PAGE_HOME;

   /**
    * Ensure that this class will never be instantiated / extended. 
    */
   private HomePages()
   {
   }
   
   /**
    * Sets the URL to the welcome page (the page displayed after signing in.)
    * @param url The URL to the root of the web application.
    */
   public static void setWelcomePage(final String url)
   {
      PAGE_WELCOME = url;
   }
   
   /**
    * Sets the root URL.
    * @param url The root URL.
    */
   public static void setRootUrl(final String url)
   {
      PAGE_ROOT = url;
   }
   
   /**
    * Return the URL to the welcome page (the page displayed after signing in.)
    * @return The URL to the root of the web application.
    */
   public static String welcome()
   {
      return PAGE_WELCOME;
   }
   
   /**
    * Return the URL to the root of the web application.
    * @return The URL to the root of the web application.
    */
   public static String root()
   {
      return PAGE_ROOT;
   }
   
   /**
    * Return the URL to the home page of the web application.
    * @param absolutePath If true, the method returns the full, absolute URL. Otherwise, returns the URL relative to the site's root.
    * @return The URL to the home page of the web application.
    */
   public static String home(final boolean absolutePath)
   {
      if (absolutePath)
         return PAGE_ROOT + PAGE_HOME;
      return PAGE_HOME;
   }
   
   /**
    * Return the (relative) URL to the home page of the web application.
    * @return The URL to the home page of the web application.
    */
   public static String home()
   {
      return home(false);
   }
}
