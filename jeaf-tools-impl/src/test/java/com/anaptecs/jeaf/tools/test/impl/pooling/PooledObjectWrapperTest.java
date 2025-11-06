/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.pooling;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.anaptecs.jeaf.tools.api.pooling.PooledObjectLifecycleManager;
import com.anaptecs.jeaf.tools.impl.pooling.PooledObjectWrapper;

public class PooledObjectWrapperTest {

  @Test
  void testPooledObjectWrapper( ) {
    TestSlotImpl lSlot = new TestSlotImpl();
    PooledObjectLifecycleManager<List<String>> lLifecycleManager = new PooledObjectLifecycleManager<List<String>>() {
      @Override
      public List<String> createObject( ) {
        fail("Method must not be used in this context");
        return null;
      }

      @Override
      public void resetObject( List<String> pObject ) {
        pObject.clear();
      }

      @Override
      public void cleanup( List<String> Object ) {
        fail("Method must not be used in this context");
      }

    };

    ArrayList<String> lPooledObject = new ArrayList<>();
    PooledObjectWrapper<List<String>> lWrapper =
        new PooledObjectWrapper<List<String>>(lPooledObject, lLifecycleManager, lSlot);

    assertEquals(lPooledObject, lWrapper.getPooledObject());
    assertEquals(false, lSlot.released);

    // Add object to list and check if it is cleared when object is returned to the pool
    lPooledObject.add("Let's jump in the pool");
    assertEquals(false, lPooledObject.isEmpty());

    lWrapper.release();
    assertEquals(true, lPooledObject.isEmpty());
    assertEquals(true, lSlot.released);

    // Check exception handling
    try {
      new PooledObjectWrapper<>(null, lLifecycleManager, lSlot);
      fail();
    }
    catch (IllegalArgumentException e) {
      assertEquals("'pObject' must not be null.", e.getMessage());
    }

    try {
      new PooledObjectWrapper<>(lPooledObject, null, lSlot);
      fail();
    }
    catch (IllegalArgumentException e) {
      assertEquals("'pLifecycleManager' must not be null.", e.getMessage());
    }

    try {
      new PooledObjectWrapper<>(lPooledObject, lLifecycleManager, null);
      fail();
    }
    catch (IllegalArgumentException e) {
      assertEquals("'pSlot' must not be null.", e.getMessage());
    }
  }
}
