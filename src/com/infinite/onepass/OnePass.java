package com.infinite.onepass;

import com.infinite.onepass.net.http.sites.SitesPages;
import com.infinite.share.Application;
import com.infinite.share.auth.event.AuthEventManager;
import com.infinite.share.net.http.HomePages;
import com.infinite.share.net.http.admin.AdminPages;
import com.infinite.share.net.mail.EmailAddressFactory;
import com.infinite.share.net.mail.InfiniteAddressBook;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * ServletContextListener used to initialize the application before any servlet is started.
 * {@inheritDoc}
 */
public final class OnePass implements ServletContextListener
{
   /**
    * Initializes the OnePass application.
    */
   public static void initialize()
   {
      Application.setApplicationName("OnePass");
      HomePages.setRootUrl("https://infinite-onepass.appspot.com");
      HomePages.setWelcomePage(SitesPages.listSites());
      AdminPages.setHostConsoleUrl("https://appengine.google.com/dashboard?&app_id=s~infinite-onepass");
      AdminPages.setDatastoreViewerUrl("https://appengine.google.com/datastore/explorer?&app_id=s~infinite-onepass");
      InfiniteAddressBook.setNoReplyAddress(EmailAddressFactory.createAddress("no-reply@infinite-onepass.appspotmail.com", "Infinite OnePass"));
      
      AuthEventManager.addAuthListener(OnePassAuthListener.getInstance());
   }
   
   /**
    * {@inheritDoc}
    */
   public void contextInitialized(final ServletContextEvent event)
   {
      initialize();
   }

   /**
    * {@inheritDoc}
    */
   public void contextDestroyed(final ServletContextEvent event)
   {
   }
}
