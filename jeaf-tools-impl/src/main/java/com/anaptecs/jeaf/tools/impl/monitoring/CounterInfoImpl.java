/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.monitoring;

import com.anaptecs.jeaf.tools.api.monitoring.CounterInfo;

import io.micrometer.core.instrument.Counter;

/**
 * Class implements a counter based on Micrometer {@link Counter}.
 * 
 * @author JEAF Development Team
 */
public class CounterInfoImpl extends MeterInfoImpl implements CounterInfo {
  /**
   * Initialize object.
   * 
   * @param pCounter
   */
  CounterInfoImpl( Counter pCounter ) {
    super(pCounter);
  }

  /**
   * Method returns the current count of the counter.
   * 
   * @return int Current value of the counter.
   */
  @Override
  public int getCount( ) {
    return (int) ((Counter) this.getMeter()).count();
  }
}
