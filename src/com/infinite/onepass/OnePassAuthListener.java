package com.infinite.onepass;

import com.infinite.onepass.sites.Site;
import com.infinite.share.auth.User;
import com.infinite.share.auth.event.AuthListener;
import com.infinite.share.auth.event.AuthListenerException;

import java.security.GeneralSecurityException;
import java.util.Set;

/**
 * AuthListener for OnePass.
 * {@inheritDoc}
 */
final class OnePassAuthListener implements AuthListener
{
   private static final OnePassAuthListener _instance = new OnePassAuthListener();
   
   /**
    * Ensures singleton.
    */
   private OnePassAuthListener()
   {
   }

   /**
    * Return the single instance of this class.
    * @return The single instance of this class.
    */
   public static OnePassAuthListener getInstance()
   {
      return _instance;
   }
   
   /**
    * {@inheritDoc}
    * Re-encrypt all sites data on the new password.
    */
   public void passwordChanged(final User userBeforeChange, final User userAfterChange) throws AuthListenerException
   {
      try
      {
         Set<Site> sites = Site.restoreAll(userBeforeChange);
         for (Site site: sites)
         {
            site.ownerUpdated(userBeforeChange, userAfterChange);
         }
      }
      catch (GeneralSecurityException e)
      {
         throw new AuthListenerException(e);
      }
   }
}
