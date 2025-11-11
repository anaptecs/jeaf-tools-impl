/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.monitoring.jmx;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.xfun.api.checks.Check;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import com.codahale.metrics.jmx.ObjectNameFactory;

public class ObjectNameFactoryImpl implements ObjectNameFactory {

  @Override
  public ObjectName createName( String pType, String pDomain, String pName ) {
    // Check parameters
    Check.checkInvalidParameterNull(pType, "pType");
    Check.checkInvalidParameterNull(pName, "pName");

    int lLastIndex = pName.lastIndexOf('.');
    String lDomainSuffix;
    String lType;
    if (lLastIndex > 0) {
      String lPrefixSuffix = pName.substring(0, lLastIndex);
      pName = pName.substring(lLastIndex + 1);

      int lPrefixLastIndex = lPrefixSuffix.lastIndexOf('.');
      if (lPrefixLastIndex > 0) {
        lDomainSuffix = lPrefixSuffix.substring(0, lPrefixLastIndex);
        lType = lPrefixSuffix.substring(lPrefixLastIndex + 1);
      }
      else {
        lDomainSuffix = null;
        lType = lPrefixSuffix;
      }
    }
    else {
      lDomainSuffix = null;
      lType = pType;
    }

    StringBuilder lBuilder = new StringBuilder();
    boolean lHasDomain = false;
    if (Tools.getStringTools().isRealString(pDomain)) {
      lHasDomain = true;
      lBuilder.append(pDomain);
      if (lDomainSuffix != null) {
        lBuilder.append('.');
      }
    }
    if (lDomainSuffix != null) {
      lHasDomain = true;
      lBuilder.append(lDomainSuffix.toLowerCase());
    }
    if (lHasDomain == false) {
      lBuilder.append("default");
    }

    lBuilder.append(":type=");
    lBuilder.append(lType);
    lBuilder.append(",name=");
    lBuilder.append(pName);
    String lObjectName = lBuilder.toString();
    try {
      return new ObjectName(lObjectName);
    }
    catch (MalformedObjectNameException e) {
      throw new JEAFSystemException(ToolsMessages.INVALID_JMX_METER_NAME, e, lObjectName);
    }
  }

}
