package com.infinite.onepass.sites;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.infinite.share.auth.User;
import com.infinite.share.persistence.DatastoreTools;
import com.infinite.share.persistence.Persistent;
import com.infinite.share.security.RandomPadding;
import com.infinite.share.security.Salt;
import com.infinite.share.security.hash.HashFunctions;
import com.infinite.share.security.symmetric.SymmetricEncryption;
import com.infinite.share.tools.ByteTools;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityExistsException;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 
 */
@Entity
public class Site implements Persistent, Comparable<Site>
{
   public static final int MAX_IMG_LENGTH = 256;
   
   /**
    * Instantiates a new site given its owner, name and properties.
    * @param owner The site owner.
    * @param name The site name.
    * @param reference The site 'reference' property.
    * @param login The site 'login' property.
    * @param password The site 'password' property.
    * @param key The site 'key' property.
    * @param comment The site 'comment' property.
    * @param imageB64 The site 'image' property, as a b64 image.
    * @throws GeneralSecurityException If an error occurred during properties encryption.
    */
   public Site(final User owner, final String name, final String reference, final String login, final String password, final String key, final String comment, final String imageB64) throws GeneralSecurityException
   {
      if (!owner.isSignedIn())
         throw new IllegalStateException("User not logged in.");
      if ((name == null) || name.isEmpty())
         throw new IllegalArgumentException("Site name is either null or empty.");
      
      m_ownerId = owner.getUniqueId();
      m_name    = name;
      padName     (name, owner);
      setReference(reference, owner);
      setLogin    (login,     owner);
      setPassword (password,  owner);
      setKey      (key,       owner);
      setComment  (comment,   owner);
      setImageB64 (imageB64,  owner);
      m_primaryKey  = generateDatastoreKey(owner, name);
   }

   /**
    * Protected default constructor, as required by the appengine / JPA API.
    * Should not be called, except by JPA.
    */
   protected Site()
   {
      m_ownerId     = null;
      m_x_name      = null;
      m_x_reference = null;
      m_x_login     = null;
      m_x_password  = null;
      m_x_key       = null;
      m_x_comment   = null;
      m_x_imageB64  = null;
      m_primaryKey  = null;
   }
   
   /**
    * Check whether a site already exist with this owner and name.
    * @param owner The site owner.
    * @param name The site name.
    * @return True if such a site already exists.
    * @throws GeneralSecurityException If an error occurred while encrypting the site's key.
    * @throws IllegalStateException In case the owner user is not signed in.
    */
   public static boolean exist(final User owner, final String name) throws GeneralSecurityException
   {
      return restoreFromName(owner, name) != null;
   }
   
   /**
    * Restores a site from the datastore, given its owner and name.
    * @param owner The site owner.
    * @param name The site name.
    * @return The site object, restored from datastore, or null if an error occured.
    * @throws GeneralSecurityException If an error occurred while encrypting the site's key.
    * @throws IllegalStateException In case the owner user is not signed in.
    */
   public static Site restoreFromName(final User owner, final String name) throws GeneralSecurityException
   {
      return restoreFromId(owner, generateSiteId(owner, name));
   }
   
   /**
    * Restores a site from the datastore, given its owner and name.
    * @param owner The site's owner
    * @param siteId The site's id (derived from its owner and name.)
    * @return The site object, restored from datastore, or null if an error occured.
    * @throws GeneralSecurityException If an error occurred while encrypting the site's key.
    * @throws IllegalStateException In case the owner user is not signed in.
    */
   public static Site restoreFromId(final User owner, final String siteId) throws GeneralSecurityException
   {
      final Key uniqueKey = generateDatastoreKey(siteId);
      final Site site = DatastoreTools.selectOne(Site.class,  uniqueKey);
      
      if (site != null)
      {
         site.postLoad(owner);
      }
      
      return site;
   }
   
