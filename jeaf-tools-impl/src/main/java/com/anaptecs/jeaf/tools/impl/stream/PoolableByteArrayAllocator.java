/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.stream;

import java.util.concurrent.atomic.AtomicInteger;

import stormpot.Allocator;
import stormpot.Slot;

public class PoolableByteArrayAllocator implements Allocator<PoolableByteArray> {
  private final int byteArraySize;

  private final AtomicInteger counter = new AtomicInteger();

  public PoolableByteArrayAllocator( int pByteArraySize ) {
    byteArraySize = pByteArraySize;
  }

  @Override
  public PoolableByteArray allocate( Slot pSlot ) throws Exception {
    counter.incrementAndGet();
    return new PoolableByteArray(byteArraySize, pSlot);
  }

  @Override
  public void deallocate( PoolableByteArray pPoolable ) throws Exception {
    // Nothing to do.
  }
}
