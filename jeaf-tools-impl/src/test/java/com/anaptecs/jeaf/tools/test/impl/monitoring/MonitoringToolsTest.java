/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.monitoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.monitoring.CounterInfo;
import com.anaptecs.jeaf.tools.api.monitoring.GaugeInfo;
import com.anaptecs.jeaf.tools.api.monitoring.MonitoringToken;
import com.anaptecs.jeaf.tools.api.monitoring.MonitoringTools;
import com.anaptecs.jeaf.tools.api.monitoring.Tag;
import com.anaptecs.jeaf.tools.api.monitoring.TimerInfo;
import com.anaptecs.jeaf.tools.api.monitoring.TimerSample;
import com.anaptecs.jeaf.tools.api.performance.Stopwatch;
import com.anaptecs.jeaf.tools.api.performance.TimePrecision;
import com.anaptecs.jeaf.tools.impl.monitoring.EmptyMonitoringTokenImpl;
import com.anaptecs.jeaf.tools.impl.monitoring.MonitoringToolsImpl;
import org.junit.jupiter.api.Test;

public class MonitoringToolsTest {
  @Test
  public void testDefaultAccess( ) {
    MonitoringTools lMonitoringTools = Tools.getMonitoringTools();
    assertEquals(MonitoringToolsImpl.class, lMonitoringTools.getClass());
  }

  @Test
  public void testTimestamps( ) {
    MonitoringToolsImpl lMonitoringTools = new MonitoringToolsImpl();
    assertEquals(TimeUnit.MICROSECONDS, lMonitoringTools.getDefaultTimeUnit());

    // Test timestamp mechanism
    long lNowNano = System.nanoTime();
    long lTimestamp = lMonitoringTools.getTimestamp();
    long lNowMicro = lNowNano / 1000;
    long lDifference = lTimestamp - lNowMicro;
    assertTrue(lDifference < 2000, "Time difference is too big: " + lDifference);

    // Test nanoseconds precision.
    lNowNano = System.nanoTime();
    lTimestamp = lMonitoringTools.getTimestamp(TimeUnit.NANOSECONDS);
    lDifference = lTimestamp - lNowNano;
    assertTrue(lDifference < 100000, "Time difference is too big: " + lDifference);

    // Test microseconds precision.
    lNowNano = System.nanoTime();
    lTimestamp = lMonitoringTools.getTimestamp(TimeUnit.MICROSECONDS);
    lNowMicro = lNowNano / 1000;
    lDifference = lTimestamp - lNowMicro;
    assertTrue(lDifference < 100, "Time difference is too big: " + lDifference);

    // Test milliseconds precision.
    long lNowMillis = System.currentTimeMillis();
    lTimestamp = lMonitoringTools.getTimestamp(TimeUnit.MILLISECONDS);
    lDifference = lTimestamp - lNowMillis;
    assertTrue(lDifference < 2, "Time difference is too big: " + lDifference);
  }

  @Test
  public void testTimerSampleCreation( ) {
    MonitoringToolsImpl lMonitoringTools = new MonitoringToolsImpl();

    // Test sample creation with default time unit which is MICROSECONDS
    TimerSample lFirstSample = lMonitoringTools.newTimerSample("my.first.timer");
    assertNotNull(lFirstSample);
    assertEquals("my.first.timer", lFirstSample.getTimerName());
    assertEquals(0, lFirstSample.getTimerTags().length);
    assertEquals(lMonitoringTools.getDefaultTimeUnit(), lFirstSample.getTimeUnit());
    assertTrue(lMonitoringTools.getTimestamp() > lFirstSample.getStartTimestamp());

    // Test sample creation with NANOSECONDS precision and tags.
    // Also ensure same instance patterns for timer with tags
    Tag lEnvTag = Tag.of("env", "prod");
    Tag lHostTag = Tag.of("host", "docker001");
    TimerSample lTimerWithTags =
        lMonitoringTools.newTimerSample("my.timer.withTags", TimeUnit.NANOSECONDS, lEnvTag, lHostTag);

    assertEquals("my.timer.withTags", lTimerWithTags.getTimerName());
    Tag[] lTimerTags = lTimerWithTags.getTimerTags();
    assertEquals("env", lTimerTags[0].getKey());
    assertEquals("prod", lTimerTags[0].getValue());
    assertEquals("host", lTimerTags[1].getKey());
    assertEquals("docker001", lTimerTags[1].getValue());
    assertEquals(2, lTimerTags.length);
  }

