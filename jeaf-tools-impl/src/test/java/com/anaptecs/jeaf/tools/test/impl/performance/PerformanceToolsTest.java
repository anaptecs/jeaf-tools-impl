/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.performance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.performance.PerformanceTools;
import com.anaptecs.jeaf.tools.api.performance.Stopwatch;
import com.anaptecs.jeaf.tools.api.performance.StopwatchResult;
import com.anaptecs.jeaf.tools.api.performance.TimePrecision;
import com.anaptecs.jeaf.tools.impl.performance.StopwatchImpl;

public class PerformanceToolsTest {
  @Test
  public void testStopwatchImpl( ) throws InterruptedException {
    StopwatchImpl lStopwatch = new StopwatchImpl("JEAF Performance Test Suite", TimePrecision.NANOS);
    assertFalse(lStopwatch.isRunning());
    long lStartLower = System.nanoTime();
    lStopwatch.start();
    long lStartUpper = System.nanoTime();
    assertTrue(lStopwatch.isRunning());

    long lStopLower = System.nanoTime();
    lStopwatch.stop();
    lStopwatch.stop();
    long lStopUpper = System.nanoTime();
    assertFalse(lStopwatch.isRunning());

    StopwatchResult lResult = lStopwatch.getResult();
    long lDuration = lResult.getDuration();
    long lMinDuration = lStopLower - lStartUpper;
    long lMaxDuration = lStopUpper - lStartLower;
    assertTrue(lDuration > lMinDuration);
    assertTrue(lDuration < lMaxDuration);
    assertEquals(TimePrecision.NANOS, lResult.getUnit());
    assertEquals(0, lResult.getTransactionsPerSecond());

    // Start new stop watch
    lStopwatch = new StopwatchImpl("JEAF Performance Test Suite", TimePrecision.MILLIS);
    lStopwatch.start(1000);
    assertTrue(lStopwatch.isRunning());
    Thread.sleep(1000);
    lResult = lStopwatch.getResult();
    assertFalse(lStopwatch.isRunning());
    assertEquals(1000, lResult.getTransactions());
    assertTrue(lResult.getTransactionsPerSecond() < 1050);
    assertTrue(lResult.getTransactionsPerSecond() > 950);

    lStopwatch = new StopwatchImpl("JEAF Performance Test Suite", TimePrecision.NANOS);
    lStopwatch.start();
    assertTrue(lStopwatch.isRunning());
    Thread.sleep(50);
    lStopwatch.stop(50);
    lStopwatch.stop(50);
    assertFalse(lStopwatch.isRunning());
    lResult = lStopwatch.getResult();
    assertEquals(50, lResult.getTransactions());
    lStopwatch.traceResult();

    lStopwatch = new StopwatchImpl("JEAF Performance Test Suite", TimePrecision.NANOS);
    lStopwatch.start();
    Thread.sleep(50);
    lStopwatch.stopAndTrace();
    lStopwatch.stopAndTrace();
    assertFalse(lStopwatch.isRunning());
    lResult = lStopwatch.getResult();
    assertEquals(0, lResult.getTransactions());

    lStopwatch = new StopwatchImpl("JEAF Performance Test Suite", TimePrecision.NANOS);
    lStopwatch.start();
    Thread.sleep(50);
    lStopwatch.stopAndTrace(234);
    lStopwatch.stopAndTrace(999);
    assertFalse(lStopwatch.isRunning());
    lResult = lStopwatch.getResult();
    assertEquals(234, lResult.getTransactions());
  }

  @Test
  public void testPerformanceTools( ) throws InterruptedException {
    PerformanceTools lPerformanceTools = Tools.getPerformanceTools();
    Stopwatch lStopwatch = lPerformanceTools.createStopwatch("My Stopwatch", TimePrecision.NANOS);
    assertNotNull(lStopwatch);
    assertEquals(StopwatchImpl.class, lStopwatch.getClass());
    lStopwatch.start();
    assertTrue(lStopwatch.isRunning());
    Thread.sleep(50);
    lStopwatch.stop(50);
    assertFalse(lStopwatch.isRunning());
    StopwatchResult lResult = lStopwatch.getResult();
    assertEquals(50, lResult.getTransactions());
    lStopwatch.traceResult();

    // Create stopwatch without a name
    lStopwatch = lPerformanceTools.createStopwatch(null, TimePrecision.NANOS);
    assertNotNull(lStopwatch);
    assertEquals(StopwatchImpl.class, lStopwatch.getClass());
    lStopwatch.start();
    assertTrue(lStopwatch.isRunning());
    Thread.sleep(50);
    lStopwatch.stop(50);
    assertFalse(lStopwatch.isRunning());
    lResult = lStopwatch.getResult();
    assertEquals(50, lResult.getTransactions());
    lStopwatch.traceResult();

    // Test exception handling
    try {
      lPerformanceTools.createStopwatch(null, null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }
}