   /**
    * Fetches all sites owned by the specified owner.
    * @param owner The sites owner.
    * @return The sorted list of all sites owned by the specified owner.
    * @throws GeneralSecurityException If an error occurred during the user's id encryption.
    */
   public static SortedSet<Site> restoreAll(final User owner) throws GeneralSecurityException
   {
      final SortedSet<Site> sortedSites = new TreeSet<Site>();
      
      final List<Site> sites = DatastoreTools.query(Site.class, "SELECT s from Site s WHERE s.m_ownerId='" + owner.getUniqueId() + "'");

      for (final Site site: sites)
         site.postLoad(owner);
      
      if (sites != null)
         sortedSites.addAll(sites);

      return sortedSites;
   }
   
   /**
    * Delete this site from the datastore.
    * @return True if the site was found and deleted.
    */
   public final boolean delete()
   {
      return DatastoreTools.delete(this);
   }

   /**
    * Insert (persist) this site in the datastore. Fails in case a site with the same unique key already exists.
    * @return True is the site was successfully inserted. False otherwise, in particular if a site with the same unique key already exists.
    * @throws EntityExistsException If asite with the same unique key already exists in the datastore.
    */
   public /*final*/ boolean persistInsert() throws EntityExistsException
   {
      return DatastoreTools.insert(this);
   }

   /**
    * Update (persist) this site in the datastore if it already exists. Previously persisted data is overwritten.
    * @param owner The owner
    * @param reference The (updated) reference
    * @param login The (updated) login
    * @param password The (updated) password
    * @param key The (updated) key
    * @param comment The (updated) comment
    * @param imageB64 The (updated) image B64
    * @return The persisted site (this or null if an error occurred).
    * @throws GeneralSecurityException If an exception occurred during properties encryption.
    */
   public /*final*/ Site persistUpdate(final User owner, final String reference, final String login, final String password, final String key, final String comment, final String imageB64) throws GeneralSecurityException
   {
      padName     (getName(owner), owner);
      setReference(reference,      owner);
      setLogin    (login,          owner);
      setPassword (password,       owner);
      setKey      (key,            owner);
      setComment  (comment,        owner);
      setImageB64 (imageB64,       owner);

      return persistUpdate(owner);
   }

   /**
    * Update (persist) this site in the datastore if it already exists. Previously persisted data is overwritten.
    * @return The persisted site (this or null if an error occurred).
    */
   private final Site persistUpdate(final User owner)
   {
      try
      {
         final Site updatedSite = DatastoreTools.update(this);
         if (updatedSite != null)
            updatedSite.postLoad(owner);
         return updatedSite;
      }
      catch (GeneralSecurityException e)
      {
      }
      return null;
   }

   /**
    * Return the default site name.
    * @return The default site name.
    */
   public static String defaultName()
   {
      return DEFAULT_NAME;
   }

   /**
    * Return the B64 representation of the default image.
    * @return The B64 representation of the default image.
    */
   public static String defaultImage()
   {
      return DEFAULT_IMAGE;
   }

   /**
    * Compares this site to the site passed in argument.
    * @param oSite The site to compare this site to.
    * @return A negative integer, zero, or a positive integer as this site is "less than", "equal to", or "greater than" the specified site.
    */
   @Override
   public final int compareTo(final Site oSite)
   {
      try
      {
         return m_name.compareToIgnoreCase(oSite.m_name);
      }
      catch (final Exception e)
      {
         return 0;
      }
   }
   
   /**
    * Informs the site of a change in its owner's properties, typically a change in secret key.
    * @param ownerBeforeChange A copy of the owner instance, as it was just before changing.
    * @param ownerAfterChange The owner instance, after changing.
    * @return True if the site was correctly modified and persisted.
    * @throws GeneralSecurityException If an error occurred while decrypting / re-ecrypting any property.
    */
   public final boolean ownerUpdated(final User ownerBeforeChange, final User ownerAfterChange) throws GeneralSecurityException
   {
      // m_ownerId Immutable (never changes once the site is created)
      // m_name    Immutable (never changes once the site is created)
      padName     (getName     (ownerBeforeChange), ownerAfterChange);
      setReference(getReference(ownerBeforeChange), ownerAfterChange);
      setLogin    (getLogin    (ownerBeforeChange), ownerAfterChange);
      setPassword (getPassword (ownerBeforeChange), ownerAfterChange);
      setKey      (getKey      (ownerBeforeChange), ownerAfterChange);
      setComment  (getComment  (ownerBeforeChange), ownerAfterChange);
      setImageB64 (getImageB64 (ownerBeforeChange), ownerAfterChange);
      // m_primaryKey Immutable (never changes once the site is created)
      
      return (persistUpdate(ownerAfterChange) != null);
   }

