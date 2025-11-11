/**
 * Copyright 2004 - 2017 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.cache;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.cache.Cache;
import com.anaptecs.jeaf.tools.api.cache.CacheTools;
import com.anaptecs.jeaf.tools.api.cache.Cacheable;

/**
 * Class implements JEAF's cache tools.
 * 
 * @author JEAF Development Team
 * @version JEAF Release 1.3
 */
@ToolsImplementation(toolsInterface = CacheTools.class)
public class CacheToolsImpl implements CacheTools {

  /**
   * Method creates a new time based cache instance.
   * 
   * @param pCacheTTL Time to life of all objects inside the cache. After the TTL expired all objects of the cache will
   * be read again from the database. The parameter must not be null. The TTL is defined in seconds.
   */
  @Override
  public <K, O extends Cacheable<K>> Cache<K, O> createTimeBasedCache( int pCacheTTL ) {
    return new SimpleObjectCache<>(pCacheTTL);
  }

}