  @Test
  public void testTimerRecording( ) throws InterruptedException {
    TimeUnit lTimeUnit = TimeUnit.NANOSECONDS;
    MonitoringTools lMonitoringTools = Tools.getMonitoringTools();
    long lStart1 = lMonitoringTools.getTimestamp(lTimeUnit);
    TimerSample lTimerSample = lMonitoringTools.newTimerSample("my.timer", lTimeUnit);
    long lStart2 = lMonitoringTools.getTimestamp(lTimeUnit);

    assertTrue(lStart1 < lTimerSample.getStartTimestamp());
    assertTrue(lTimerSample.getStartTimestamp() < lStart2);

    Thread.sleep(5);
    long lStop2 = lMonitoringTools.getTimestamp(lTimeUnit);
    lMonitoringTools.recordTimerSample(lTimerSample);
    long lStop1 = lMonitoringTools.getTimestamp(lTimeUnit);

    TimerInfo lTimerInfo = lMonitoringTools.getTimerInfo("my.timer");

    assertEquals("my.timer", lTimerInfo.getName());
    assertEquals(0, lTimerInfo.getTags().size());
    assertEquals(1, lTimerInfo.getCount());
    long lMinDuration = lStop2 - lStart2;
    long lMaxDuration = lStop1 - lStart1;
    assertTrue(lMinDuration < lTimerInfo.getMean(lTimeUnit));
    assertTrue(lMaxDuration > lTimerInfo.getMean(lTimeUnit));
    assertEquals(lTimerInfo.getMean(), lTimerInfo.getMax());
    assertEquals(lTimerInfo.getMean(), lTimerInfo.getTotalTime());
  }

  @Test
  public void testTimerAccessPerformance( ) {
    MonitoringTools lMonitoringTools = Tools.getMonitoringTools();
    TimeUnit lDefaultTimeUnit = lMonitoringTools.getDefaultTimeUnit();
    int lCount = 10000000;

    Stopwatch lStopwatch = Tools.getPerformanceTools().createStopwatch("Timer-Recording", TimePrecision.NANOS).start();
    for (int i = 0; i < lCount; i++) {
      TimerSample lTimerSample = lMonitoringTools.newTimerSample("my.timer.performance", lDefaultTimeUnit);
      lMonitoringTools.recordTimerSample(lTimerSample);
    }
    lStopwatch.stopAndTrace(lCount);
  }

  @Test
  public void testCounterUsage( ) {
    MonitoringTools lMonitoringTools = Tools.getMonitoringTools();
    CounterInfo lCounter = lMonitoringTools.getCounterInfo("my.first.counter");
    assertEquals(0, lCounter.getCount());

    lMonitoringTools.incrementCounter("my.first.counter");
    assertEquals(1, lCounter.getCount());
    lCounter = lMonitoringTools.getCounterInfo("my.first.counter", (Tag[]) null);
    assertEquals(1, lCounter.getCount());

    // Also test counter with tags.
    CounterInfo lCounterInfo = lMonitoringTools.getCounterInfo("my.first.counter", Tag.of("key", "value"));
    assertEquals(0, lCounterInfo.getCount());
    List<Tag> lTags = lCounterInfo.getTags();
    assertEquals(1, lTags.size());
    assertEquals("key", lTags.get(0).getKey());
    assertEquals("value", lTags.get(0).getValue());
  }

  @Test
  public void testGaugeUsage( ) {
    MonitoringTools lMonitoringTools = Tools.getMonitoringTools();
    List<String> lListToBeWtached = new ArrayList<>();
    lMonitoringTools.monitorObject(lListToBeWtached, List::size, "my.list.size");

    GaugeInfo lGaugeInfo = lMonitoringTools.getGaugeInfo("my.list.size");
    assertEquals(0, lGaugeInfo.getValue());

    lListToBeWtached.add("1");
    assertEquals(1, lGaugeInfo.getValue());

    lListToBeWtached.add("2");
    assertEquals(2, lGaugeInfo.getValue());

    lListToBeWtached.remove(0);
    assertEquals(1, lGaugeInfo.getValue());
  }

  @SafeVarargs
  public static <T> void monitorObject(String name, T obj, ToIntFunction<T>... valueFunction) {
  }

  @SafeVarargs
  public static <T> void monitorObject(String name, T obj, ToDoubleFunction<T>... valueFunction) {

  }

  @Test
  public void testMonitoringToken( ) {
    MonitoringTools lMonitoringTools = Tools.getMonitoringTools();
    MonitoringToken lToken = lMonitoringTools.getMonitoringToken();
    assertNotNull(lToken);
    assertEquals(EmptyMonitoringTokenImpl.class, lToken.getClass());
    assertEquals(true, lToken.isActive());
    assertEquals(true, lToken.link());
    assertEquals(true, lToken.link());
    lToken.release();
    assertEquals(false, lToken.isActive());
    lToken.release();
    assertEquals(false, lToken.link());
    assertEquals(false, lToken.linkAndRelease());

    lToken = lMonitoringTools.getMonitoringToken();
    assertEquals(true, lToken.isActive());
    assertEquals(true, lToken.linkAndRelease());
    assertEquals(false, lToken.isActive());
  }
}
