/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.monitoring.jmx;

import com.anaptecs.jeaf.tools.api.monitoring.MeterRegistryFactory;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;

/**
 * Class implements a MeterRegistryFactory that is based on the Micrometer JMX implementation.
 * 
 * @author JEAF Development Team
 */
public class JMXMeterRegistryFactory implements MeterRegistryFactory {

  @Override
  public MeterRegistry createMeterRegistry( String pDomain ) {

    MetricRegistry lRegistry = new MetricRegistry();

    JmxConfig lConfig = new JmxConfig() {
      @Override
      public String get( String pKey ) {
        return null;
      }

      @Override
      public String domain( ) {
        return pDomain;
      }
    };
    JmxReporter lReporter = JmxReporter.forRegistry(lRegistry).createsObjectNamesWith(new ObjectNameFactoryImpl())
        .inDomain(lConfig.domain()).build();

    return new JmxMeterRegistry(lConfig, Clock.SYSTEM, ( id, convention ) -> id.getName(), lRegistry, lReporter);
  }
}
