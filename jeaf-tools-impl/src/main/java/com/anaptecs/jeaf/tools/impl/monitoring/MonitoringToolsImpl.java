/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.monitoring;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.monitoring.CounterInfo;
import com.anaptecs.jeaf.tools.api.monitoring.GaugeInfo;
import com.anaptecs.jeaf.tools.api.monitoring.MonitoringToken;
import com.anaptecs.jeaf.tools.api.monitoring.MonitoringTools;
import com.anaptecs.jeaf.tools.api.monitoring.Tag;
import com.anaptecs.jeaf.tools.api.monitoring.TimerInfo;
import com.anaptecs.jeaf.tools.api.monitoring.TimerSample;
import com.anaptecs.jeaf.xfun.api.checks.Check;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Gauge.Builder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.search.RequiredSearch;

@ToolsImplementation(toolsInterface = MonitoringTools.class)
public class MonitoringToolsImpl implements MonitoringTools {
  /**
   * Meter registry implementation as it was configured.
   */
  private final MeterRegistry meterRegistry;

  /**
   * Default time unit as it was configured.
   */
  private final TimeUnit defaultTimeUnit;

  /**
   * Initialize object using default configuration.
   */
  public MonitoringToolsImpl( ) {
    this(new MonitoringToolsConfiguration());
  }

  /**
   * Initialize monitoring tools.
   * 
   * @param pConfiguration Configuration for monitoring tools. The parameter must not be null.
   */
  public MonitoringToolsImpl( MonitoringToolsConfiguration pConfiguration ) {
    meterRegistry = pConfiguration.loadMeterRegistryFactory().createMeterRegistry(pConfiguration.getDomain());
    defaultTimeUnit = pConfiguration.getDefaultTimeUnit();
  }

  /**
   * Method returns the configured default time unit. The default time unit is used when ever no time unit is set
   * explicitly when working with JEAF's Monitoring Tools. It's recommended that you either always work with explicit
   * time units are always with the default one.
   * 
   * @return {@link TimeUnit} Configured default time unit. The method never returns null.
   */
  @Override
  public TimeUnit getDefaultTimeUnit( ) {
    return defaultTimeUnit;
  }

  /**
   * Method returns the current timestamp with default time unit precision.
   * 
   * @return long Current timestamp based on the precision of the configured default time unit.
   * 
   * {@link #getDefaultTimeUnit()}
   */
  @Override
  public long getTimestamp( ) {
    return this.getTimestamp(defaultTimeUnit);
  }

  /**
   * Method returns a timestamp that can be used to measure execution time. Based on the passed time unit either
   * {@link System#currentTimeMillis()} or {@link System#nanoTime()} will be used to resolve timestamp. Due to different
   * mechanisms in timestamp resolution timestamps with different {@link TimeUnit} can not be mixed.
   * 
   * @param pTimeUnit Time unit that should be used to measure. The parameter must not be null.
   * @return long Timestamp using the passed time unit.
   */
  @Override
  public long getTimestamp( TimeUnit pTimeUnit ) {
    // Check parameter
    Check.checkInvalidParameterNull(pTimeUnit, "pTimeUnit");

    // Resolve current timestamp according to the requested precision
    long lTimestamp;
    switch (pTimeUnit) {
      case NANOSECONDS:
      case MICROSECONDS:
        lTimestamp = pTimeUnit.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
        break;

      default:
        lTimestamp = pTimeUnit.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }
    // Return timestamp
    return lTimestamp;
  }

  /**
   * Method returns the timer that matches to the passed name and tags. Please be aware that a timer is identified by
   * its name and all of its tags.
   * 
   * @param pName Name of the timer. The parameter must be a real string.
   * @param pTimerTags Optional list of tags of the timer.
   * @return {@link Timer} Timer that matches to the passed name and tags. The method never returns null. If a matching
   * timer already exists then it will be returned else a new one will be created.
   */
  private Timer getTimer( String pName, Tag... pTimerTags ) {
    return meterRegistry.timer(pName, this.convertTags(pTimerTags));
  }

