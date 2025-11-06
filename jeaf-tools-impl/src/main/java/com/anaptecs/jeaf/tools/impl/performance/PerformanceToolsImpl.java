/**
 * Copyright 2004 - 2019 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.performance;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.performance.PerformanceTools;
import com.anaptecs.jeaf.tools.api.performance.Stopwatch;
import com.anaptecs.jeaf.tools.api.performance.TimePrecision;

/**
 * Class implements JEAF's Performance Tools.
 * 
 * @author JEAF Development Team
 */
@ToolsImplementation(toolsInterface = PerformanceTools.class)
public class PerformanceToolsImpl implements PerformanceTools {

  /**
   * Method creates a new stop watch. A stop watch can be used multiple times if required. However creating a new one is
   * also very cheap.
   * 
   * @param pName Name of the executed measurement. The parameter may be null.
   * @param pPrecision Precision with which the stop watch should report results.
   */
  @Override
  public Stopwatch createStopwatch( String pName, TimePrecision pTimePrecision ) {
    // Create new stop watch and return it.
    return new StopwatchImpl(pName, pTimePrecision);
  }
}
