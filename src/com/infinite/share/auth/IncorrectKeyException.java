package com.infinite.share.auth;

/********************************************************************************************************************************
 * Exception thrown when the password provided with a user id is incorrect.
 ********************************************************************************************************************************/
@SuppressWarnings("serial")
public class IncorrectKeyException extends Exception
{
   /**
    * Instantiate a new IncorrectPasswordException with the message passed in argument.
    * @param message The exception message.
    */
   IncorrectKeyException(final String message)
   {
      super(message);
   }
}
