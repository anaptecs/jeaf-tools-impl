/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.monitoring;

import com.anaptecs.jeaf.tools.api.monitoring.MonitoringToken;

/**
 * Class implements an empty monitoring token that will be used as default implementation. Please be aware that the
 * default implementation is not connected to any performance monitoring solution.
 */
public class EmptyMonitoringTokenImpl implements MonitoringToken {
  private boolean active = true;

  @Override
  public boolean link( ) {
    return active;
  }

  @Override
  public void release( ) {
    active = false;
  }

  @Override
  public boolean linkAndRelease( ) {
    boolean lSuccessful = this.link();
    this.release();
    return lSuccessful;
  }

  @Override
  public boolean isActive( ) {
    return active;
  }
}