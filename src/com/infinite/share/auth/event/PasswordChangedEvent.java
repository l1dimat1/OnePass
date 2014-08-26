package com.infinite.share.auth.event;

import com.infinite.share.auth.User;

/**
 * Event dispatched when password is changed.
 */
public final class PasswordChangedEvent
{
   private final User m_userBeforeChange;
   private final User m_userAfterChange;
   
   /**
    * Instantiates a new PasswordChangedEvent
    * @param userBeforeChange A copy of the user object, in the sate it was just before the password change. 
    * @param userAfterChange The user instance, after the password change.
    */
   PasswordChangedEvent(final User userBeforeChange, final User userAfterChange)
   {
      m_userBeforeChange = userBeforeChange;
      m_userAfterChange  = userAfterChange;
   }
   
   /**
    * Return a copy of the user object, in the state it was just before the password change.
    * @return A copy of the user object, in the state it was just before the password change. 
    */
   public User getUserBeforeChange()
   {
      return this.m_userBeforeChange;
   }
   
   /**
    * Return the user instance, after the password change.
    * @return The user instance, after the password change. 
    */
   public User getUserAfterChange()
   {
      return this.m_userAfterChange;
   }
}