   /**
    * Return the site's id.
    * @return The site's id.
    */
   public final String getSiteId()
   {
      return m_primaryKey.getName();
   }
   
   /**
    * Get the name property.
    * @return The name property.
    * @throws GeneralSecurityException If an error occurred while decrypting the property.
    */
   public final String getName(final User owner) throws GeneralSecurityException
   {
      if ((owner == null) || !owner.getUniqueId().equals(m_ownerId))
         throw new IllegalArgumentException("Not owner of site.");
      if (!owner.isSignedIn())
         throw new IllegalStateException("Owner not logged in.");
      return fromPaddedUTF8Bytes(SymmetricEncryption.getEngine().decrypt(m_x_name, owner.getSecretKey()));
   }

   /**
    * Return true if the reference of this site looks like an URL.
    * @return The reference property.
    * @throws GeneralSecurityException If an error occurred while decrypting the property.
    */
   public final boolean isReferenceAUrl(final User owner) throws GeneralSecurityException
   {
      final String reference = getReference(owner);
      return (reference.toLowerCase().indexOf("http") == 0);
   }
   
   /**
    * Get the reference property.
    * @return The reference property.
    * @throws GeneralSecurityException If an error occurred while decrypting the property.
    */
   public final String getReference(final User owner) throws GeneralSecurityException
   {
      if ((owner == null) || !owner.getUniqueId().equals(m_ownerId))
         throw new IllegalArgumentException("Not owner of site.");
      if (!owner.isSignedIn())
         throw new IllegalStateException("Owner not logged in.");
      if (m_x_reference == null)
         return "";
      return fromPaddedUTF8Bytes(SymmetricEncryption.getEngine().decrypt(m_x_reference, owner.getSecretKey()));
   }
   
   /**
    * Get the reference property.
    * @return The reference property.
    * @throws GeneralSecurityException If an error occurred while decrypting the property.
    */
   public final String getReferenceURL(final User owner) throws GeneralSecurityException
   {
      final String ref = getReference(owner);
      if (ref.toLowerCase().indexOf("http") != 0)
         return "http://" + ref;
      return ref;
   }
   
   /**
    * Get the login property.
    * @return The login property.
    * @throws GeneralSecurityException If an error occurred while decrypting the property.
    */
   public final String getLogin(final User owner) throws GeneralSecurityException
   {
      if ((owner == null) || !owner.getUniqueId().equals(m_ownerId))
         throw new IllegalArgumentException("Not owner of site.");
      if (!owner.isSignedIn())
         throw new IllegalStateException("Owner not logged in.");
      if (m_x_login == null)
         return "";
      return fromPaddedUTF8Bytes(SymmetricEncryption.getEngine().decrypt(m_x_login, owner.getSecretKey()));
   }
   
   /**
    * Get the password property.
    * @return The password property.
    * @throws GeneralSecurityException If an error occurred while decrypting the property.
    */
   public final String getPassword(final User owner) throws GeneralSecurityException
   {
      if ((owner == null) || !owner.getUniqueId().equals(m_ownerId))
         throw new IllegalArgumentException("Not owner of site.");
      if (!owner.isSignedIn())
         throw new IllegalStateException("Owner not logged in.");
      if (m_x_password == null)
         return "";
      return fromPaddedUTF8Bytes(SymmetricEncryption.getEngine().decrypt(m_x_password, owner.getSecretKey()));
   }
   
