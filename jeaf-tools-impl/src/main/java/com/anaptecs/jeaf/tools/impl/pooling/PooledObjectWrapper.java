/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.pooling;

import com.anaptecs.jeaf.tools.api.pooling.PooledObject;
import com.anaptecs.jeaf.tools.api.pooling.PooledObjectLifecycleManager;
import com.anaptecs.jeaf.xfun.api.checks.Check;

import stormpot.Poolable;
import stormpot.Slot;

/**
 * Class implements a wrapper for the objects that should actually be pooled. This wrapper is required as we do not want
 * clients to implement anything like this (avoid boiler blade code). Especially as it is possible to avoid that using a
 * generics based implementation.
 * 
 * @author JEAF Development Team
 */
public final class PooledObjectWrapper<T> implements PooledObject<T>, Poolable {
  /**
   * The actually pooled object
   */
  private final T wrappedObject;

  /**
   * Lifecycle manager that belongs to to the pooled object. It is required to provide a simple releasing process to
   * client code.
   */
  private final PooledObjectLifecycleManager<T> lifecycleManager;

  /**
   * Reference to stormpots pooling mechanism.
   */
  private final Slot slot;

  /**
   * Initialize object.
   * 
   * @param pObject Object that actually should be pooled. The parameter must not be null.
   * @param pLifecycleManager Lifecycle manager that should be used to release object to pool again. The parameter must
   * not be null.
   * @param pSlot Slot that is required to communicate with Stormpot pool implementation when releaseing the object
   * again. The parameter must not be null.
   */
  public PooledObjectWrapper( T pObject, PooledObjectLifecycleManager<T> pLifecycleManager, Slot pSlot ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pObject, "pObject");
    Check.checkInvalidParameterNull(pLifecycleManager, "pLifecycleManager");
    Check.checkInvalidParameterNull(pSlot, "pSlot");

    wrappedObject = pObject;
    lifecycleManager = pLifecycleManager;
    slot = pSlot;
  }

  /**
   * Method is called by application code when an object is no longer needed and should be returned to the pool again.
   */
  @Override
  public void release( ) {
    slot.release(this);
    lifecycleManager.resetObject(wrappedObject);
  }

  /**
   * Method return the object that actually is the one that should be pooled.
   * 
   * @return T The actually pooled object. The method never returns null.
   */
  @Override
  public T getPooledObject( ) {
    return wrappedObject;
  }
}
