/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.monitoring;

import com.anaptecs.jeaf.tools.api.monitoring.MonitoringToken;
import com.anaptecs.jeaf.xfun.api.checks.Check;
import com.newrelic.api.agent.Token;

/**
 * Class implements a {@link MonitoringToken} that can be used in combination with New Relic as Performance Monitoring
 * solution.
 * 
 * @author JEAF Development Team
 */
public class NewRelicMonitoringToken implements MonitoringToken {
  /**
   * New Relic Monitoring token that is wrapped by this class.
   */
  private final Token token;

  /**
   * Initialize object.
   * 
   * @param pToken New Relic monitoring token that should be wrapped by this class. The parameter must not be null.
   */
  public NewRelicMonitoringToken( Token pToken ) {
    // Check parameter
    Check.checkInvalidParameterNull(pToken, "pToken");

    token = pToken;
  }

  /**
   * Method links the current thread with the transaction to which the token originally belongs to.
   * 
   * @return boolean Method returns true if the current thread could be linked and false in all other cases.
   */
  @Override
  public boolean link( ) {
    return token.link();
  }

  /**
   * Method releases the token. This means that it can not be used any longer.
   */
  @Override
  public void release( ) {
    token.expire();
  }

  @Override
  public boolean linkAndRelease( ) {
    return token.linkAndExpire();
  }

  /**
   * Method checks if the token can still be used. As soon as the token was release it will not be active any longer.
   * 
   * @return boolean Method returns true if the token can still be used and false in all other cases.
   */
  @Override
  public boolean isActive( ) {
    return token.isActive();
  }
}
