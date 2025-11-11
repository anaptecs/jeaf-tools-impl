/*
 * anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * Copyright 2004 - 2013 All rights reserved.
 */

package com.anaptecs.jeaf.tools.impl.cache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.anaptecs.jeaf.tools.api.cache.Cache;
import com.anaptecs.jeaf.tools.api.cache.Cacheable;
import com.anaptecs.jeaf.xfun.api.checks.Check;

/**
 * Class is the base class of all cache implementations that store all instances of a persistent object class and make
 * them accessible through their cache key. For the content of the cache a time to life (TTL) can be defined. The TTL
 * defines the period within which all objects will be read from the database again.
 * 
 * The cache must only be accessed from service methods that provide an transaction context.
 * 
 * Subclasses have to ensure that both cache key and the cached object are immutable if required.
 * 
 * @author JEAF Development Team
 * @version JEAF Release 1.2
 */
public class SimpleObjectCache<K, O extends Cacheable<K>> implements Cache<K, O> {

  /**
   * Constant for conversion from seconds to milliseconds.
   */
  private static final int MILLIS = 1000;

  /**
   * Attribute contains the time to life of the cached objects in seconds.
   */
  private final int cacheTTL;

  /**
   * System timestamp indicating when the cached objects have to be reloaded again. If the attribute has the value null
   * then the data will never be refreshed.
   */
  private Long nextRefresh;

  /**
   * Map contains all cached objects.
   */
  private Map<K, O> cachedObjects = new HashMap<>();

  /**
   * Initialize new cache instance
   * 
   * @param pCacheTTL Time to life of all objects inside the cache. After the TTL expired all objects of the cache will
   * be read again from the database. The parameter must not be null. The TTL is defined in seconds.
   */
  public SimpleObjectCache( int pCacheTTL ) {
    cacheTTL = pCacheTTL;

    nextRefresh = System.currentTimeMillis() + (cacheTTL * MILLIS);
  }

  /**
   * Method returns the cached object with the passed key.
   * 
   * @param pCacheKey Key of the cached object that should be returned. The parameter must not be null-
   * @return CachedObject Cached object with the passed key. The method returns null if no object with the passed key
   * exists.
   */
  @Override
  public final synchronized O getCachedObject( K pCacheKey ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pCacheKey, "pCacheKey");

    // Refresh content if required.
    this.cleanIfExpired();
    O lCacheEntry = cachedObjects.get(pCacheKey);

    // Check if entry is expired and remove it in this case.
    if (lCacheEntry != null && this.isCacheEntryExpired(lCacheEntry) == true) {
      lCacheEntry = null;
      cachedObjects.remove(pCacheKey);
    }
    return lCacheEntry;
  }

  /**
   * Method adds the passed object to the cache. If an object with the same key already exists then it will be
   * overwritten.
   * 
   * @param pCacheObject Object that should be added to the cache. The parameter must not be null.
   */
  @Override
  public final synchronized void cacheObject( O pCacheObject ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pCacheObject, "pCacheObject");

    cachedObjects.put(pCacheObject.getCacheKey(), pCacheObject);
  }

  /**
   * Method returns the cache keys of all objects inside of the cache.
   * 
   * @return {@link List} List with the cache keys of all objects. The method never returns null.
   */
  public final synchronized List<K> getAllKeys( ) {
    // Refresh content if required.
    this.cleanIfExpired();

    // Return all cache keys.
    Set<K> lKeySet = cachedObjects.keySet();
    return new ArrayList<>(lKeySet);
  }

  /**
   * Method checks if the passed cache entry is expired.
   * 
   * @param pCachedObject Cache entry that should be checked. The parameter must not be null.
   * @return boolean Method returns true if the entry is expired and false in all other cases.
   */
  private boolean isCacheEntryExpired( O pCachedObject ) {
    Calendar lExpiryDate = pCachedObject.getExpiryDate();
    long lNow = System.currentTimeMillis();

    return lExpiryDate.getTimeInMillis() < lNow;
  }

  /**
   * Method returns all objects that are inside the cache.
   * 
   * @return {@link List} List with all cached objects. The method never returns null.
   */
  public final synchronized List<O> getAllCachedObjects( ) {
    // Refresh content if required.
    this.cleanIfExpired();

    // Return all cached objects.
    return new ArrayList<>(cachedObjects.values());
  }

  /**
   * Method reloads all properties if the property values are expired according to the defined refresh interval.
   */
  private void cleanIfExpired( ) {
    // Determine current timestamp
    final long lNow = System.currentTimeMillis();

    // If a refresh timestamp is defined and it is in the past then we have to refresh.
    if (nextRefresh != null && lNow > nextRefresh) {
      cachedObjects.clear();
      nextRefresh = lNow + (cacheTTL * MILLIS);
    }
  }
}
