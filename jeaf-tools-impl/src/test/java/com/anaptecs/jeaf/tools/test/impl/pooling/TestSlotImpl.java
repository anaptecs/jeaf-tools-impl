/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.pooling;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import stormpot.Poolable;
import stormpot.Slot;

public class TestSlotImpl implements Slot {
  public boolean released = false;

  @Override
  public void release(Poolable pObject) {
    assertNotNull(pObject);
    released = true;
  }

  @Override
  public void expire(Poolable pObj) {
  }
}
