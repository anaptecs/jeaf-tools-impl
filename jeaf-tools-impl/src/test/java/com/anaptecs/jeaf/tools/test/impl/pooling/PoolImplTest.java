/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.pooling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Calendar;

import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.pooling.PoolConfiguration;
import com.anaptecs.jeaf.tools.api.pooling.PoolConfiguration.Builder;
import com.anaptecs.jeaf.tools.api.pooling.PoolGrowthStrategy;
import com.anaptecs.jeaf.tools.api.pooling.PooledObject;
import com.anaptecs.jeaf.tools.api.pooling.PooledObjectLifecycleManager;
import com.anaptecs.jeaf.tools.impl.pooling.PoolImpl;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import org.junit.jupiter.api.Test;

public class PoolImplTest {
  static int creationCounter;

  @Test
  void testPoolCreation( ) throws InterruptedException {
    PooledObjectLifecycleManager<Calendar> lLifecycleManager = new PooledObjectLifecycleManager<Calendar>() {

      @Override
      public Calendar createObject( ) {
        creationCounter++;
        return Calendar.getInstance();
      }

      @Override
      public void resetObject(Calendar pObject) {
        pObject.clear();
      }

      @Override
      public void cleanup(Calendar Object) {
      }
    };

    PoolConfiguration lPoolConfiguration = PoolConfiguration.Builder.newBuilder().build();
    creationCounter = 0;
    PoolImpl<Calendar> lPool = new PoolImpl<>("MyFirstPool", lLifecycleManager, lPoolConfiguration);

    // Check created pool
    assertEquals("MyFirstPool", lPool.getPoolName());
    assertEquals(lPoolConfiguration.getInitialPoolSize(), lPool.getCurrentPoolSize());

    // Wait for a short amount of time until objects are created.
    Thread.sleep(50);
    assertTrue(lPoolConfiguration.getInitialPoolSize() <= creationCounter);

    // Get first object from pool
    PooledObject<Calendar> lPooledObject = lPool.getPooledObject();
    Calendar lPooledCalendar = lPooledObject.getPooledObject();
    lPooledCalendar.set(2022, 7, 8);
    assertEquals(2022, lPooledCalendar.get(Calendar.YEAR));
    assertEquals(7, lPooledCalendar.get(Calendar.MONTH));
    assertEquals(8, lPooledCalendar.get(Calendar.DAY_OF_MONTH));

    // Return object to pool.
    lPooledObject.release();
    assertEquals(1970, lPooledCalendar.get(Calendar.YEAR));
    assertEquals(0, lPooledCalendar.get(Calendar.MONTH));
    assertEquals(1, lPooledCalendar.get(Calendar.DAY_OF_MONTH));

    // Test exception handling
    try {
      new PoolImpl<>(null, lLifecycleManager, lPoolConfiguration);
      fail();
    }
    catch (IllegalArgumentException e) {
      assertEquals("'pPoolName' must not be null.", e.getMessage());
    }

    // Test exception handling
    try {
      new PoolImpl<>("2nd Pool", null, lPoolConfiguration);
      fail();
    }
    catch (IllegalArgumentException e) {
      assertEquals("'pLifecycleManager' must not be null.", e.getMessage());
    }

    // Test exception handling
    try {
      new PoolImpl<>("3rd Pool", lLifecycleManager, null);
      fail();
    }
    catch (IllegalArgumentException e) {
      assertEquals("'pPoolConfiguration' must not be null.", e.getMessage());
    }
  }

  @Test
  void testPoolResizing( ) throws InterruptedException {
    PooledObjectLifecycleManager<Calendar> lLifecycleManager = new PooledObjectLifecycleManager<Calendar>() {

      @Override
      public Calendar createObject( ) {
        creationCounter++;
        return Calendar.getInstance();
      }

      @Override
      public void resetObject(Calendar pObject) {
        pObject.clear();
      }

      @Override
      public void cleanup(Calendar Object) {
      }
    };

    Builder lBuilder = PoolConfiguration.Builder.newBuilder();
    lBuilder.setMaxPoolSize(10);
    lBuilder.setInitialPoolSize(9);
    lBuilder.setIncrementSize(2);
    lBuilder.setPoolGrowthStrategy(PoolGrowthStrategy.FIXED_SIZE_GROWTH);
    lBuilder.setAcquireTimeout(100);
    PoolConfiguration lPoolConfiguration = lBuilder.build();

    creationCounter = 0;
    PoolImpl<Calendar> lPool = new PoolImpl<>("MyFirstPool", lLifecycleManager, lPoolConfiguration);
    // Wait for a short amount of time until objects are created.
    Thread.sleep(50);
    assertEquals(9, creationCounter);
    assertEquals(9, lPool.getCurrentPoolSize());

    // Test new pool size calculation
    assertEquals(10, lPool.calculateNewPoolSize(9));

    // Acquire new object from pool
    for (int i = 0; i < 9; i++) {
      lPool.getPooledObject();
    }
    assertEquals(9, creationCounter);
    assertEquals(9, lPool.getCurrentPoolSize());

    // Now pool needs to be increased.
    PooledObject<Calendar> lLastPooledObject = lPool.getPooledObject();
    assertNotNull(lLastPooledObject);
    assertEquals(10, creationCounter);
    assertEquals(10, lPool.getCurrentPoolSize());

    // Now let's try to acquire another on. Now we should run into an exception.
    try {
      lPool.getPooledObject();
      fail();
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.POOL_OVERLOADED, e.getErrorCode());
    }

    // Tool pool resizing with percentage strategy
    lBuilder = PoolConfiguration.Builder.newBuilder();
    lBuilder.setPoolGrowthStrategy(PoolGrowthStrategy.PERCENTAGED_GROWTH);
    lBuilder.setMaxPoolSize(100);
    lBuilder.setInitialPoolSize(20);
    lBuilder.setIncrementPercentage(25);
    lPoolConfiguration = lBuilder.build();

    creationCounter = 0;
    lPool = new PoolImpl<>("MyFirstPool", lLifecycleManager, lPoolConfiguration);
    assertEquals(25, lPool.calculateNewPoolSize(20));

  }

  @Test
  void testBrokenLifecycleManager( ) throws Exception {
    PooledObjectLifecycleManager<Calendar> lLifecycleManager = new PooledObjectLifecycleManager<Calendar>() {
      @Override
      public Calendar createObject( ) {
        fail("Method must not be used in this context");
        return null;
      }

      @Override
      public void resetObject(Calendar pObject) {
      }

      @Override
      public void cleanup(Calendar Object) {
        fail("Method must not be used in this context");
      }
    };

    PoolConfiguration lPoolConfiguration = PoolConfiguration.Builder.newBuilder().build();
    PoolImpl<Calendar> lPool = new PoolImpl<>("MyFirstPool", lLifecycleManager, lPoolConfiguration);
    Thread.sleep(10);

    try {
      lPool.getPooledObject();
      fail();
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_ACQUIRE_OBJECT_FROM_POOL, e.getErrorCode());
    }
  }
}
