package com.infinite.onepass.net.http.sites;

import com.infinite.onepass.sites.Site;

/**
 * Helper class for sites display and management pages.
 */
public class SitesPages
{
   public static final String INPUT_FILTER = "filter";
   
   public static final String INPUT_SITE_ID  = "siteId";
   public static final String INPUT_PROPERTY = "property";
   public static final String INPUT_ACTION    = "action"; 
   
   public static final String INPUT_SITE_NAME    = "siteName";
   public static final String INPUT_REFERENCE    = "siteReference";
   public static final String INPUT_LOGIN        = "siteLogin";
   public static final String INPUT_PASSWORD     = "sitePassword";
   public static final String INPUT_KEY1         = "siteKey1";
   public static final String INPUT_KEY2         = "siteKey2";
   public static final String INPUT_KEY3         = "siteKey3";
   public static final String INPUT_COMMENT1     = "siteComment1";
   public static final String INPUT_COMMENT2     = "siteComment2";
   public static final String INPUT_COMMENT3     = "siteComment3";
   public static final String INPUT_IMAGE_STRING = "siteImageB64";
   
   public static final int ACTION_VIEW = 0;
   public static final int ACTION_EDIT = 0;
   
   private static final String PAGE_EDIT_SITE  = "/onepass/sites/site.jsp";
   private static final String PAGE_LIST_SITES = "/onepass/sites/sites.jsp";
   
   /**
    * Ensure that this class will never be instantiated / extended. 
    */
   private SitesPages()
   {
   }

   /**
    * Return the URL to the site creation page.
    * @return The URL to the site display / edition page.
    */
   public static String createSite()
   {
      return PAGE_EDIT_SITE + "?" + INPUT_ACTION + "=" + ACTION_EDIT;
   }

   /**
    * Return the URL to the site edition page.
    * @return The URL to the site display / edition page.
    */
   public static String editSite()
   {
      return PAGE_EDIT_SITE;
   }

   /**
    * Return the URL to the site edition page.
    * @return The URL to the site display / edition page.
    */
   public static String editSite(final Site site)
   {
      if (site != null)
         return PAGE_EDIT_SITE + "?" + INPUT_SITE_ID + "=" + site.getSiteId();
      return PAGE_EDIT_SITE;
   }

   /**
    * Return the URL to the site edition page.
    * @param allowEdition True in case the page should allow site edition / site saving.
    * @return The URL to the site display / edition page.
    */
   public static String editSite(final Site site, final boolean allowEdition)
   {
      if (site != null)
         return PAGE_EDIT_SITE + "?" + INPUT_SITE_ID + "=" + site.getSiteId() + "&" + INPUT_ACTION + "=" + ((allowEdition) ? (ACTION_EDIT) : (ACTION_VIEW));
      return PAGE_EDIT_SITE;
   }

   /**
    * Return the URL to the sites list page.
    * @return The URL to the sites list page.
    */
   public static String listSites()
   {
      return PAGE_LIST_SITES;
   }
}
