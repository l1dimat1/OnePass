package com.infinite.share.auth;

import com.google.appengine.api.datastore.Key;
import com.infinite.share.Application;
import com.infinite.share.net.http.HomePages;
import com.infinite.share.net.http.auth.AuthPages;
import com.infinite.share.net.mail.EmailAddressFactory;
import com.infinite.share.net.mail.InfiniteAddressBook;
import com.infinite.share.net.mail.MimeMessageBuilder;
import com.infinite.share.persistence.DatastoreTools;
import com.infinite.share.persistence.Persistent;
import com.infinite.share.security.RandomKeyGenerator;
import com.infinite.share.security.Salt;
import com.infinite.share.security.hash.HashFunctions;
import com.infinite.share.security.symmetric.SymmetricEncryption;
import com.infinite.share.tools.ByteTools;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.crypto.SecretKey;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityExistsException;
import javax.persistence.Id;
import javax.persistence.Transient;

/********************************************************************************************************************************
 * This class represents an invitation, sent from one existing user to an email address.
 * It relies on a random, expiring key, stored in database, and associated to an inviter's user id and an invitee's email address.
 * The random key, called invitation code, ensures that only people who have been invited can sign up.
 ********************************************************************************************************************************/
@Entity
public final class Invitation implements Persistent
{  
   /**
    * Generates a new invitation key, and instantiates a new invitation.
    * @param inviteName The id of the user issuing the invitation.
    * @param inviteeEmailAddress The email address to which the invitation will be sent, and with which the invitee will have to sign-up.
    * @throws GeneralSecurityException If a security exception occurred during the email address or the expiration time encryption.
    */
   public Invitation(final String inviteName, final String inviteeEmailAddress) throws GeneralSecurityException
   {
      if (!EmailAddressFactory.verifyAddressFormat(inviteeEmailAddress))
         throw new IllegalArgumentException("Email address format not correct.");
      m_inviteName           = inviteName;
      m_inviteeEmailAddress = inviteeEmailAddress;
      m_primaryKey          = generateDatastoreKey(generateInvitationKey());
      m_h_verificationCode  = generateVerificationCode();
      m_secretKey           = generateSecretKey();
      setExpiryTime(Calendar.getInstance().getTimeInMillis() + EXPIRY_PERIOD_IN_MS);
   }
   
   /**
    * Generates a new invitation key, and instantiates a new invitation.
    * @param inviter The user issuing the invitation.
    * @param inviteeEmailAddress The email address to which the invitation will be sent, and with which the invitee will have to sign-up.
    * @throws GeneralSecurityException If a security exception occurred during the email address or the expiration time encryption.
    */
   public Invitation(final User inviter, final String inviteeEmailAddress) throws GeneralSecurityException
   {
      this(inviter.getDisplayName(), inviteeEmailAddress);
   }

   /**
    * Protected default constructor, as required by the app-engine / JPA API.
    * Should not be called, except by JPA.
    */
   protected Invitation()
   {
      m_primaryKey         = null;
      m_secretKey          = null;
      m_h_verificationCode = null;
      m_x_expiryTime       = null;
      m_deletionTime       = 0;
   }

   /**
    * Load user data from the datastore, and put it in a new user instance. 
    * @param invitationUniqueKey The invitation key.
    * @param inviterName The name of the user who issued the invitation.
    * @param inviteeEmailAddress The email address of the user trying to sign up with this invitation key.
    * @return The invitation object, restored from datastore, or null if an error occurred.
    * @throws IncorrectKeyException In case the email address provided are incorrect.
    * @throws GeneralSecurityException In case the secret key could not be generated.
    */
   public static Invitation restore(final String invitationUniqueKey, final String inviterName, final String inviteeEmailAddress) throws GeneralSecurityException, IncorrectKeyException
   {
      final Key uniqueKey = generateDatastoreKey(invitationUniqueKey);
      Invitation invitation = DatastoreTools.selectOne(Invitation.class, uniqueKey);

      if (invitation != null)
      {
         invitation.postLoad(inviterName, inviteeEmailAddress);
         
         if (!invitation.isEmailAddressCorrect(inviteeEmailAddress))
            throw new IncorrectKeyException("The email address does not correspond to this invitation key.");
      }

      return invitation;
   }

   public static int deleteExpiredInvitations() throws GeneralSecurityException
   {
      final long now = Calendar.getInstance().getTimeInMillis();
      final List<Invitation> expiredInvitations = DatastoreTools.query(Invitation.class, "SELECT o from Invitation o WHERE o.m_deletionTime < " + now);

      int nDeleted = 0;
      for (Invitation invitation: expiredInvitations)
      {
         if (invitation.delete())
            nDeleted++;
      }
      
      return nDeleted;
   }
   
