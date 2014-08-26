package com.infinite.share.auth;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.infinite.share.auth.event.AuthEventManager;
import com.infinite.share.persistence.DatastoreTools;
import com.infinite.share.persistence.Persistent;
import com.infinite.share.security.Salt;
import com.infinite.share.security.hash.HashFunctions;
import com.infinite.share.security.symmetric.SymmetricEncryption;
import com.infinite.share.tools.ByteTools;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityExistsException;
import javax.persistence.Id;
import javax.persistence.Transient;

/********************************************************************************************************************************
 * This class represents a user of infinite solutions.
 * It contains most basic profile information, including user id, email address and password.
 * It also implements the security logic used to authenticate users.
 ********************************************************************************************************************************/
@Entity
public final class User implements Persistent
{   
   /**
    * Instantiates a new user given its basic profile information.
    * This constructor should only be called to instantiate a user that is not registered yet. To get an instance of an already
    * registered user, user User.restore instead.
    * @param userId The user id, which should not be registered yet.
    * @param password The password, which validity should have already been checked.
    * @param emailAddress The email address.
    * @throws GeneralSecurityException If a security exception occurred during the email address encryption. 
    */
   public User(final String userId, final String password, final String displayName, final String emailAddress) throws GeneralSecurityException
   {
      if (!isAValidUserId(userId))
         throw new IllegalArgumentException("Invalid user id.");
      if (!isAValidPassword(password))
         throw new IllegalArgumentException("Invalid password.");
      if ((displayName == null) || displayName.isEmpty())
         throw new IllegalArgumentException("Empty display name.");
      if ((emailAddress == null) || emailAddress.isEmpty())
         throw new IllegalArgumentException("Empty email address.");

      m_immutableUserId = userId.toLowerCase();
      m_primaryKey      = generateDatastoreKey(m_immutableUserId);
      m_salt            = Salt.generateSalt(64);
      m_h_passwordHash  = hashPassword(password);
      generateSecretKey(m_immutableUserId, password);
      
      m_displayName  = displayName;
      m_emailAddress = emailAddress;
   }

   /**
    * Protected default constructor, as required by the appengine / JPA API.
    * Should not be called, except by JPA.
    */
   protected User()
   {
      m_immutableUserId = null;
      m_primaryKey      = null;
      m_salt            = null;
      m_h_passwordHash  = null;
      m_secretKey       = null;
      m_displayName     = null;
      m_emailAddress    = null;
   }

   /**
    * Load user data from the datastore, verify its password, and returns a new user instance. 
    * @param userId The id of the user to load.
    * @param password The password to be used to restore user data.
    * @return The user object, restored from datastore, or null if an error occurred.
    * @throws IncorrectKeyException In case the password provided is incorrect.
    * @Exception GeneralSecurityException In case the secret key could not be re-generated from the available information. Should never happen. 
    */
   public static User restore(final String userId, final String password) throws GeneralSecurityException, IncorrectKeyException
   {
      final Key uniqueKey = generateDatastoreKey(userId);
      User user = DatastoreTools.selectOne(User.class, uniqueKey);

      if (user != null)
      {
         if (!user.isPasswordCorrect(password))
         {
            user = null;
            throw new IncorrectKeyException("Password provided for user " + userId.toLowerCase() + " is incorrect.");
         }
      
         // User exists and credentials are correct
         user.postLoad(userId, password);
      }
      
      return user;
   }

   /**
    * Simply load user data from the datastore (without verifying its password) and returns a new user instance.
    * Note that without the password set, the user is not logged in, and its encrypted information is therefore unavailable. 
    * @param userId The id of the user to load.
    * @return The user object, restored from datastore, or null if an error occurred. 
    */
   public static User restore(final String userId)
   {
      final Key uniqueKey = generateDatastoreKey(userId);
      return DatastoreTools.selectOne(User.class, uniqueKey);
   }

   /**
    * Verify whether the suggested userId is available (i.e. not registered by anyone yet) or not.
    * Note that the user id is not reserved at the time this method is called, so trying to insert the user id later on might fail in case the user id was
    * reserved in-between.
    * @param userId The user id
    * @return True if the user id is available, false otherwise (if it is already registered)
    */
   public static boolean isUserIdAvailable(final String userId)
   {
      final Key uniqueKey = generateDatastoreKey(userId);
      final User user = DatastoreTools.selectOne(User.class, uniqueKey);
      return user == null;
   }

   /**
    * Verify that the suggested userId respects the rules.
    * @param userId The user id
    * @return True if the user id respects the user id's naming rules.
    */
   public static boolean isAValidUserId(final String userId)
   {
      return (userId != null) && (userId.length() > 4) && (userId.matches("[a-zA-Z0-9._]*"));
   }

