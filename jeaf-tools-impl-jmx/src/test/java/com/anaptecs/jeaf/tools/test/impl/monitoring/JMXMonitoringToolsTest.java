/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.monitoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.anaptecs.jeaf.tools.annotations.MonitoringToolsConfig;
import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.monitoring.GaugeInfo;
import com.anaptecs.jeaf.tools.api.monitoring.MonitoringTools;
import com.anaptecs.jeaf.tools.api.monitoring.TimerSample;
import com.anaptecs.jeaf.tools.impl.monitoring.MonitoringToolsConfiguration;
import com.anaptecs.jeaf.tools.impl.monitoring.MonitoringToolsImpl;
import com.anaptecs.jeaf.tools.impl.monitoring.jmx.JMXMeterRegistryFactory;
import com.anaptecs.jeaf.tools.impl.monitoring.jmx.ObjectNameFactoryImpl;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import org.junit.jupiter.api.Test;

@MonitoringToolsConfig(meterRegistryFactory = JMXMeterRegistryFactory.class)
public class JMXMonitoringToolsTest {

  @Test
  public void testObjectNameFactory( ) {
    ObjectNameFactoryImpl lObjectNameFactory = new ObjectNameFactoryImpl();

    ObjectName lName = lObjectNameFactory.createName("Meter", null, "com.anaptecs.jeaf.Counters");
    assertEquals("com.anaptecs:name=Counters,type=jeaf", lName.getCanonicalName());

    assertEquals("com.anaptecs", lName.getDomain());

    assertEquals("jeaf", lName.getKeyProperty("type"));
    assertEquals("Counters", lName.getKeyProperty("name"));

    lName = lObjectNameFactory.createName("Meter", "", "Counters");
    assertEquals("default:name=Counters,type=Meter", lName.getCanonicalName());

    lName = lObjectNameFactory.createName("Meter", "", "type.Name");
    assertEquals("default:name=Name,type=type", lName.getCanonicalName());

    lName = lObjectNameFactory.createName("Meter", "com.anaptecs.jeaf", "type.Name");
    assertEquals("com.anaptecs.jeaf:name=Name,type=type", lName.getCanonicalName());

    lName = lObjectNameFactory.createName("Meter", "", "domain.type.Name");
    assertEquals("domain:name=Name,type=type", lName.getCanonicalName());

    lName = lObjectNameFactory.createName("Meter", "com.anaptecs.jeaf", "domain.type.Name");
    assertEquals("com.anaptecs.jeaf.domain:name=Name,type=type", lName.getCanonicalName());

    try {
      lObjectNameFactory.createName("Meter", "", "Invalid=Type=Name");
      fail();
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.INVALID_JMX_METER_NAME, e.getErrorCode());
      assertEquals("[0 1053] 1053: default:type=Meter,name=Invalid=Type=Name", e.getMessage());
    }
  }

  @Test
  public void testJMXTimers( ) throws JMException, InterruptedException {
    XFun.getTrace().info("Hellow World!");
    MonitoringToolsConfiguration lConfiguration = new MonitoringToolsConfiguration("JMXMonitoring", "META-INF", true);
    MonitoringToolsImpl lMonitoringTools = new MonitoringToolsImpl(lConfiguration);
    TimerSample lTimerSample = lMonitoringTools.newTimerSample("my.first.Timer");
    Thread.sleep(10);
    lMonitoringTools.recordTimerSample(lTimerSample);

    // Try to connect to MBeanServer.
    ObjectName lServerName = new ObjectName("my:type=first,name=Timer");
    MBeanServer lMBeanServer = ManagementFactory.getPlatformMBeanServer();

    // Get state of JEAF Fast Lane server via JMX
    Long lCount = (Long) lMBeanServer.getAttribute(lServerName, "Count");
    assertEquals(1, lCount);

    lMonitoringTools.incrementCounter("Counters");
    lServerName = new ObjectName("default:type=meters,name=Counters");

    // Get state of JEAF Fast Lane server via JMX
    lCount = (Long) lMBeanServer.getAttribute(lServerName, "Count");

  }

  @Test
  public void testJMXGaugeUsage( ) throws JMException {
    MonitoringTools lMonitoringTools = Tools.getMonitoringTools();
    List<String> lListToBeWtached = new ArrayList<>();
    lMonitoringTools.monitorObject(lListToBeWtached, List::size, "my.list.Size");

    lListToBeWtached.add("1");

    // Try to connect to MBeanServer.
    ObjectName lServerName = new ObjectName("my:type=list,name=Size");
    MBeanServer lMBeanServer = ManagementFactory.getPlatformMBeanServer();

    // Get state of JEAF Fast Lane server via JMX
    Double lValue = (Double) lMBeanServer.getAttribute(lServerName, "Value");
    assertEquals(1, lValue);

    GaugeInfo lGaugeInfo = lMonitoringTools.getGaugeInfo("my.list.Size");
    assertEquals(1, lGaugeInfo.getValue());

    lListToBeWtached.add("2");
    assertEquals(2, lGaugeInfo.getValue());
    lValue = (Double) lMBeanServer.getAttribute(lServerName, "Value");
    assertEquals(2, lValue);

    lListToBeWtached.remove(0);
    assertEquals(1, lGaugeInfo.getValue());

  }

}
