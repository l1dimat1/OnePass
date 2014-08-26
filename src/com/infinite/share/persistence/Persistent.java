package com.infinite.share.persistence;

import java.io.Serializable;

/********************************************************************************************************************************
 * An empty interface that is used to type classes that are eligible for persistence in the database.
 * This interface only enforces that any persistent entity class implements the Serializable interface. 
 ********************************************************************************************************************************/
public interface Persistent extends Serializable
{
}
