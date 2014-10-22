package com.infinite.onepass.sites;

import com.infinite.share.auth.User;

import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class providing sites filtering functions.
 */
public class SiteFilter
{
   /**
    * Filters a collection of sites, returning the sub-collection containing all sites which name,
    * reference or comment contains (ignoring case) the filter string.
    * In case the filter string is null or empty, a copy of the input collection is returned. 
    * @param sites The complete collection of sites, to be filtered.
    * @param filterString The string to look for.
    * @param owner The owner of the sites.
    * @return A collection of sites 
    */
   public static Collection<Site> filter(final Collection<Site> sites, final String filterString, final User owner)
   {
      if ((filterString == null) || filterString.isEmpty())
         return sites;
         
      final List<Site> filteredSites = new LinkedList<Site>();
      final String lcFilterString = filterString.toLowerCase();
      for (final Site site: sites)
      {
         try
         {
            final String name      = site.getName     (owner);
            final String reference = site.getReference(owner);
            if (((name      != null) && name     .toLowerCase().contains(lcFilterString)) ||
                ((reference != null) && reference.toLowerCase().contains(lcFilterString)))
               filteredSites.add(site);
         }
         catch (final GeneralSecurityException e)
         {
         }
      }
      
      return filteredSites;
   }
}
