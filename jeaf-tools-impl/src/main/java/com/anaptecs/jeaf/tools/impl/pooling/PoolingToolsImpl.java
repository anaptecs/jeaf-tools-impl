/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.pooling;

import java.util.ArrayList;
import java.util.List;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.pooling.Pool;
import com.anaptecs.jeaf.tools.api.pooling.PoolConfiguration;
import com.anaptecs.jeaf.tools.api.pooling.PooledObject;
import com.anaptecs.jeaf.tools.api.pooling.PooledObjectLifecycleManager;
import com.anaptecs.jeaf.tools.api.pooling.PoolingTools;
import com.anaptecs.jeaf.xfun.api.checks.Check;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;

/**
 * Class provides a stormpot (http://chrisvest.github.io/stormpot/) based implementation of JEAF's Pooling Tools
 * 
 * @author JEAF Development Team
 */
@ToolsImplementation(toolsInterface = PoolingTools.class)
public final class PoolingToolsImpl implements PoolingTools {
  /**
   * List contains all list of created pools. Pools are stored in a list to avoid rather slow map lookups for the right
   * pool.
   */
  private final List<PoolImpl<?>> pools = new ArrayList<>();

  /**
   * Method creates a new object pool.
   * 
   * @param pPoolName Name of the pool that should be created. It's recommended but not required to use unique names per
   * pool. The parameter must not be null.
   * @param pLifecycleManager Lifecycle manager for the created pool. The lifecycle manager is used to create and reset
   * pooled objects.The parameter must not be null.
   * @param pConfiguration Pool configuration that should be used. The parameter must not be null.
   * @return {@link Pool} Object representing the created pool. This object is needed when you want to acquire objects
   * from the pool. The method never returns null.
   */
  @Override
  public synchronized <T> Pool<T> createPool( String pPoolName, PooledObjectLifecycleManager<T> pLifecycleManager,
      PoolConfiguration pConfiguration ) {

    // Check parameters
    Check.checkInvalidParameterNull(pPoolName, "pPoolName");
    Check.checkInvalidParameterNull(pLifecycleManager, "pLifecycleManager");
    Check.checkInvalidParameterNull(pConfiguration, "pConfiguration");

    // Lookup ID for new pool.
    int lPoolID = pools.size();

    // Create new pool and return it.
    PoolImpl<T> lNewPoolImpl = new PoolImpl<>(pPoolName, pLifecycleManager, pConfiguration);
    pools.add(lNewPoolImpl);
    return new Pool<>(lPoolID, pPoolName);
  }

  /**
   * Method returns an object from the pool.
   * 
   * Please be aware that the object has explicitly returned to the pool again as soon as it it not needed any longer
   * using {@link PooledObject#release()} or {@link PoolingTools#releasePooledObject(PooledObject)}.
   * 
   * 
   * @param pPool Pool from which the object should be taken. The parameter must not be null.
   * @return {@link PooledObject} Object from the pool. The method never returns null. Method might throw a
   * {@link JEAFSystemException} in case that within the configured timeout of the pool no object could be acquired.
   */
  @Override
  public <T> PooledObject<T> acquirePooledObject( Pool<T> pPool ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pPool, "pPool");

    // Lookup pool with the passed ID
    @SuppressWarnings("unchecked")
    PoolImpl<T> lPoolImpl = (PoolImpl<T>) pools.get(pPool.getPoolID());
    return lPoolImpl.getPooledObject();
  }

  /**
   * Method is intended to be called to return a pooled object back again to the pool. After an object was returned to
   * the pool it must not be used any longer but the application code.
   * 
   * @param pPooledObject Object that should be returned to the pool. The parameter must not be null.
   */
  @Override
  public <T> void releasePooledObject( PooledObject<T> pPooledObject ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pPooledObject, "pPooledObject");

    // Release pooled object.
    PooledObjectWrapper<T> pWrapper = (PooledObjectWrapper<T>) pPooledObject;
    pWrapper.release();
  }
}
