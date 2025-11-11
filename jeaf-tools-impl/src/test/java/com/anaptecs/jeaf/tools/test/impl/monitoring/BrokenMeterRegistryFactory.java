/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.monitoring;

import com.anaptecs.jeaf.tools.annotations.MonitoringToolsConfig;
import com.anaptecs.jeaf.tools.api.monitoring.MeterRegistryFactory;

import io.micrometer.core.instrument.MeterRegistry;

@MonitoringToolsConfig(meterRegistryFactory = BrokenMeterRegistryFactory.class)
public class BrokenMeterRegistryFactory implements MeterRegistryFactory {
  private BrokenMeterRegistryFactory( ) {
  }

  @Override
  public MeterRegistry createMeterRegistry( String pDomain ) {
    return null;
  }

}