  /**
   * Method creates a new timer sample. This is the starting point for a new recording. As soon as the recording is done
   * {@link #recordTimerSample(TimerSample)} needs to be called. The created timer sample uses the default time unit for
   * its recording.
   * 
   * @param pName Name of the timer. The parameter must not be null.
   * @param pTimerTags Optional tags for the timer. Please be aware that a timer is identified by its name and all of
   * its tags.
   * @return {@link TimerSample} Created timer sample. the method never returns null.
   */
  @Override
  public TimerSample newTimerSample( String pName, Tag... pTimerTags ) {
    return this.newTimerSample(pName, defaultTimeUnit, pTimerTags);
  }

  /**
   * Method creates a new timer sample. This is the starting point for a new recording. As soon as the recording is done
   * {@link #recordTimerSample(TimerSample)} needs to be called. The created timer sample uses the default time unit for
   * its recording.
   * 
   * @param pName Name of the timer. The parameter must not be null.
   * @param pTimeUnit Time unit that is used for recording. The parameter must not be null.
   * @param pTimerTags Optional tags for the timer. Please be aware that a timer is identified by its name and all of
   * its tags.
   * @return {@link TimerSample} Created timer sample. The method never returns null.
   */
  @Override
  public TimerSample newTimerSample( String pName, TimeUnit pTimeUnit, Tag... pTimerTags ) {
    // Resolve current timestamp and create new timer sample.
    long lStart = this.getTimestamp(pTimeUnit);
    return new TimerSampleImpl(pName, pTimerTags, lStart, pTimeUnit);
  }

  /**
   * Method records the passed timer sample. Start time of the recorded sample is when the sample was created and stop
   * time will be this call.
   * 
   * @param pTimerSample Time sample that should be recorded. The parameter must not be null.
   */
  @Override
  public void recordTimerSample( TimerSample pTimerSample ) {
    // Check parameter
    Check.checkInvalidParameterNull(pTimerSample, "pTimerSample");

    // Record sample. Therefore we need the timer and of course the current timestamp.
    TimeUnit lTimeUnit = pTimerSample.getTimeUnit();
    long lStop = this.getTimestamp(lTimeUnit);
    Timer lTimer = this.getTimer(pTimerSample.getTimerName(), pTimerSample.getTimerTags());
    lTimer.record(lStop - pTimerSample.getStartTimestamp(), lTimeUnit);
  }

  /**
   * Method returns status information about the timer that is identified by the passed parameters.
   * 
   * @param pName Name of the timer. The parameter must not be null.
   * @param pTimerTags Optional tags for the timer. Please be aware that a timer is identified by its name and all of
   * its tags.
   * @return {@link TimerInfo} Status information about the requested timer. The method never returns null.
   */
  @Override
  public TimerInfo getTimerInfo( String pName, Tag... pTimerTags ) {
    Timer lTimer = this.getTimer(pName, pTimerTags);
    return new TimerInfoImpl(lTimer);
  }

  /**
   * Method increments the counter with the passed name and tags.Please be aware that a counter is identified by its
   * name and all of its tags.
   * 
   * @param pName Name of the counter. The parameter must be a real string.
   * @param pCounterTags Optional list of tags of the counter.
   */
  @Override
  public void incrementCounter( String pName, Tag... pCounterTags ) {
    this.getCounter(pName, pCounterTags).increment();
  }

  /**
   * Method returns the counter that matches to the passed name and tags. Please be aware that a counter is identified
   * by its name and all of its tags.
   * 
   * @param pName Name of the counter. The parameter must be a real string.
   * @param pCounterTags Optional list of tags of the counter.
   * @return {@link Counter} Counter that matches to the passed name and tags. The method never returns null. If a
   * matching counter already exists then it will be returned else a new one will be created.
   */
  @Override
  public CounterInfo getCounterInfo( String pName, Tag... pCounterTags ) {
    Counter lCounter = this.getCounter(pName, pCounterTags);
    return new CounterInfoImpl(lCounter);
  }

