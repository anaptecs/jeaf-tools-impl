/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.pooling;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;

import org.junit.jupiter.api.Test;

import com.anaptecs.jeaf.tools.api.pooling.Pool;
import com.anaptecs.jeaf.tools.api.pooling.PoolConfiguration;
import com.anaptecs.jeaf.tools.api.pooling.PooledObject;
import com.anaptecs.jeaf.tools.api.pooling.PooledObjectLifecycleManager;
import com.anaptecs.jeaf.tools.api.pooling.PoolingTools;
import com.anaptecs.jeaf.tools.impl.pooling.PoolingToolsImpl;

public class PoolingToolsImplTest {
  @Test
  void testPoolingTools( ) {

    PooledObjectLifecycleManager<Calendar> lLifecycleManager = new PooledObjectLifecycleManager<Calendar>() {

      @Override
      public Calendar createObject( ) {
        return Calendar.getInstance();
      }

      @Override
      public void resetObject( Calendar pObject ) {
        pObject.clear();
      }

      @Override
      public void cleanup( Calendar Object ) {
      }
    };

    PoolConfiguration lConfiguration = PoolConfiguration.Builder.newBuilder().build();
    PoolingToolsImpl lPoolingTools = new PoolingToolsImpl();

    // Test pooling tools access
    assertEquals(PoolingToolsImpl.class, PoolingTools.getPoolingTools().getClass());

    // Test pool creation.
    Pool<Calendar> lPool_01 = lPoolingTools.createPool("MyFirstPool", lLifecycleManager, lConfiguration);
    assertEquals(0, lPool_01.getPoolID());
    assertEquals("MyFirstPool", lPool_01.getPoolName());

    // Test exception handling.
    try {
      lPoolingTools.createPool(null, lLifecycleManager, lConfiguration);
      fail();
    }
    catch (IllegalArgumentException e) {
      assertEquals("'pPoolName' must not be null.", e.getMessage());
    }
    try {
      lPoolingTools.createPool("BrokenPool", null, lConfiguration);
      fail();
    }
    catch (IllegalArgumentException e) {
      assertEquals("'pLifecycleManager' must not be null.", e.getMessage());
    }
    try {
      lPoolingTools.createPool("Broken Pool", lLifecycleManager, null);
      fail();
    }
    catch (IllegalArgumentException e) {
      assertEquals("'pConfiguration' must not be null.", e.getMessage());
    }

    // Try to acquire object from pool
    PooledObject<Calendar> lPooledObject = lPoolingTools.acquirePooledObject(lPool_01);
    assertNotNull(lPooledObject);
    lPoolingTools.releasePooledObject(lPooledObject);
  }

  @Test
  void testPoolingToolsViaAPI( ) {
    PooledObjectLifecycleManager<Calendar> lLifecycleManager = new PooledObjectLifecycleManager<Calendar>() {

      @Override
      public Calendar createObject( ) {
        return Calendar.getInstance();
      }

      @Override
      public void resetObject( Calendar pObject ) {
        pObject.clear();
      }

      @Override
      public void cleanup( Calendar Object ) {
      }
    };

    PoolConfiguration lConfiguration = PoolConfiguration.Builder.newBuilder().build();
    Pool<Calendar> lPool = PoolingTools.getPoolingTools().createPool("Another Pool", lLifecycleManager, lConfiguration);
    PooledObject<Calendar> lPooledObject = lPool.acquirePooledObject();
    assertNotNull(lPooledObject);
    lPooledObject.release();
  }
}
