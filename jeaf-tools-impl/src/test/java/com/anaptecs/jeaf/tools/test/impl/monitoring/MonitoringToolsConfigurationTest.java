/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.monitoring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.anaptecs.jeaf.tools.annotations.MonitoringToolsConfig;
import com.anaptecs.jeaf.tools.impl.DefaultToolsConfiguration;
import com.anaptecs.jeaf.tools.impl.monitoring.MonitoringToolsConfiguration;
import com.anaptecs.jeaf.tools.impl.monitoring.simple.SimpleMeterRegistryFactory;

@MonitoringToolsConfig(
    defaultTimeUnit = TimeUnit.NANOSECONDS,
    meterRegistryFactory = MyTestMeterRegistryFactory.class,
    domain = "bla.bla")
public class MonitoringToolsConfigurationTest {

  @Test
  public void testDefaultConfiguration( ) {
    MonitoringToolsConfiguration lConfiguration = new MonitoringToolsConfiguration();
    assertEquals(TimeUnit.MICROSECONDS, lConfiguration.getDefaultTimeUnit());
    assertEquals(SimpleMeterRegistryFactory.class, lConfiguration.loadMeterRegistryFactory().getClass());
    assertEquals(DefaultToolsConfiguration.class, lConfiguration.getConfigurationClass());
    assertEquals("", lConfiguration.getDomain());
  }

  @Test
  public void testEmptyConfiguration( ) {
    MonitoringToolsConfig lEmptyConfiguration = new MonitoringToolsConfiguration().getEmptyConfiguration();
    assertEquals(TimeUnit.MICROSECONDS, lEmptyConfiguration.defaultTimeUnit());
    assertEquals(null, lEmptyConfiguration.meterRegistryFactory());
    assertEquals(MonitoringToolsConfig.class, lEmptyConfiguration.annotationType());
    assertEquals("", lEmptyConfiguration.domain());
  }

  @Test
  public void testCustomConfiguration( ) {
    MonitoringToolsConfiguration lConfiguration =
        new MonitoringToolsConfiguration("MyMonitoringTools", "META-INF", true);

    assertEquals(TimeUnit.NANOSECONDS, lConfiguration.getDefaultTimeUnit());
    assertEquals(MyTestMeterRegistryFactory.class, lConfiguration.loadMeterRegistryFactory().getClass());
    assertEquals(MonitoringToolsConfigurationTest.class, lConfiguration.getConfigurationClass());
    assertEquals("bla.bla", lConfiguration.getDomain());
    MonitoringToolsConfig lAnnotation =
        MonitoringToolsConfigurationTest.class.getAnnotation(MonitoringToolsConfig.class);
    List<String> lErrors = lConfiguration.checkCustomConfiguration(lAnnotation);
    assertEquals(0, lErrors.size());

    // Test broken custom configuration
    lConfiguration = new MonitoringToolsConfiguration("BrokenMonitoringTools", "META-INF", true);
    assertEquals(TimeUnit.MICROSECONDS, lConfiguration.getDefaultTimeUnit());
    assertEquals(BrokenMeterRegistryFactory.class, lConfiguration.getConfigurationClass());
    lAnnotation = BrokenMeterRegistryFactory.class.getAnnotation(MonitoringToolsConfig.class);
    lErrors = lConfiguration.checkCustomConfiguration(lAnnotation);
    assertEquals(1, lErrors.size());
    assertEquals(
        "Unable to create new instance of implementation for interface com.anaptecs.jeaf.tools.api.monitoring.MeterRegistryFactory. Faulty implemetation class: com.anaptecs.jeaf.tools.test.impl.monitoring.BrokenMeterRegistryFactory",
        lErrors.get(0));

  }
}
