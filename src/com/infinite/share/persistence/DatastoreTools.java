package com.infinite.share.persistence;

// http://en.wikibooks.org/wiki/Java_Persistence/Persisting
// http://localhost:8888/_ah/admin

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/********************************************************************************************************************************
 * A utility class which simplifies usage of the app-engine
 * data store through a set of helper methods.
 ********************************************************************************************************************************/
public final class DatastoreTools
{
   private static final String               _persistenceUnitName  = "transactions-optional";
   private static final EntityManagerFactory _entityManagerFactory;
   
   /**
    * Ensure that this class will never be instantiated / extended.
    */
   private DatastoreTools()
   {
   }

   /* ******************************************************************************************************************************** */
   static
   {
      _entityManagerFactory = Persistence.createEntityManagerFactory(_persistenceUnitName);
   }

   /**
    * Creates a unique datastore key given a persistent object and its id (as a string.)
    * @param oClass The class of the persistent object.
    * @param id The id of the object.
    * @return The create datastore key. 
    */
   public static <T extends Persistent> Key createKey(final Class<T> oClass, final String id)
   {
      return KeyFactory.createKey(getPersistenceKind(oClass), id);
   }

   /**
    * Creates a unique datastore key given a persistent object and its id (as a long.)
    * @param oClass The class of the persistent object.
    * @param id The id of the object.
    * @return The create datastore key. 
    */
   public static <T extends Persistent> Key createKey(final Class<T> oClass, final long id)
   {
      return KeyFactory.createKey(getPersistenceKind(oClass), id);
   }

   /**
    * Creates a unique datastore key given a persistent object, its parent and its id (as a string.)
    * @param parentKey The parent key of the persistent's object.
    * @param oClass The class of the persistent object.
    * @param id The id of the object.
    * @return The create datastore key. 
    */
   public static <T extends Persistent> Key createKey(final Key parentKey, final Class<T> oClass, final String id)
   {
      return KeyFactory.createKey(parentKey, getPersistenceKind(oClass), id);
   }

   /**
    * Creates a unique datastore key given a persistent object, its parent and its id (as a long.)
    * @param parentKey The parent key of the persistent's object.
    * @param oClass The class of the persistent object.
    * @param id The id of the object.
    * @return The create datastore key. 
    */
   public static <T extends Persistent> Key createKey(final Key parentKey, final Class<T> oClass, final long id)
   {
      return KeyFactory.createKey(parentKey, getPersistenceKind(oClass), id);
   }

   /**
    * Begins a transaction against the datastore. Callers are responsible for explicitly calling Transaction.commit() or Transaction.rollback() when they no longer need the Transaction.
    * @return The transaction that was started.
    * @throws DatastoreFailureException
    */
   public static Transaction beginTransaction()
   {
      return DatastoreServiceFactory.getDatastoreService().beginTransaction();
   }
   
   /**
    * Return the persistence 'kind' of the object which class is passed as argument.
    * @return The persistence 'kind'
    */
   public static <T extends Persistent> String getPersistenceKind(final Class<T> oClass)
   {
      return oClass.getSimpleName();  
   }
   
   /**
    * Create a new application-managed EntityManager.
    * This method returns a new instance each time it is invoked.
    * @return A new entity manager instance.
    */
   private static EntityManager newEntityManager()
   {
      return _entityManagerFactory.createEntityManager();
   }

   /**
    * Insert (persist) an entity in the datastore.
    * @param entity The entity to persist.
    * @return The managed instance that the state was merged to, or null in case any error occurred.
    * @throws EntityExistsException If an entity with the same unique key already exists in the datastore.
    */
   public static <T extends Persistent> boolean insert(final T entity) throws EntityExistsException
   {
      EntityManager em = null;
      try
      {
         em = DatastoreTools.newEntityManager();
         em.persist(entity);
         return true;
      }
      catch (final EntityExistsException e)
      {
         throw e;
      }
      catch (final Exception e)
      {
      }
      finally
      {
         em.close();
      }
      return false;
   }

   /**
    * Deletes an (persisted) entity from the datastore.
    * @param entity The entity to delete.
    * @return True if the entity could be removed.
    * @throws EntityExistsException If an entity with the same unique key already exists in the datastore.
    */
   public static <T extends Persistent> boolean delete(final T entity) throws EntityExistsException
   {
      boolean deleted = false;
      EntityManager em = null;
      try
      {
         em = DatastoreTools.newEntityManager();
         T attachedEntity = em.merge(entity);
         if (attachedEntity != null)
         {
            em.remove(attachedEntity);
            deleted = true;
         }
      }
      catch (final Exception e)
      {
      }
      finally
      {
         try
         {
            em.close();
         }
         catch (final Exception e)
         {
            deleted = false;
         }
      }
      return deleted;
   }

   /**
    * Update (persist) an entity in the datastore.
    * @param entity The entity to persist.
    * @return The managed instance that the state was merged to, or null in case any error occurred.
    */
   public static <T extends Persistent> T update(final T entity)
   {
      T output = null;
      EntityManager em = null;
      try
      {
         em = DatastoreTools.newEntityManager();
         output = em.merge(entity);
      }
      catch (final Exception e)
      {
      }
      finally
      {
         try
         {
            em.close();
         }
         catch (final Exception e)
         {
            output = null;
         }
      }
      return output;
   }

   /**
    * Instantiate and restore an entity from the datastore. Returns null in case the entity was not found.
    * @param entityClass The class of the entity to be restored. Such class must implement an accessible default constructor.
    * @param key The datastore key of the entity to restore. 
    * @return A new entity instance, which content has been restored from the datastore, or null if an error occured.
    */
   public static <T extends Persistent> T selectOne(final Class<T> entityClass, final Key key)
   {
      T output = null;
      EntityManager em = null;
      try
      {
         em = DatastoreTools.newEntityManager();
         output = em.find(entityClass, key);
      }
      catch (final Exception e)
      {
      }
      finally
      {
         try
         {
            em.close();
         }
         catch (final Exception e)
         {
            output = null;
         }
      }
      return output;
   }

   /**
    * Sends a JPA Query to the datastore, and returns the results list.
    * @param entityClass The class of the objects that are expected to be returned by the query.
    * @param queryString The JPA query.
    * @return The list of results returned by the query, or null if an error occurred.
    */
   public static <T extends Persistent> List<T> query(final Class<T> entityClass, final String queryString)
   {
      List<T> output = null;
      EntityManager em = null;
      try
      {
         em = DatastoreTools.newEntityManager();
         final TypedQuery<T> query = em.createQuery(queryString, entityClass);
         output = query.getResultList();
      }
      finally
      {
         try
         {
            em.close();
         }
         catch (final Exception e)
         {
            output = null;
         }
      }
      return output;
   }
}
