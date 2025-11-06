/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.monitoring;

import com.anaptecs.jeaf.tools.api.monitoring.GaugeInfo;

import io.micrometer.core.instrument.Gauge;

/**
 * Class implements the status object for a gauge.
 * 
 * @author JEAF Development Team
 */
public class GaugeInfoImpl extends MeterInfoImpl implements GaugeInfo {
  /**
   * Initialize object.
   * 
   * @param pGauge Micrometer gauge objects that is wrapped by this implementation. The parameter must not be null.
   */
  GaugeInfoImpl( Gauge pGauge ) {
    super(pGauge);
  }

  /**
   * Method returns the current value of the gauge.
   * 
   * @return double Current value of the gauge.
   */
  @Override
  public double getValue( ) {
    Gauge lGauge = (Gauge) this.getMeter();
    return lGauge.value();
  }

}
