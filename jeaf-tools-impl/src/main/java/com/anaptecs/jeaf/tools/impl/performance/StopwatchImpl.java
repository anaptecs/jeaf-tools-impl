/**
 * Copyright 2004 - 2018 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.performance;

import com.anaptecs.jeaf.tools.api.performance.Stopwatch;
import com.anaptecs.jeaf.tools.api.performance.StopwatchResult;
import com.anaptecs.jeaf.tools.api.performance.TimePrecision;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.checks.Check;

/**
 * Class implements a simple stop watch that can be used for performance measurements
 * 
 * @author JEAF Development Team
 * @version JEAF Release 1.3
 */
public class StopwatchImpl implements Stopwatch {
  /**
   * Name of the executed measurement.
   */
  private final String name;

  /**
   * Number of iterations.
   */
  private long count;

  /**
   * Precision with which the stop watch should report results. Internally it is always working with nano time
   * precision.
   */
  private final TimePrecision precision;

  /**
   * Start time of stop watch
   */
  private long start;

  /**
   * Stop time of stop watch
   */
  private long stop;

  /**
   * Create new stop watch.
   * 
   * @param pName Name of the executed measurement. The parameter may be null.
   * @param pPrecision Precision with which the stop watch should report results. Internally it is always working with
   * nano time precision.
   */
  public StopwatchImpl( String pName, TimePrecision pPrecision ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pPrecision, pName);

    name = pName;
    precision = pPrecision;
  }

  /**
   * Method starts the stop watch. A stop watch can be started multiple times. When starting the stop watch may be
   * existing results will be deleted.
   */
  @Override
  public Stopwatch start( ) {
    this.start(0);
    return this;
  }

  /**
   * Method starts the stop watch. A stop watch can be started multiple times. When starting the stop watch may be
   * existing results will be deleted.
   * 
   * @param pName Name of the executed measurement. The parameter may be null.
   * @param pCount number of iterations.
   */
  @Override
  public Stopwatch start( long pCount ) {
    count = pCount;
    start = System.nanoTime();
    stop = 0;
    return this;
  }

  /**
   * Method stops a running measurement.
   */
  @Override
  public void stop( ) {
    if (this.isRunning()) {
      stop = System.nanoTime();
    }
  }

  /**
   * Method stops a running measurement.
   * 
   * @param pCount Number of iterations.
   */
  @Override
  public void stop( long pCount ) {
    if (this.isRunning()) {
      count = pCount;
      this.stop();
    }
  }

  /**
   * Method stops a running measurements and logs the result.
   */
  @Override
  public void stopAndTrace( ) {
    if (this.isRunning()) {
      this.stop();
      this.traceResult();
    }
  }

  /**
   * Method stops a running measurements and logs the result.
   * 
   * @param pCount Number of iterations.
   */
  @Override
  public void stopAndTrace( long pCount ) {
    if (this.isRunning()) {
      count = pCount;
      this.stopAndTrace();
    }
  }

  /**
   * Method checks if the stop watch is still running.
   * 
   * @return boolean Method returns true if the stop watch is still running and otherwise false.
   */
  @Override
  public boolean isRunning( ) {
    return start != 0 && stop == 0;
  }

  /**
   * Method returns the result of the measurement. If the stop watch is not stopped then it will be done by this method.
   * 
   * @return {@link StopwatchResult} Result of the measurement.
   */
  @Override
  public StopwatchResult getResult( ) {
    if (this.isRunning() == true) {
      this.stop();
    }

    // Create result for measurement and return it.
    long lDuration = (stop - start) / precision.getDurationDivisor();

    return new StopwatchResult(lDuration, precision, count);
  }

  /**
   * Method prints the results of a measurement to sysout.
   */
  @Override
  public void traceResult( ) {
    long lDuration = (stop - start) / precision.getDurationDivisor();
    String lUnit = precision.getUnit();
    double lTPS = (double) count / lDuration * precision.getTPSFactor();

    XFun.getTrace().info(name + " " + lDuration + lUnit);
    if (count > 0) {
      XFun.getTrace().info("Objects / cycles: " + count + " Time per cycle: " + (((double) lDuration) / count) + lUnit);
      XFun.getTrace().info("TPS: " + lTPS);
    }
  }
}
