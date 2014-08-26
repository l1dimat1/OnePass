package com.infinite.share;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 
 */
public final class Application implements ServletContextListener
{
   private static String _appName = "";
   
   /**
    * Set the name of the current infinite application.
    * @param appName The application name. 
    */
   public static void setApplicationName(final String appName)
   {
      _appName = appName;
   }

   /**
    * Return the name of the current infinite application.
    * @return The name of the current infinite application.
    */
   public static String getApplicationName()
   {
      return _appName;
   }

   /**
    * Return the full name of the current infinite application: infinite *.
    * @return The full name of the current infinite application.
    */
   public static String getFullApplicationName()
   {
      return (_appName.isEmpty()) ? ("Infinite") : ("Infinite " + _appName);
   }

   /**
    * {@inheritDoc}
    */
   public void contextInitialized(final ServletContextEvent event)
   {
   }

   /**
    * {@inheritDoc}
    */
   public void contextDestroyed(final ServletContextEvent event)
   {
   }
}
