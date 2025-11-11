/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.cache;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.anaptecs.jeaf.tools.api.cache.Cacheable;

public class CacheableObject implements Cacheable<Long> {
  private final long id;

  private final Calendar expiryDate;

  public CacheableObject( long pID, long pTTL ) {
    id = pID;
    expiryDate = new GregorianCalendar();
    expiryDate.setTimeInMillis(expiryDate.getTimeInMillis() + pTTL);
  }

  @Override
  public Long getCacheKey( ) {
    return id;
  }

  @Override
  public Calendar getExpiryDate( ) {
    return expiryDate;
  }

}
