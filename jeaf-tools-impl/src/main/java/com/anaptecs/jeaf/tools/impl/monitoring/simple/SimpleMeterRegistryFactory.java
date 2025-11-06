/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.monitoring.simple;

import com.anaptecs.jeaf.tools.api.monitoring.MeterRegistryFactory;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/**
 * Class implements a factory to use the build-in Micrometer simple registry for monitoring.
 * 
 * @author JEAf Development Team.
 */
public class SimpleMeterRegistryFactory implements MeterRegistryFactory {

  @Override
  public MeterRegistry createMeterRegistry( String pDomain ) {
    return new SimpleMeterRegistry();
  }
}