  /**
   * Method returns the counter that matches to the passed name and tags. Please be aware that a counter is identified
   * by its name and all of its tags.
   * 
   * @param pName Name of the counter. The parameter must be a real string.
   * @param pCounterTags Optional list of tags of the counter.
   * @return {@link Counter} Counter that matches to the passed name and tags. The method never returns null. If a
   * matching counter already exists then it will be returned else a new one will be created.
   */
  private Counter getCounter( String pName, Tag... pCounterTags ) {
    return meterRegistry.counter(pName, this.convertTags(pCounterTags));
  }

  /**
   * Method monitors the passed object using the defined method to retrieve the monitored value.
   * 
   * @param pMonitoredObject Object that should be monitored. The parameter must not be null.
   * @param pValueFunction Function that should be used to used to retrieve the value that should be monitored. The
   * parameter must not be null
   * @param pName Name of the value that will be monitored and will also be used to name the meter. The parameter must
   * be a real string. Please be aware that meters are identified by its name and all of its tags.
   * @param pTags Optional list of tags of the meter.
   */
  @Override
  public <T> void monitorObject( T pMonitoredObject, ToDoubleFunction<T> pValueFunction, String pName, Tag... pTags ) {
    // Create gauge and register it.
    Builder<T> lBuilder = Gauge.builder(pName, pMonitoredObject, pValueFunction);
    lBuilder.tags(this.convertTags(pTags));
    lBuilder.register(meterRegistry);
  }

  /**
   * Method returns a status information about the gauge that can be identified by the passed name and tags.
   * 
   * @param pName Name of the gauge that should be returned. The parameter must be a real string. Please be aware that
   * meters are identified by its name and all of its tags.
   * @param pTags Optional list of tags of the meter.
   * @return {@link GaugeInfo} Info object representing the requested gauge. The method never returns null.H
   */
  @Override
  public GaugeInfo getGaugeInfo( String pName, Tag... pTags ) {
    RequiredSearch lSearch = meterRegistry.get(pName);
    lSearch.tags(this.convertTags(pTags));
    Gauge lGauge = lSearch.gauge();
    return new GaugeInfoImpl(lGauge);
  }

  /**
   * Method returns a monitoring token. Monitoring tokens can be used to link multiple threads that execute a business
   * transaction into a single monitoring transaction. Linking of threads is only required in case that some parts of a
   * business transaction are executed asynchronously.
   * 
   * To effectively link threads with each other method {@link MonitoringToken#link()} or
   * {@link MonitoringToken#linkAndRelease()} has to be called from the thread that should be linked to its "parent".
   * 
   * It's very important to release the token as soon as no further threads should be linked
   * ({@link MonitoringToken#release()}) .
   * 
   * @return {@link MonitoringToken} Token that can be used to link another thread with the current one to span them
   * together into a single transaction from a performance monitoring perspective.
   */
  @Override
  public MonitoringToken getMonitoringToken( ) {
    return new EmptyMonitoringTokenImpl();
  }

  /**
   * Method converts that passed tags into the representation as it is defined by the Micrometer API.
   * 
   * @param pTags Tags that should be converted. The parameter may be null.
   * @return {@link io.micrometer.core.instrument.Tag} Micrometer tags as they were created. The method never returns
   * null. In case that no tags were passed an empty list will b returned.
   */
  private List<io.micrometer.core.instrument.Tag> convertTags( Tag[] pTags ) {
    List<io.micrometer.core.instrument.Tag> lMicrometerTags;
    if (pTags != null) {
      lMicrometerTags = new ArrayList<>(pTags.length);
      for (Tag lNext : pTags) {
        lMicrometerTags.add(io.micrometer.core.instrument.Tag.of(lNext.getKey(), lNext.getValue()));
      }
    }
    else {
      lMicrometerTags = null;
    }
    return lMicrometerTags;
  }
}
