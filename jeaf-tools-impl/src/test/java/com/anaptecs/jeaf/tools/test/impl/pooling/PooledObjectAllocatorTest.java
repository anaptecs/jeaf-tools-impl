/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.pooling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import com.anaptecs.jeaf.tools.api.pooling.PooledObjectLifecycleManager;
import com.anaptecs.jeaf.tools.impl.pooling.PooledObjectAllocator;
import com.anaptecs.jeaf.tools.impl.pooling.PooledObjectWrapper;
import org.junit.jupiter.api.Test;
import stormpot.Slot;

public class PooledObjectAllocatorTest {
  @Test
  void testObjectAllocator( ) throws Exception {
    PooledObjectLifecycleManager<List<String>> lLifecycleManager = new PooledObjectLifecycleManager<List<String>>() {
      public boolean cleanedup = false;

      @Override
      public List<String> createObject( ) {
        return new ArrayList<>();
      }

      @Override
      public void resetObject(List<String> pObject) {
        fail("Method must not be used in this context");
      }

      @Override
      public void cleanup(List<String> pObject) {
        pObject.clear();
      }

    };

    Slot lSlot = new TestSlotImpl();

    PooledObjectAllocator<List<String>> lAllocator = new PooledObjectAllocator<>(lLifecycleManager);
    PooledObjectWrapper<List<String>> lObjectWrapper = lAllocator.allocate(lSlot);
    assertEquals(true, lObjectWrapper.getPooledObject().isEmpty());

    lObjectWrapper.getPooledObject().add("Jump in");
    assertEquals(false, lObjectWrapper.getPooledObject().isEmpty());

    lAllocator.deallocate(lObjectWrapper);
    assertEquals(true, lObjectWrapper.getPooledObject().isEmpty());

    // Test exception handling
    try {
      new PooledObjectAllocator<>(null);
      fail();
    }
    catch (IllegalArgumentException e) {
      assertEquals("'pLifecycleManager' must not be null.", e.getMessage());
    }
  }
}