   /**
    * Get the key property.
    * @return The key property.
    * @throws GeneralSecurityException If an error occurred while decrypting the property.
    */
   public final String getKey(final User owner) throws GeneralSecurityException
   {
      if ((owner == null) || !owner.getUniqueId().equals(m_ownerId))
         throw new IllegalArgumentException("Not owner of site.");
      if (!owner.isSignedIn())
         throw new IllegalStateException("Owner not logged in.");
      if (m_x_key == null)
         return "";
      return fromPaddedUTF8Bytes(SymmetricEncryption.getEngine().decrypt(m_x_key, owner.getSecretKey()));
   }
   
   /**
    * Get the comment property.
    * @return The comment property.
    * @throws GeneralSecurityException If an error occurred while decrypting the property.
    */
   public final String getComment(final User owner) throws GeneralSecurityException
   {
      if ((owner == null) || !owner.getUniqueId().equals(m_ownerId))
         throw new IllegalArgumentException("Not owner of site.");
      if (!owner.isSignedIn())
         throw new IllegalStateException("Owner not logged in.");
      if (m_x_comment == null)
         return "";
      return fromPaddedUTF8Bytes(SymmetricEncryption.getEngine().decrypt(m_x_comment, owner.getSecretKey()));
   }
   
   /**
    * Get the image property.
    * @return The image property (as an image b64 string.)
    * @throws GeneralSecurityException If an error occurred while decrypting the property.
    */
   public final String getImageB64(final User owner) throws GeneralSecurityException
   {
      if ((owner == null) || !owner.getUniqueId().equals(m_ownerId))
         throw new IllegalArgumentException("Not owner of site.");
      if (!owner.isSignedIn())
         throw new IllegalStateException("Owner not logged in.");
      if (m_x_imageB64 == null)
         return DEFAULT_IMAGE;
      return fromPaddedUTF8Bytes(SymmetricEncryption.getEngine().decrypt(m_x_imageB64.getBytes(), owner.getSecretKey()));
   }
   
   /********************************************************************************************************************************
    ****************  __   __              ___  ___ 
    **************** |__) |__) | \  /  /\   |  |__  
    **************** |    |  \ |  \/  /~~\  |  |___
    ****************  
    ********************************************************************************************************************************/

   private static final long serialVersionUID = 1L;
   
   private static final byte[] SITE_PKEY_512 = { 73, 15, -61, -7, -8, -56, -52, 6, 67, 113, -106, 12, -104, 29, 23, -29, -81, -83, 16, -90, -34, 19, -49, 11, -28, 54, -110, -80, -123, 110, 0, -78, 100, 116, 37, 120, -18, -10, 109, -125, 71, 66, 28, -27, -78, -53, -103, 21, -58, -71, 126, 89, -104, -14, 104, 30, 45, -44, 82, 100, 115, -15, -124, 66 };
   private static final short MIN_PADDING_LENGTH = 16;
   private static final short MAX_PADDING_LENGTH = 64;

   private static final String DEFAULT_NAME = "[New Site]";
   private static final String DEFAULT_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHEAAABMCAIAAADV+M/0AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcE"
                           + "hZcwAADsMAAA7DAcdvqGQAAAAadEVYdFNvZnR3YXJlAFBhaW50Lk5FVCB2My41LjExR/NCNwAAAWxJREFUeF7t1FFShDAQAFHvfyqOxBEcSYxTxA2Ldqhlq9/XZAgfdLl+"
                           + "rKLZlGdTnk15NuXZlGdTnk15NuXZlGdTnk15NuXZlGdTnk15NuXZlGdTnk15NuXZlGdTnk15NuXZlGdTnk15NuXZlGdTnk15NuXNarosS52+9ZuBU5dfzcSmuy63znTK3L"
                           + "/T3NGm/zVuGnNRz532qAzb3Z9Nm4uyCfW8aZu8L8tQz3NM/386GMKjz8uXH827Ifz6tF+GPONu0LQM4fDF8VuHr1OmNw1lPvVth5d3c9M2ZQh5mZXlDLdv+rflVFc0DXFs"
                           + "m/woz9nh5f5CDE8uQ55xFzUNu08q6rnTHuU7g7kcn1yGep5jVtPXMbtg7z2b5o42xXz9wjf1fKH3/+1fz6Y8m/JsyrMpz6Y8m/JsyrMpz6Y8m/JsyrMpz6Y8m/JsyrMpz6"
                           + "Y8m/JsyrMpz6Y8m/JsyrMpz6Y8m/JsyrMpz6a0df0EBKESM4jGP8UAAAAASUVORK5CYII=";
   
