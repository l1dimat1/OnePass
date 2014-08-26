package com.infinite.share.auth.event;

import com.infinite.share.auth.User;

import java.util.HashSet;
import java.util.Set;

/**
 * The authorizations manager is in charge of dispatching authorization related events to all registered AuthListener instances.
 * 
 */
public class AuthEventManager
{
   private static Set<AuthListener> _authListeners = new HashSet<AuthListener>();
   
   /**
    * Adds an AuthListener. Each AuthListener added by this method will be forwarded all AuthManager related events.
    * The order in which events are forwarded is the same as the order in which listeners were added with this method.
    * @param authListener The authorization listener
    */
   public static void addAuthListener(final AuthListener authListener)
   {
      _authListeners.add(authListener);
   }
   
   /**
    * Dispatch the "passwordChanged" event to all Auth listeners
    * @param userBeforeChange A copy of the user object, in the sate it was just before the password change. 
    * @param userAfterChange The user instance, after the password change.
    * @throws AuthListenerException Whenever a registered AuthListener throws such exception.
    */
   public static void passwordChanged(final User userBeforeChange, final User userAfterChange) throws AuthListenerException
   {
      for (final AuthListener l: _authListeners)
         l.passwordChanged(userBeforeChange, userAfterChange);
   }
}
