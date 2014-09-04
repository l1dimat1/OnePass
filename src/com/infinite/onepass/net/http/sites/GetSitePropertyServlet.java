package com.infinite.onepass.net.http.sites;

import com.infinite.onepass.sites.Site;
import com.infinite.onepass.sites.TransientSite;
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
public class GetSitePropertyServlet extends InfiniteServlet
{
   public static final long PROPERTY_DISPLAY_TIMEOUT_MS = 60000;
   
   /**
    * {@inheritDoc}
    */
   public GetSitePropertyServlet()
   {
      super(SitesPages.editSite(), SitesPages.listSites());
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
            final String propertyName = req.getParameter(SitesPages.INPUT_PROPERTY);
            Site site = Site.restoreFromId(user, siteId);
            if (site == null)
               site = new TransientSite(user);
            
            /**/ if (propertyName.equals(SitesPages.INPUT_LOGIN))
               terminatePlainTextResponse(site.getLogin(user), resp);
            else if (propertyName.equals(SitesPages.INPUT_PASSWORD))
               terminatePlainTextResponse(site.getPassword(user), resp);
            else if (propertyName.equals(SitesPages.INPUT_KEY))
               terminatePlainTextResponse(site.getKey(user), resp);
            else if (propertyName.equals(SitesPages.INPUT_COMMENT))
               terminatePlainTextResponse(site.getComment(user), resp);
            else if (propertyName.equals(SitesPages.INPUT_IMAGE_STRING))
               terminatePlainTextResponse(site.getImageB64(user), resp);
         }
         catch (final GeneralSecurityException e)
         {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         }
      }
      else
      {
         resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }
   }

   public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
   }
}