   @Transient             private String    m_name;
   @Id                    private Key       m_primaryKey;   // = f(owner, name)
   @Basic(optional=false) private String    m_ownerId;
   @Basic(optional=false) private byte[]    m_x_name;
   @Basic                 private byte[]    m_x_reference;
   @Basic                 private byte[]    m_x_login;
   @Basic                 private byte[]    m_x_password;
   @Basic                 private byte[]    m_x_key;
   @Basic                 private byte[]    m_x_comment;
   @Basic                 private Blob      m_x_imageB64;

   /**
    * Gets the UTF8 bytes of a string, and add padding using the standard site padding policy.
    * @param s The input string.
    * @return The padded UTF8 bytes.
    */
   private static byte[] toPaddedUTF8Bytes(final String s)
   {
      final int l = Math.max(s.length(), 16);
      return RandomPadding.pad(ByteTools.toUTF8Bytes(s), l + MIN_PADDING_LENGTH, l + MAX_PADDING_LENGTH);
   }

   /**
    * Gets the string from its padded UTF8 bytes.
    * @param bytes The UTF8 bytes.
    * @return The original string.
    */
   private static String fromPaddedUTF8Bytes(final byte[] bytes)
   {
      return ByteTools.fromUTF8Bytes(RandomPadding.unpad(bytes));
   }
   
   /**
    * Generate a site's datastore key given its owner and name.
    * @param owner The owner.
    * @param name The site's name.
    * @return The unique datastore key of this site.
    */
   private static Key generateDatastoreKey(final User owner, final String name)
   {
      return generateDatastoreKey(generateSiteId(owner, name));
   }
   
   /**
    * Generate a site's datastore key given its id (derived from the owner and the site name.
    * @param siteId The site id
    * @return The unique datastore key of this site.
    */
   private static Key generateDatastoreKey(final String siteId)
   {
      if ((siteId == null) || siteId.isEmpty())
         throw new IllegalArgumentException("Invalid site id.");
      return DatastoreTools.createKey(Site.class, siteId);
   }
   
   /**
    * Return the site's hashed unique id.
    * @param owner The owner.
    * @param name The site name. 
    * @return The site's hashed unique id.
    */
   private static String generateSiteId(final User owner, final String name)
   {
      if (owner == null)
         throw new IllegalArgumentException("Owner is null.");
      if (!owner.isSignedIn())
         throw new IllegalStateException("Owner not logged in.");
      if ((name == null) || name.isEmpty())
         throw new IllegalArgumentException("Site's name is either null or empty.");
      return HashFunctions.getSecureHashInstance().hashToHexaString(Salt.salt(ByteTools.toUTF8Bytes(owner.getImmutableUserId() + "." + name.toLowerCase()), SITE_PKEY_512));
   }

   /**
    * rePad the name.
    * @param owner This site's owner
    * @throws GeneralSecurityException If an error occurred while encrypting the name.
    */
   private final void padName(final String name, final User owner) throws GeneralSecurityException
   {
      if ((name == null) || name.isEmpty())
         throw new IllegalArgumentException("Site name is either null or empty.");
      if ((owner == null) || !owner.getUniqueId().equals(m_ownerId))
         throw new IllegalArgumentException("Not owner of site.");
      if (!owner.isSignedIn())
         throw new IllegalStateException("Owner not logged in.");
      m_x_name  = SymmetricEncryption.getEngine().encrypt(toPaddedUTF8Bytes(name), owner.getSecretKey());
   }

   /**
    * Set the reference property.
    * @param value The reference property.
    * @param owner This site's owner
    * @throws GeneralSecurityException If an error occurred while encrypting the property.
    */
   private final void setReference(String value, final User owner) throws GeneralSecurityException
   {
      if ((owner == null) || !owner.getUniqueId().equals(m_ownerId))
         throw new IllegalArgumentException("Not owner of site.");
      if (!owner.isSignedIn())
         throw new IllegalStateException("Owner not logged in.");
      if (value == null)
         value = "";
      m_x_reference = SymmetricEncryption.getEngine().encrypt(toPaddedUTF8Bytes(value), owner.getSecretKey());
   }
   
