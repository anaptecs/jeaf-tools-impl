/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.monitoring;

import java.util.ArrayList;
import java.util.List;

import com.anaptecs.jeaf.tools.api.monitoring.MeterInfo;
import com.anaptecs.jeaf.tools.api.monitoring.Tag;
import com.anaptecs.jeaf.xfun.api.checks.Check;

import io.micrometer.core.instrument.Meter;

/**
 * Class implements a base class for kinds of metes.
 * 
 * @author JEAF Development Team
 */
public abstract class MeterInfoImpl implements MeterInfo {
  /**
   * Micrometer mete object that is wrapped by this class.
   */
  private final Meter meter;

  /**
   * Initialize object
   * 
   * @param pMeter Meter that should be wrapped by this object. The paramter must not be null.
   */
  protected MeterInfoImpl( Meter pMeter ) {
    // Check parameter
    Check.checkInvalidParameterNull(pMeter, "pMeter");

    meter = pMeter;
  }

  /**
   * Method returns the meter that is wrapped by this object.
   * 
   * @return {@link Meter} Meter that is wrapped by this object. The method never returns null.
   */
  protected final Meter getMeter( ) {
    return meter;
  }

  /**
   * Method return the name of the meter.
   * 
   * @return {@link String} Name of the meter. The method always returns a real string.
   */
  @Override
  public String getName( ) {
    return meter.getId().getName();
  }

  /**
   * Method returns all tags of the timer.
   * 
   * @return {@link Tag} Array with all tags of the timer. The method may return null as tags are optional.
   */
  @Override
  public List<Tag> getTags( ) {
    List<io.micrometer.core.instrument.Tag> lTags = meter.getId().getTags();
    return this.convertTags(lTags);
  }

  /**
   * Method converts the passed Micrometer tags into the matching representation of JEAF's Monitoring tools.
   * 
   * @param pMicrometerTags Micrometer tags that should be converted. The parameter may be null.
   * @return {@link List} List with all tags as they are represented by JEAF's Monitoring Tools. If the null was passed
   * then an empty list wil be returned.
   */
  private List<Tag> convertTags( List<io.micrometer.core.instrument.Tag> pMicrometerTags ) {
    List<Tag> lTags = new ArrayList<>(pMicrometerTags.size());
    for (io.micrometer.core.instrument.Tag lNext : pMicrometerTags) {
      Tag lTag = Tag.of(lNext.getKey(), lNext.getValue());
      lTags.add(lTag);
    }
    return lTags;
  }

}
