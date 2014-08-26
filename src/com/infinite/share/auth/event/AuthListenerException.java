package com.infinite.share.auth.event;

/**
 * Throw this exception whenever processing an event within an AuthListener, in order to inform the event source that the even could not be correctly processed.
 */
@SuppressWarnings("serial")
public class AuthListenerException extends Exception
{
   /**
    * Instantiates a new AuthListenerException
    * {@inheritDoc}
    */
   public AuthListenerException(final String message)
   {
      super(message);
   }
   
   /**
    * Instantiates a new AuthListenerException
    * {@inheritDoc}
    */
   public AuthListenerException(final String message, final Throwable cause)
   {
      super(message, cause);
   }
   
   /**
    * Instantiates a new AuthListenerException
    * {@inheritDoc}
    */
   public AuthListenerException(final Throwable cause)
   {
      super(cause);
   }
}
