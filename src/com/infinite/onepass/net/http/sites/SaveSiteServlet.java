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
public class SaveSiteServlet extends InfiniteServlet
{
   /**
    * {@inheritDoc}
    */
   public SaveSiteServlet()
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

            final String siteReference = getNonNullParameter(req, SitesPages.INPUT_REFERENCE);
            final String siteLogin     = getNonNullParameter(req, SitesPages.INPUT_LOGIN);
            final String sitePassword  = getNonNullParameter(req, SitesPages.INPUT_PASSWORD);
            final String siteKey1      = getNonNullParameter(req, SitesPages.INPUT_KEY1);
            final String siteKey2      = getNonNullParameter(req, SitesPages.INPUT_KEY2);
            final String siteKey3      = getNonNullParameter(req, SitesPages.INPUT_KEY3);
            final String siteComment1  = getNonNullParameter(req, SitesPages.INPUT_COMMENT1);
            final String siteComment2  = getNonNullParameter(req, SitesPages.INPUT_COMMENT2);
            final String siteComment3  = getNonNullParameter(req, SitesPages.INPUT_COMMENT3);
            final String siteImageB64  = getNonNullParameter(req, SitesPages.INPUT_IMAGE_STRING, Site.defaultImage());
            
            Site site = Site.restoreFromId(user, siteId);
            if (site == null)
            {
               final String newSiteName = req.getParameter(SitesPages.INPUT_SITE_NAME);
               if (!Site.exist(user, newSiteName))
               {
                  site = new Site(user, newSiteName, siteReference,
                                          siteLogin, sitePassword,
                                          siteKey1, siteKey2, siteKey3,
                                          siteComment1, siteComment2, siteComment3, siteImageB64);
                  site.persistUpdate(user, siteReference, siteLogin, sitePassword,
                                          siteKey1, siteKey2, siteKey3,
                                          siteComment1, siteComment2, siteComment3,
                                          siteImageB64);
                  terminateRedirect(resp);
               }
               else
               {
                  terminateAbort("Site \"" + newSiteName + "\" already exists. Please chose another site name.", req, resp);
               }
            }
            else
            {
               site.persistUpdate(user, siteReference, siteLogin, sitePassword, siteKey1, siteKey2, siteKey3, siteComment1, siteComment2, siteComment3, siteImageB64);
               terminateRedirect(resp);
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