   /**
    * Delete this invitation from the datastore.
    * @return True if the invitation was found and deleted.
    */
   public final boolean delete()
   {
      return DatastoreTools.delete(this);
   }

   /**
    * Insert (persist) this invitation in the datastore. Fails in case an invitation with the same unique key already exists.
    * @return True is the invitation was successfully inserted. False otherwise, in particular if an invitation with the same unique key already exists.
    * @throws EntityExistsException If an invitation with the same unique key already exists in the datastore.
    */
   public boolean persistInsert() throws EntityExistsException
   {
      return DatastoreTools.insert(this);
   }

   /**
    * Verify that the inviter'id and invitee's email address correspond to this validation key.
    * @param inviteeEmailAddress The email address to which the invitation is sent
    * @return True is the validation key is correct, false otherwise.
    */
   public boolean isEmailAddressCorrect(final String inviteeEmailAddress)
   {
      if ((m_h_verificationCode == null) || (m_h_verificationCode.length == 0))
         throw new IllegalStateException("The verification code is not set");

      return Arrays.equals(generateVerificationCode(m_inviteName, inviteeEmailAddress, getSalt()), m_h_verificationCode);
   }

   /**
    * Check whether this invitation has already expired or not yet.
    * @return True if this invitation has expired, false otherwise.
    * @throws GeneralSecurityException If an error occurs during expiry time encryption.
    */
   public boolean hasExpired() throws GeneralSecurityException
   {
      return Calendar.getInstance().getTimeInMillis() > getExpiryTime();
   }

   /**
    * Checks whether this invitation can be deleted.
    * @return True if this invitation can be deleted, false otherwise.
    */
   public boolean isEligibleForDeletion()
   {
      return Calendar.getInstance().getTimeInMillis() > m_deletionTime;
   }

   /**
    * Send the invitation by email to the invitee's email address.
    * @return True if the invitation was sent properly, false otherwise.
    */
   public boolean send()
   {
      try
      {
         final String signUpUrl = AuthPages.signUp(getInvitationKey(), m_inviteName, m_inviteeEmailAddress, true);
         MimeMessage msg = MimeMessageBuilder.newInstance()
                                 .setSender(InfiniteAddressBook.noReplyAddress())
                                 .addToRecipient(EmailAddressFactory.createAddress(m_inviteeEmailAddress))
                                 .setSubject("[" + Application.getFullApplicationName() + "] Invitation to join " + Application.getFullApplicationName())
                                 .setHtmlContent("Hello<br><br>"
                                               + m_inviteName + " would like to invite you to join Infinite.<br><br>"
                                               + "To join, simply click <a href=\"" + signUpUrl + "\">here</a>, and sign-up using this email address.<br><br>"
                                               + "In case the above link does not work, you can copy and paste the below URL to your favorite Web browser:<br>"
                                               + "---------------- do not copy this line ----------------<br>"
                                               + signUpUrl + "<br>"
                                               + "---------------- do not copy this line ----------------<br><br>"
                                               + "See you soon on <a href=\"" + HomePages.home(true) + "\">" + Application.getApplicationName() + "</a>!<br><br>"
                                               + "The " + Application.getApplicationName() + " team")
                                 .build();
         Transport.send(msg);
         return true;
      }
      catch (final MessagingException e) // Including SendFailedException
      {
      }
      return false;
   }

   /********************************************************************************************************************************
    ****************  __   __              ___  ___ 
    **************** |__) |__) | \  /  /\   |  |__  
    **************** |    |  \ |  \/  /~~\  |  |___
    ****************  
    ********************************************************************************************************************************/
   
   private static final long serialVersionUID = 1L;
   
   private static final long EXPIRY_PERIOD_IN_DAYS = 14;
   private static final long EXPIRY_PERIOD_IN_MS = EXPIRY_PERIOD_IN_DAYS * 24 * 60 * 60 * 1000;
   private static final int EXPIRY_RANDOMNESS_IN_MS = 60 * 60 * 1000;   // 1h
   private static final int DELETION_RANDOMNESS_IN_MS = 10 * 24 * 60 * 60 * 1000;   // 10 days

   @Transient             private String    m_inviteName;
   @Transient             private String    m_inviteeEmailAddress;
   @Transient             private SecretKey m_secretKey;
   @Id                    private Key       m_primaryKey;
   @Basic(optional=false) private byte[]    m_h_verificationCode;
   @Basic(optional=false) private byte[]    m_x_expiryTime;
   @Basic(optional=false) private long      m_deletionTime;

   /**
    * Generates a new, random, 64 bytes invitation key.
    * @return A new, random invitation key.
    */
   private static String generateInvitationKey()
   {
      return ByteTools.toHexaString(RandomKeyGenerator.generateKey(64));
   }
   
