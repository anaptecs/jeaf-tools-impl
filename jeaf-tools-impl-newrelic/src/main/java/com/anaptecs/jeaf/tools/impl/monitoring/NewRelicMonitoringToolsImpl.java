/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.monitoring;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.monitoring.MonitoringToken;
import com.anaptecs.jeaf.tools.api.monitoring.MonitoringTools;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Transaction;

/**
 * Class implements JEAF Monitoring Tools based on New Relic as performance monitoring solution.
 * 
 * Besides the rather simple New Relic integration all other parts of Monitoring Tools are derived from the standard
 * implementation {@link MonitoringToolsImpl}.
 * 
 * @author JEAF Development Team
 */
@ToolsImplementation(toolsInterface = MonitoringTools.class)
public class NewRelicMonitoringToolsImpl extends MonitoringToolsImpl {
  /**
   * Initialize New Relic based monitoring tools.
   */
  public NewRelicMonitoringToolsImpl( ) {
    this(new MonitoringToolsConfiguration());
  }

  /**
   * Initialize New Relic based monitoring tools.
   * 
   * @param pConfiguration Configuration for monitoring tools. The parameter must not be null.
   */
  public NewRelicMonitoringToolsImpl( MonitoringToolsConfiguration pConfiguration ) {
    super(pConfiguration);
  }

  /**
   * Method returns a New Relic monitoring token. Monitoring tokens can be used to link multiple threads that execute a
   * business transaction into a single monitoring transaction. Linking of threads is only required in case that some
   * parts of a business transaction are executed asynchronously.
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
    Transaction lTransaction = NewRelic.getAgent().getTransaction();
    return new NewRelicMonitoringToken(lTransaction.getToken());
  }
}
