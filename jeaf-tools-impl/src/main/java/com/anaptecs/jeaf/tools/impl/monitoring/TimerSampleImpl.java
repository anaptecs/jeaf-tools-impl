/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.monitoring;

import java.util.concurrent.TimeUnit;

import com.anaptecs.jeaf.tools.api.monitoring.Tag;
import com.anaptecs.jeaf.tools.api.monitoring.TimerSample;
import com.anaptecs.jeaf.xfun.api.checks.Check;

public class TimerSampleImpl implements TimerSample {
  private final String timerName;

  private Tag[] timerTags;

  private final long startTimestamp;

  private final TimeUnit timeUnit;

  TimerSampleImpl( String pTimerName, Tag[] pTimerTags, long pStartTimestamp, TimeUnit pTimeUnit ) {
    // Check parameters
    Check.checkIsRealString(pTimerName, "pTimerName");
    Check.checkInvalidParameterNull(pTimeUnit, "pTimeUnit");

    timerName = pTimerName;
    timerTags = pTimerTags;
    startTimestamp = pStartTimestamp;
    timeUnit = pTimeUnit;
  }

  /**
   * Method returns the name of the timer.
   * 
   * @return {@link String} Name of the timer. the method always returns a real string.
   */
  @Override
  public String getTimerName( ) {
    return timerName;
  }

  /**
   * Method returns the tags of the timer.
   * 
   * @return {@link Tag} Array with all tags of the timer. the method may return null.
   */
  @Override
  public Tag[] getTimerTags( ) {
    return timerTags;
  }

  /**
   * Method returns the timestamp when the timer sample was started using the {@link TimeUnit} as returned by
   * {@link #getTimeUnit()}.
   * 
   * @return long Start timestamp.
   */
  @Override
  public long getStartTimestamp( ) {
    return startTimestamp;
  }

  /**
   * Method returns the time unit that is used to record the sample.
   * 
   * @return {@link TimeUnit} Time unit that is used for the sample. The method never returns null.
   */
  @Override
  public TimeUnit getTimeUnit( ) {
    return timeUnit;
  }
}
