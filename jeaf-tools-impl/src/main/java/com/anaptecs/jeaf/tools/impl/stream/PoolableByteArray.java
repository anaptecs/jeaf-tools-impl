/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.stream;

import java.util.Arrays;

import stormpot.Poolable;
import stormpot.Slot;

/**
 * Class implements a poolable byte array. Idea behind that is to reduce the amount of garbage that is produced by
 * allocating byte arrays over and over again when copying streams.
 * 
 * Implementations using this calls must ensure that they return objects back to the pool again ({@link #release()}).
 * Not returning objects to the pool will end up in memory leaks.
 * 
 * @author JEAF Development Team
 */
public class PoolableByteArray implements Poolable {
  /**
   * Location inside a pool where this instance is stored.
   */
  private final Slot slot;

  /**
   * Byte array that is actually pooled.
   */
  private final byte[] byteArray;

  /**
   * Initialize object.
   * 
   * @param pSize Size of the byte are that should be pooled.
   * @param pSlot Slot that is used to communicate with the pool. The parameter may be null.
   */
  public PoolableByteArray( int pSize, Slot pSlot ) {
    byteArray = new byte[pSize];
    slot = pSlot;
  }

  /**
   * Method returns the pooled byte array. Read and write access to the byte array is not protected. So you have to
   * ensure yourself that not multiple threads will write to it at the same time.
   * 
   * @return byte[] Byte array that is pooled. The method never returns null.
   */
  public byte[] getByteArray( ) {
    return byteArray;
  }

  /**
   * Method returns the pooled byte array back to the if it is connected with one. This also means the the byte array is
   * cleared again. As the object is now back in the pool it must not be used any longer.
   * 
   * Please ensure that the object will be returned to the pool again, by calling this method. Otherwise memory leaks
   * will occur.
   */
  @Override
  public void release( ) {
    // Clear content.
    Arrays.fill(byteArray, (byte) 0);

    // Return byte array back to pool
    if (slot != null) {
      slot.release(this);
    }
  }
}
