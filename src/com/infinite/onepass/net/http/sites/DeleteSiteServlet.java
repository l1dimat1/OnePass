package com.infinite.onepass.net.http.sites;

import com.infinite.onepass.sites.Site;
import com.infinite.share.auth.User;
import com.infinite.share.auth.UserSession;
import com.infinite.share.net.http.InfiniteServlet;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet called to update a site in datastore.
 */
@SuppressWarnings("serial")
public class DeleteSiteServlet extends InfiniteServlet
{
   /**
    * {@inheritDoc}
    */
   public DeleteSiteServlet()
   {
      super(SitesPages.listSites(), SitesPages.listSites());
   }

   /**
    * {@inheritDoc}
    */
   public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
      final User user = UserSession.getUser(req.getSession());
      if ((user != null) && user.isSignedIn())
      {
         try
         {
            final String siteId = req.getParameter(SitesPages.INPUT_SITE_ID);
            if (siteId != null)
            {
               final Site site = Site.restoreFromId(user, siteId);
               if (site != null)
               {
                  site.delete();
                  terminateRedirect(resp);
               }
            }
            else
            {
               terminateAbort("Site id not set.", req, resp);
            }
         }
         catch (final GeneralSecurityException e)
         {
            terminateAbort("System error. Please try again later", req, resp);
         }
      }
      else
      {
         terminateSignOut(resp);
      }
   }
}
