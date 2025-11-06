/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.pooling;

import com.anaptecs.jeaf.tools.api.pooling.PooledObjectLifecycleManager;
import com.anaptecs.jeaf.xfun.api.checks.Check;

import stormpot.Allocator;
import stormpot.Slot;

/**
 * Class implements a Stormpot object allocator {@link Allocator}. This implementation uses the lifecycle manager that
 * was passed when an object pool was created.
 * 
 * @author JEAF Development Team
 */
public final class PooledObjectAllocator<T> implements Allocator<PooledObjectWrapper<T>> {
  /**
   * Lifecycle manager is used to create new pooled objects.
   */
  private final PooledObjectLifecycleManager<T> lifecycleManager;

  /**
   * Initialize object.
   * 
   * @param pLifecycleManager Lifecycle manager that should be used to create new instances of the pooled object. The
   * parameter must not be null.
   */
  public PooledObjectAllocator( PooledObjectLifecycleManager<T> pLifecycleManager ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pLifecycleManager, "pLifecycleManager");

    lifecycleManager = pLifecycleManager;
  }

  /**
   * Method creates a new instance of the pooled objects.
   * 
   * @return {@link PooledObjectWrapper} Method returns a wrapper for the new pooled object. The method never returns
   * null.
   */
  @Override
  public PooledObjectWrapper<T> allocate( Slot pSlot ) throws Exception {
    T lObject = lifecycleManager.createObject();
    return new PooledObjectWrapper<>(lObject, lifecycleManager, pSlot);
  }

  /**
   * Method is used to free resources when an object is removed from the pool again.
   */
  @Override
  public void deallocate( PooledObjectWrapper<T> pPoolable ) throws Exception {
    lifecycleManager.cleanup(pPoolable.getPooledObject());
  }
}
