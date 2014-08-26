package com.infinite.share.auth.event;

import com.infinite.share.auth.User;

/**
 * Implement this interface, and add a new instance of this listener to the AuthManager in order to be informed of changes in the authorizations.
 */
public interface AuthListener
{
   /**
    * Method called whenever the user password is changed.
    * @param userBeforeChange A copy of the user object, in the sate it was just before the password change. 
    * @param userAfterChange The user instance, after the password change. 
    * @throws AuthListenerException Whenever something could not be correctly processed.
    */
   public void passwordChanged(final User userBeforeChange, final User userAfterChange) throws AuthListenerException;
}