   /**
    * Verify that the suggested password respects the rules.
    * @param password The password
    * @return True if the suggested password respects the rules.
    */
   public static boolean isAValidPassword(final String password)
   {
      return (password != null) && (password.length() >= 8);
   }

   /**
    * Insert (persist) this invitation in the datastore. Fails in case a user with the same unique key already exists.
    * @return True is the user was successfully inserted. False otherwise, in particular if a user with the same unique key already exists.
    * @throws EntityExistsException If a user with the same id already exists in the datastore.
    */
   public boolean persistInsert() throws EntityExistsException
   {
      return DatastoreTools.insert(this);
   }

   /**
    * Update (persist) this user in the datastore if it already exists. Previously persisted data is overwritten.
    * @return The persisted user (this or null if an error occurred).
    */
   private User persistUpdate()
   {
      return DatastoreTools.update(this);
   }

   /**
    * Sign the user out (i.e. removes its credentials.)
    * A signed out user will be signin in again before being used for encryption.
    */
   public void signOut()
   {
      m_secretKey = null;
   }
   
   /**
    * Check whether this user's credentials have been verified and are valid.
    * @return True if this user's credentials are valid, false otherwise.
    */
   public boolean isSignedIn()
   {
      return m_secretKey != null;
   }

   /**
    * Verify that the password provided is correct, ie that it produces the same password hash.
    * @param password The password to verify .
    * @return True is the password is correct, false otherwise.
    */
   public boolean isPasswordCorrect(final String password)
   {
      if ((m_h_passwordHash == null) || (m_h_passwordHash.length == 0))
         throw new IllegalStateException("Password hash not set.");

      if ((password == null) || password.isEmpty())
         return false;
 
      return Arrays.equals(hashPassword(password), m_h_passwordHash);
   }
   
   /**
    * Changes this user's password.
    * Dispatching of the "PasswordChanged" event is encapsulated in this method.
    * @param newPassword The new password.
    * @return True if the password change was successful, false otherwise.
    * @throws GeneralSecurityException If an error occurred during decryption / re-encryption of data.
    */
   public boolean changePassword(final String newPassword)
   {
      final User userBeforeChange = new User(this);
      
      Transaction transaction = null;
      try
      {
         transaction = DatastoreTools.beginTransaction();
         if (transaction != null)
         {
            m_salt           = Salt.generateSalt(64);  // New salt for higher security
            m_h_passwordHash = hashPassword(newPassword);
            generateSecretKey(m_immutableUserId, newPassword);
            //m_displayName  (not impacted)
            //m_emailAddress (not impacted)
   
            if (persistUpdate() != null)
            {
               AuthEventManager.passwordChanged(userBeforeChange, this);
               transaction.commit();
               return true;
            }
         }
      }
      catch (final Exception e)
      {
      }
      finally
      {
         if ((transaction != null) && transaction.isActive())
            transaction.rollback();
      }
      return false;
   }

   /**
    * Return the user's id. This id never changes for a give user.
    * @return The user's id.
    */
   public String getImmutableUserId()
   {
      return m_immutableUserId;
   }

   /**
    * Return the user's unique (hashed) id.
    * @return The user's unique id as a string.
    * @throws GeneralSecurityException If a security exception occurred during the user id encryption.
    */
   public String getUniqueId() throws GeneralSecurityException
   {
      return m_primaryKey.getName();
   }

   /**
    * Return the user's secret key
    * @return The user's secret key
    */
   public SecretKey getSecretKey()
   {
      return m_secretKey;
   }

   /**
    * Return the user's display name.
    * @return The user's display name.
    * @throws GeneralSecurityException If a security exception occurred during the email address decryption.
    */
   public String getDisplayName() throws GeneralSecurityException
   {
      return m_displayName;
   }

   /**
    * Return the user's email address.
    * @return The user's email address.
    * @throws GeneralSecurityException If a security exception occurred during the email address decryption.
    */
   public String getEmailAddress() throws GeneralSecurityException
   {
      return m_emailAddress;
   }

   /**
    * Return true if the user id is the administrator's user id.
    * @return True if the user id is the administrator's user id.
    */
   public static boolean isAdministratorUserId(final String userId)
   {
      return (userId != null) && ADMINISTRATOR_USER_ID.equals(userId.toLowerCase());      
   }
   
   /**
    * Return true if the user has administration rights.
    * @return True if the user has administration rights.
    */
   public boolean hasAdministrationRights()
   {
      return isAdministratorUserId(m_immutableUserId);
   }
   