   /**
    * Set the login property.
    * @param value The login property.
    * @param owner This site's owner
    * @throws GeneralSecurityException If an error occurred while encrypting the property.
    */
   private final void setLogin(String value, final User owner) throws GeneralSecurityException
   {
      if ((owner == null) || !owner.getUniqueId().equals(m_ownerId))
         throw new IllegalArgumentException("Not owner of site.");
      if (!owner.isSignedIn())
         throw new IllegalStateException("Owner not logged in.");
      if (value == null)
         value = "";
      m_x_login = SymmetricEncryption.getEngine().encrypt(toPaddedUTF8Bytes(value), owner.getSecretKey());
   }
   
   /**
    * Set the password property.
    * @param value The password property.
    * @param owner This site's owner
    * @throws GeneralSecurityException If an error occurred while encrypting the property.
    */
   private final void setPassword(String value, final User owner) throws GeneralSecurityException
   {
      if ((owner == null) || !owner.getUniqueId().equals(m_ownerId))
         throw new IllegalArgumentException("Not owner of site.");
      if (!owner.isSignedIn())
         throw new IllegalStateException("Owner not logged in.");
      if (value == null)
         value = "";
      m_x_password = SymmetricEncryption.getEngine().encrypt(toPaddedUTF8Bytes(value), owner.getSecretKey());
   }
   
   /**
    * Set the key property.
    * @param value The key property.
    * @param owner This site's owner
    * @throws GeneralSecurityException If an error occurred while encrypting the property.
    */
   private final void setKey(String value, final User owner) throws GeneralSecurityException
   {
      if ((owner == null) || !owner.getUniqueId().equals(m_ownerId))
         throw new IllegalArgumentException("Not owner of site.");
      if (!owner.isSignedIn())
         throw new IllegalStateException("Owner not logged in.");
      if (value == null)
         value = "";
      m_x_key = SymmetricEncryption.getEngine().encrypt(toPaddedUTF8Bytes(value), owner.getSecretKey());
   }
   
   /**
    * Set the comment property.
    * @param value The comment property.
    * @param owner This site's owner
    * @throws GeneralSecurityException If an error occurred while encrypting the property.
    */
   private final void setComment(String value, final User owner) throws GeneralSecurityException
   {
      if ((owner == null) || !owner.getUniqueId().equals(m_ownerId))
         throw new IllegalArgumentException("Not owner of site.");
      if (!owner.isSignedIn())
         throw new IllegalStateException("Owner not logged in.");
      if (value == null)
         value = "";
      m_x_comment = SymmetricEncryption.getEngine().encrypt(toPaddedUTF8Bytes(value), owner.getSecretKey());
   }
   
   /**
    * Set the image property.
    * @param value The image property (as an image b64 string.)
    * @param owner This site's owner
    * @throws GeneralSecurityException If an error occurred while encrypting the property.
    */
   private final void setImageB64(String value, final User owner) throws GeneralSecurityException
   {
      if ((owner == null) || !owner.getUniqueId().equals(m_ownerId))
         throw new IllegalArgumentException("Not owner of site.");
      if (!owner.isSignedIn())
         throw new IllegalStateException("Owner not logged in.");
      if ((value == null) || value.isEmpty())
         value = DEFAULT_IMAGE;
      m_x_imageB64 = new Blob(SymmetricEncryption.getEngine().encrypt(toPaddedUTF8Bytes(value), owner.getSecretKey()));
   }

   /**
    * Method that <b>must absolutely be called after reloading</b> a owner from DB, in order to make sure that all transient (non-persistent) members
    * are correctly populated.
    * @param owner The owner.
    * @throws GeneralSecurityException If an error occurred while decrypting the name.
    */
   private final void postLoad(final User owner) throws GeneralSecurityException
   {
      m_name = getName(owner);
   }
}
