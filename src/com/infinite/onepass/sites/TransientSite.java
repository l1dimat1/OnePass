package com.infinite.onepass.sites;

import com.infinite.share.auth.User;

import java.security.GeneralSecurityException;

import javax.persistence.Entity;
import javax.persistence.EntityExistsException;

/**
 * 
 */
@Entity
public final class TransientSite extends Site
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructor.
    * @param owner The owner of th site
    * @throws GeneralSecurityException
    */
   public TransientSite(final User owner) throws GeneralSecurityException
   {
      super(owner, defaultName(), "", "", "", "", "", null);
   }

   /**
    * Transient sites cannot be persisted. Throws am UnsupportedOperationException.
    */
   public boolean persistInsert() throws EntityExistsException
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Transient sites cannot be persisted. Throws am UnsupportedOperationException.
    */
   public Site persistUpdate(final User owner, final String reference, final String login, final String password, final String key, final String comment, final String imageB64) throws GeneralSecurityException
   {
      throw new UnsupportedOperationException();
   }
}