   /********************************************************************************************************************************
    ****************  __   __              ___  ___ 
    **************** |__) |__) | \  /  /\   |  |__  
    **************** |    |  \ |  \/  /~~\  |  |___
    ****************  
    ********************************************************************************************************************************/

   private static final long serialVersionUID = 1L;

   private static final byte[] USER_PKEY_512 = { -3, 68, -52, 85, -49, 51, -31, -26, 40, 26, -87, 1, -55, -46, 81, 5, -124, 48, 82, -70, -57, 108, 126, 47, -46, -58, 26, 87, -28, -29, 109, 96, 6, -101, -30, -34, 15, -100, -46, 22, -36, 118, -15, -18, 96, -26, 90, 112, 52, 23, 122, -123, -11, 46, 12, 81, -68, 11, 13, 15, 30, 50, 9, 29 };
   
   private static final String ADMINISTRATOR_USER_ID = "admin";
   //                                                  1qaz2wsx3edc
   
   @Transient             private String    m_immutableUserId;
   @Transient             private SecretKey m_secretKey;
   @Id                    private Key       m_primaryKey;
   @Basic(optional=false) private byte[]    m_salt;
   @Basic(optional=false) private byte[]    m_h_passwordHash;
   @Basic(optional=false) private String    m_displayName;
   @Basic(optional=false) private String    m_emailAddress;

   /**
    * Copy constructor.
    * @param o The user to copy from.
    */
   private User(final User o)
   {
      m_immutableUserId = o.m_immutableUserId;
      m_secretKey       = o.m_secretKey;
      m_primaryKey      = o.m_primaryKey;
      m_salt            = o.m_salt;
      m_h_passwordHash  = o.m_h_passwordHash;
      m_displayName     = o.m_displayName;
      m_emailAddress    = o.m_emailAddress;
   }
   
   /**
    * Return the user's hashed unique id.
    * @return The user's hashed unique id.
    */
   private static String hashUserId(final String userId)
   {
      return HashFunctions.getSecureHashInstance().hashToHexaString(Salt.salt(ByteTools.toUTF8Bytes(userId.toLowerCase()), USER_PKEY_512));
   }
   
   /**
    * Generate a user's datastore key given its user id.
    * @param userId The user id.
    * @return The unique datastore key corresponding to this user id.
    */
   private static Key generateDatastoreKey(final String userId)
   {
      if ((userId == null) || userId.isEmpty())
         throw new IllegalArgumentException("User id is either null or empty.");
      return DatastoreTools.createKey(User.class, hashUserId(userId.toLowerCase()));
   }
   
   /**
    * Return the hashed password.
    * @return The hashed password.
    */
   byte[] getPasswordHash()
   {
      return m_h_passwordHash;
   }

   /**
    * Salts and hashes the password.
    * @param password The password to salt and hash.
    * @return The salted and hashed password.
    */
   private byte[] hashPassword(final String password)
   {
      if ((m_salt == null) || (m_salt.length == 0))
         throw new IllegalStateException("Salt is not set.");      
      return HashFunctions.getSecureHashInstance().hash(saltPassword(password));
   }
   
   /**
    * Salts the user's password.
    * @param password The password to salt.
    * @return The salted password.
    */
   private byte[] saltPassword(final String password)
   {
      if ((password == null) || password.isEmpty())
         throw new IllegalArgumentException("Password is either null or empty.");
      if ((m_salt == null) || (m_salt.length == 0))
         throw new IllegalStateException("Salt is not set.");

      return Salt.salt(ByteTools.toUTF8Bytes(password), m_salt);
   }

   /**
    * Generate a user's secret key given its password and salt.
    * @param password The password.
    * @throws GeneralSecurityException If the secret key could not be generated.
    */
   private void generateSecretKey(final String userId, final String password) throws GeneralSecurityException
   {
      m_secretKey = SymmetricEncryption.getEngine().generateSecretKey((userId.toLowerCase() + password).toCharArray(), m_salt);
   }
   
   /**
    * Method that <b>must absolutely be called after reloading</b> a user from DB, in order to make sure that all transient (non-persistent) members
    * are correctly populated.
    * @param userId The user id.
    * @param password The password, assumed to be correct.
    * @throws GeneralSecurityException If the secret key could not be generated.
    */
   private void postLoad(final String userId, final String password) throws GeneralSecurityException
   {
      if ((password == null) || password.isEmpty())
         throw new IllegalArgumentException("Password is either null or empty.");
      if ((userId == null) || userId.isEmpty())
         throw new IllegalArgumentException("User id is either null or empty.");

      m_immutableUserId = userId.toLowerCase();
      generateSecretKey(userId, password);
   }
}
