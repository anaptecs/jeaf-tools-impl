/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.monitoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.monitoring.MonitoringToken;
import com.anaptecs.jeaf.tools.api.monitoring.MonitoringTools;
import com.anaptecs.jeaf.tools.impl.monitoring.NewRelicMonitoringToken;
import com.anaptecs.jeaf.tools.impl.monitoring.NewRelicMonitoringToolsImpl;
import org.junit.jupiter.api.Test;

public class NewRelicMonitoringToolsTest {
  @Test
  public void testMonitoringToken( ) {
    MonitoringTools lMonitoringTools = Tools.getMonitoringTools();
    assertEquals(NewRelicMonitoringToolsImpl.class, lMonitoringTools.getClass());
    MonitoringToken lToken = lMonitoringTools.getMonitoringToken();
    assertNotNull(lToken);
    assertEquals(NewRelicMonitoringToken.class, lToken.getClass());

    // As we run in an environment without New Relic agent token is never active.
    assertEquals(false, lToken.isActive());
    assertEquals(false, lToken.link());
    assertEquals(false, lToken.link());
    lToken.release();
    assertEquals(false, lToken.isActive());
    lToken.release();
    assertEquals(false, lToken.link());
    assertEquals(false, lToken.linkAndRelease());

    lToken = lMonitoringTools.getMonitoringToken();
    assertEquals(false, lToken.isActive());
    assertEquals(false, lToken.linkAndRelease());
    assertEquals(false, lToken.isActive());
  }
}
