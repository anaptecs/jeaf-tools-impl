/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.monitoring;

import java.util.concurrent.TimeUnit;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.monitoring.MonitoringTools;
import com.anaptecs.jeaf.tools.api.monitoring.TimerInfo;
import com.anaptecs.jeaf.xfun.api.checks.Check;

import io.micrometer.core.instrument.Timer;

/**
 * Class implements a timer info based on a Micrometer {@link Timer}.
 * 
 * @author JEAF Development Team
 */
public class TimerInfoImpl extends MeterInfoImpl implements TimerInfo {
  /**
   * Initialize object.
   * 
   * @param pTimer Micrometer timer that is used to provide timer info. The parameter must not be null.
   */
  public TimerInfoImpl( Timer pTimer ) {
    super(pTimer);
  }

  /**
   * Method returns the Micrometer timer that is used by this object to return information.
   * 
   * @return {@link Timer} The Timer. The method neve returns null.
   */
  private Timer getTimer( ) {
    return (Timer) this.getMeter();
  }

  /**
   * Method returns the amount of samples that were recorder for the timer.
   * 
   * @return long Number of recorded samples of the counter.
   */
  @Override
  public long getCount( ) {
    return this.getTimer().count();
  }

  /**
   * Method returns the total time of all recorded samples. Method uses the default time unit
   * {@link MonitoringTools#getDefaultTimeUnit()}.
   * 
   * @return double The total time of recorded samples using the default time unit.
   */
  @Override
  public double getTotalTime( ) {
    return this.getTotalTime(Tools.getMonitoringTools().getDefaultTimeUnit());
  }

  /**
   * Method returns the total time of all recorded samples.
   * 
   * @param pTimeUnit The base unit of time to scale the total to. The parameter must not be null.
   * @return double The total time of recorded samples using the passed time unit.
   */
  @Override
  public double getTotalTime( TimeUnit pTimeUnit ) {
    // Check parameter
    Check.checkInvalidParameterNull(pTimeUnit, "pTimeUnit");

    return this.getTimer().totalTime(pTimeUnit);
  }

  /**
   * Method returns the average time calculated over all recorded samples. Method uses the default time unit
   * {@link MonitoringTools#getDefaultTimeUnit()}.
   * 
   * @return double Average time of all recorded samples using the passed time unit.
   */
  @Override
  public double getMean( ) {
    return this.getMean(Tools.getMonitoringTools().getDefaultTimeUnit());
  }

  /**
   * Method returns the average time calculated over all recorded samples.
   * 
   * @param pTimeUnit The base unit of time to scale the average to. The parameter must not be null.
   * @return double Average time of all recorded samples using the passed time unit.
   */
  @Override
  public double getMean( TimeUnit pTimeUnit ) {
    // Check parameter
    Check.checkInvalidParameterNull(pTimeUnit, "pTimeUnit");

    return this.getTimer().mean(pTimeUnit);
  }

  /**
   * Method returns the maximum time of all recorded samples. Method uses the default time unit
   * {@link MonitoringTools#getDefaultTimeUnit()}.
   * 
   * @return double Maximum time of all recorded samples using the default time unit.
   */
  @Override
  public double getMax( ) {
    return this.getMax(Tools.getMonitoringTools().getDefaultTimeUnit());
  }

  /**
   * Method returns the maximum time of all recorded samples.
   * 
   * @param pTimeUnit The base unit of time to scale the total to. The parameter must not be null.
   * @return double Maximum time of all recorded samples using the passed time unit.
   */
  @Override
  public double getMax( TimeUnit pTimeUnit ) {
    // Check parameter
    Check.checkInvalidParameterNull(pTimeUnit, "pTimeUnit");

    return this.getTimer().max(pTimeUnit);
  }
}
