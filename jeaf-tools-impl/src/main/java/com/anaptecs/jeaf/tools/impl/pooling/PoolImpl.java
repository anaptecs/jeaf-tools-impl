/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.pooling;

import java.util.concurrent.TimeUnit;

import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.pooling.PoolConfiguration;
import com.anaptecs.jeaf.tools.api.pooling.PoolGrowthStrategy;
import com.anaptecs.jeaf.tools.api.pooling.PooledObject;
import com.anaptecs.jeaf.tools.api.pooling.PooledObjectLifecycleManager;
import com.anaptecs.jeaf.xfun.api.checks.Assert;
import com.anaptecs.jeaf.xfun.api.checks.Check;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import com.anaptecs.jeaf.xfun.api.trace.Trace;

import stormpot.BlazePool;
import stormpot.Config;
import stormpot.PoolException;
import stormpot.TimeExpiration;
import stormpot.Timeout;

/**
 * Class implements an object pool based on stormpot. As an extension to stormpot's standard behavior this pool is able
 * to grow according to the pool configuration as it was provided when the pool was created.
 * 
 * Currently shrinking of the pool is not supported.
 * 
 * @author JEAF Development Team
 */
public final class PoolImpl<T> {
  /**
   * Name of the pool. The name is never null. However it is not guaranteed that the name of a pool is unique. Pool name
   * is only needed for logging purposes.
   */
  private final String poolName;

  /**
   * Configuration parameters of the pool.
   */
  private final PoolConfiguration poolConfiguration;

  /**
   * Stormpot pool that stores the objects.
   */
  private final BlazePool<PooledObjectWrapper<T>> pool;

  /**
   * Timeout that is used when new objects are acquired from the pool.
   */
  private final Timeout timeout;

  /**
   * Initialize new pool.
   * 
   * @param pPoolName Name of the pool. The parameter must not be null.
   * @param pLifecycleManager Lifecycle manager that should be used. The parameter must not be null.
   * @param pPoolConfiguration Configuration parameters for the new pool. The parameter must not be null.
   */
  public PoolImpl( String pPoolName, PooledObjectLifecycleManager<T> pLifecycleManager,
      PoolConfiguration pPoolConfiguration ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pPoolName, "pPoolName");
    Check.checkInvalidParameterNull(pLifecycleManager, "pLifecycleManager");
    Check.checkInvalidParameterNull(pPoolConfiguration, "pPoolConfiguration");

    poolName = pPoolName;
    poolConfiguration = pPoolConfiguration;
    timeout = new Timeout(pPoolConfiguration.getAcquireTimeout(), TimeUnit.MILLISECONDS);

    // Create new pool
    Config<PooledObjectWrapper<T>> lConfig = new Config<>();
    lConfig.setAllocator(new PooledObjectAllocator<>(pLifecycleManager));
    lConfig.setExpiration(new TimeExpiration<>(poolConfiguration.getObjectTTL(), TimeUnit.SECONDS));
    lConfig.setSize(poolConfiguration.getInitialPoolSize());
    lConfig.setBackgroundExpirationEnabled(false);
    pool = new BlazePool<>(lConfig);
  }

  /**
   * Method returns the name of the pool as it was defined when the pool was created.
   * 
   * @return {@link String} Name of the pool. The method never returns null.
   */
  public String getPoolName( ) {
    return poolName;
  }

  /**
   * Method tries to acquired an object from the pool. The maximum waiting time for a pooled object is taken from pool
   * configuration. If possible the method tries to increase the pool and the tries again to acquire an object.
   * 
   * @return {@link PooledObject} Object from the pool. The method never returns null. If no object could be acquired
   * within the configured timeout then an exception will be thrown.
   */
  public PooledObject<T> getPooledObject( ) {
    try {
      PooledObjectWrapper<T> lPooledObject = pool.claim(timeout);

      if (lPooledObject == null) {
        boolean lIncreasedPool = this.increasePoolSizeIfPossible();

        // Try to get an object again as we increased the pool size.
        if (lIncreasedPool == true) {
          lPooledObject = pool.claim(timeout);

          // Increasing pool size did not help that fast. May be a retry might help.
          if (lPooledObject == null) {
            throw new JEAFSystemException(ToolsMessages.UNABLE_TO_ACQUIRE_OBJECT_FROM_POOL, poolName);
          }
        }
        // Pool already has maximum size.
        else {
          throw new JEAFSystemException(ToolsMessages.POOL_OVERLOADED, poolName,
              Integer.toString(pool.getTargetSize()));
        }
      }
      return lPooledObject;
    }
    // Unable to acquire new object from pool.
    catch (PoolException | InterruptedException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_ACQUIRE_OBJECT_FROM_POOL, e, poolName);
    }
  }

  public int getCurrentPoolSize( ) {
    return pool.getTargetSize();
  }

  /**
   * Method increases the size of the pool if possible.
   * 
   * @return boolean Method returns true if the pool size could be increased and false otherwise.
   */
  public boolean increasePoolSizeIfPossible( ) {
    // Determine current pool size
    int lCurrentPoolSize = pool.getTargetSize();

    // Increase pool size
    boolean lIncreasedPoolSize;
    if (lCurrentPoolSize < poolConfiguration.getMaxPoolSize()) {
      // Calculate new pool size depending on growth policy.
      int lNewPoolSize = this.calculateNewPoolSize(lCurrentPoolSize);

      // Increase size of pool.
      pool.setTargetSize(lNewPoolSize);
      Trace.getTrace().write(ToolsMessages.INCREASED_POOL_SIZE, poolName, Integer.toString(lCurrentPoolSize),
          Integer.toString(lNewPoolSize));

      lIncreasedPoolSize = true;
    }
    // Pool already has maximum size
    else {
      lIncreasedPoolSize = false;
    }
    return lIncreasedPoolSize;

  }

  /**
   * Method calculates the new size of the pool according to the configuration.
   */
  public int calculateNewPoolSize( int lCurrentPoolSize ) {
    // Calculate new pool size depending on growth policy.
    int lNewPoolSize;
    PoolGrowthStrategy lEnlargementStrategy = poolConfiguration.getPoolGrowthStrategy();
    switch (lEnlargementStrategy) {
      case FIXED_SIZE_GROWTH:
        lNewPoolSize = lCurrentPoolSize + poolConfiguration.getIncrementSize();
        break;

      case PERCENTAGED_GROWTH:
        lNewPoolSize = (int) (lCurrentPoolSize * (1 + (poolConfiguration.getIncrementPercentage() / 100)));
        break;

      default:
        Assert.unexpectedEnumLiteral(lEnlargementStrategy);
        lNewPoolSize = lCurrentPoolSize;
    }
    lNewPoolSize = Math.min(lNewPoolSize, poolConfiguration.getMaxPoolSize());
    return lNewPoolSize;
  }
}
