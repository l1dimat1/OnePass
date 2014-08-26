package com.infinite.share.net.mail;

import static javax.mail.Message.RecipientType.BCC;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/********************************************************************************************************************************
 * A builder for the javax.mail.internet.MimeMessage class.
 ********************************************************************************************************************************/
public class MimeMessageBuilder
{  
   private InternetAddress       m_sender;
   private List<InternetAddress> m_replyTos;
   private List<Recipient>       m_recipients;
   private String                m_subject;
   private String                m_content;
   private String                m_contentType;
   private Properties            m_properties;

   /**
    * Instantiates a new MimeMessageBuilder.
    */
   private MimeMessageBuilder() 
   {
      m_sender      = null;
      m_replyTos    = new LinkedList<InternetAddress>();
      m_recipients  = new LinkedList<Recipient>();
      m_subject     = null;
      m_content     = null;
      m_contentType = null;
      m_properties  = new Properties();
   }
   
   /**
    * Instantiates a new MimeMessageBuilder.
    */
   public static MimeMessageBuilder newInstance() 
   {
      return new MimeMessageBuilder();
   }
   
   /**
    * Set the sender.
    * @param sender The sender's address.
    * @return This builder.
    */
   public MimeMessageBuilder setSender(final InternetAddress sender)
   {
      m_sender = sender;
      return this;
   }
   
   /**
    * Add an address to which the replies should be redirected.
    * @param address The address.
    * @return This builder.
    */
   public MimeMessageBuilder addReplyTo(final InternetAddress address)
   {
      m_replyTos.add(address);
      return this;
   }
   
   /**
    * Add a "TO" recipient.
    * @param address The recipient's address.
    * @return This builder.
    */
   public MimeMessageBuilder addToRecipient(final InternetAddress address)
   {
      m_recipients.add(new Recipient(TO, address));
      return this;
   }
   
   /**
    * Add a "CC" recipient.
    * @param address The recipient's address.
    * @return This builder.
    */
   public MimeMessageBuilder addCcRecipient(final InternetAddress address)
   {
      m_recipients.add(new Recipient(CC, address));
      return this;
   }
   
   /**
    * Add a "BCC" recipient.
    * @param address The recipient's address.
    * @return This builder.
    */
   public MimeMessageBuilder addBccRecipient(final InternetAddress address)
   {
      m_recipients.add(new Recipient(BCC, address));
      return this;
   }
   
   /**
    * Set the subject.
    * @param subject The message's subject.
    * @return This builder.
    */
   public MimeMessageBuilder setSubject(final String subject)
   {
      m_subject = subject;
      return this;
   }
   
   /**
    * Set the content, as MIME type "text/html".
    * @param subject The message's subject.
    * @return This builder.
    */
   public MimeMessageBuilder setHtmlContent(final String content)
   {
      m_content = content;
      m_contentType = "text/html; charset=utf-8";
      return this;
   }
   
   /**
    * Set the content, as MIME type "text/html".
    * @param subject The message's subject.
    * @return This builder.
    */
   public MimeMessageBuilder setPlainTextContent(final String content)
   {
      m_content = content;
      m_contentType = "text/plain; charset=utf-8";
      return this;
   }

   /**
    * Set a new session property.
    * @param subject The message's subject.
    * @return This builder.
    */
   public MimeMessageBuilder setSessionProperty(final String key, final String value)
   {
      m_properties.setProperty(key, value);
      return this;
   }

   /**
    * Creates a new MIME message based on the information contained in the builder.
    * @return The new built MIME message.
    * @throws MessagingException
    */
   public MimeMessage build() throws MessagingException
   {
      final MimeMessage msg = new MimeMessage(Session.getDefaultInstance(m_properties, null));
      
      msg.setFrom(m_sender);
      msg.setReplyTo(m_replyTos.toArray(new InternetAddress[m_replyTos.size()]));
      
      for (Recipient r: m_recipients)
         msg.addRecipient(r.getType(), r.getAddress());

      msg.setSubject(m_subject, "UTF-8");      
      msg.setContent(m_content, m_contentType);

      return msg;
   }

   /** *****************************************************************************************************************************
    * An email recipient: an address and a type of recipient (RecipientType)
    * ******************************************************************************************************************************/
   private static class Recipient
   {
      private final RecipientType m_type;
      private final InternetAddress           m_address;
      
      /**
       * Instantiates a new recipient.
       * @param type The type of recipient: RecipientType.{ BCC || CC || TO}
       */
      Recipient(final RecipientType type, final InternetAddress address)
      {
         m_type = type;
         m_address = address;
      }

      /**
       * Return the type of recipient.
       * @return The type of recipient.
       */
      private RecipientType getType()
      {
         return m_type;
      }
      
      /**
       * Return the recipient's address.
       * @return The recipient's address.
       */
      private InternetAddress getAddress()
      {
         return m_address;
      }
   }
}