   /**
    * Generate an invitation's datastore key given its invitation key.
    * @param invitationUniqueKey The invitation key.
    * @return The datastore key corresponding to this invitation key.
    */
   private static Key generateDatastoreKey(final String invitationUniqueKey)
   {
      if ((invitationUniqueKey == null) || invitationUniqueKey.isEmpty())
         throw new IllegalArgumentException("Invitation key is either null or empty.");
      return DatastoreTools.createKey(Invitation.class, invitationUniqueKey);
   }
   
   /**
    * Return the invitation key.
    * @return The invitation key.
    */
   String getInvitationKey()
   {
      return m_primaryKey.getName();
   }

   /**
    * Return the SALT used by this invitation.
    * This SALT is directly deduced from the invitation key (the name of the unique key.)
    * @return The SALT.
    */
   private byte[] getSalt()
   {
      if (m_primaryKey == null)
         throw new IllegalStateException("The invitation unique key is not set.");

      return ByteTools.fromHexaString(getInvitationKey());
   }

   /**
    * Add noise to the expiry time, deduce the deletion time, encrypt the expiry time and set both.
    * @param expiryTime The expiry time
    * @throws GeneralSecurityException If an error occurs during expiry time encryption.
    */
   private void setExpiryTime(final long expiryTime) throws GeneralSecurityException
   {
      if (m_secretKey == null)
         throw new IllegalStateException("The secret key is not set.");
      
      final SecureRandom rnd = RandomKeyGenerator.newSecureRandom();
      final long randomizedExpiryTime = expiryTime + rnd.nextInt(EXPIRY_RANDOMNESS_IN_MS) - rnd.nextInt(EXPIRY_RANDOMNESS_IN_MS);
      
      m_x_expiryTime = SymmetricEncryption.getEngine().encrypt(ByteTools.longToBytes(randomizedExpiryTime), m_secretKey);
      m_deletionTime = randomizedExpiryTime + rnd.nextInt(DELETION_RANDOMNESS_IN_MS);
   }

   /**
    * Decrypt and return the expiry time.
    * @return The expiry time.
    * @throws GeneralSecurityException If an error occurs during expiry time encryption.
    */   
   private long getExpiryTime() throws GeneralSecurityException
   {
      if (m_secretKey == null)
         throw new IllegalStateException("The secret key is not set.");

      return ByteTools.longFromBytes(SymmetricEncryption.getEngine().decrypt(m_x_expiryTime, m_secretKey));
   }

   /**
    * Generate a invitation's secret key.
    * @return The generated secret key.
    * @throws GeneralSecurityException If the secret key could not be generated.
    */
   private SecretKey generateSecretKey() throws GeneralSecurityException
   {
      if ((m_inviteName == null) || m_inviteName.isEmpty())
         throw new IllegalStateException("The inviter id is either null or empty.");
      if ((m_inviteeEmailAddress == null) || m_inviteeEmailAddress.isEmpty())
         throw new IllegalStateException("The invitee's email address is either null or empty.");
      if (m_primaryKey == null)
         throw new IllegalStateException("Invitation unique key is not set.");

      return SymmetricEncryption.getEngine().generateSecretKey((m_inviteName + m_inviteeEmailAddress).toCharArray(), getSalt());
   }

   /**
    * Generates the unique verification code corresponding to this invitation's inviter, invitee's email address and invitation code.
    * @param inviteName The name of the user sending the invitation.
    * @param inviteeEmailAddress The email address to which this invitation will be sent.
    * @param salt The salt of this invitation.
    * @return The verification code.
    */
   private static byte[] generateVerificationCode(final String inviteName, final String inviteeEmailAddress, final byte[] salt)
   {
      return HashFunctions.getSecureHashInstance().hash(Salt.salt(ByteTools.toUTF8Bytes(inviteName + inviteeEmailAddress), salt));
   }

   /**
    * Generates the unique verification code corresponding to this invitation's inviter, invitee's email address and invitation code.
    * @return The verification code.
    */
   private byte[] generateVerificationCode()
   {
      return generateVerificationCode(m_inviteName, m_inviteeEmailAddress, getSalt());
   }
   
   /**
    * Method that <b>must absolutely be called after reloading</b> an invitation from DB, in order to make sure that all non-persistent
    * members are correctly populated.
    * @param inviterName The name of the inviter
    * @param inviteeEmailAddress The email address to which the invitation is sent
    * @throws GeneralSecurityException If the secret key could not be generated.
    */
   private void postLoad(final String inviterName, final String inviteeEmailAddress) throws GeneralSecurityException
   {
      m_inviteName           = inviterName;
      m_inviteeEmailAddress = inviteeEmailAddress;
      m_secretKey           = generateSecretKey();
   }
}
