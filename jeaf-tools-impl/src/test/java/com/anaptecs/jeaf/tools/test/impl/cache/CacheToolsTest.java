/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.cache.Cache;
import com.anaptecs.jeaf.tools.api.cache.CacheTools;
import com.anaptecs.jeaf.tools.api.performance.PerformanceTools;
import com.anaptecs.jeaf.tools.api.performance.Stopwatch;
import com.anaptecs.jeaf.tools.api.performance.TimePrecision;
import com.anaptecs.jeaf.tools.impl.cache.SimpleObjectCache;
import com.anaptecs.jeaf.xfun.api.XFun;
import org.junit.jupiter.api.Test;

public class CacheToolsTest {

  @Test
  public void testSimpleObjectCache( ) throws InterruptedException {
    // Initialize JEAF X-Fun.
    XFun.getTrace().info("Starting Cache tests");
    int lCacheTTLSeconds = 1;
    SimpleObjectCache<Long, CacheableObject> lCache = new SimpleObjectCache<>(lCacheTTLSeconds);

    int lShortTermTTL = 30;
    CacheableObject lShortTermObject1 = new CacheableObject(1, lShortTermTTL);
    lCache.cacheObject(lShortTermObject1);
    CacheableObject lShortTermObject2 = new CacheableObject(2, lShortTermTTL);
    lCache.cacheObject(lShortTermObject2);

    CacheableObject lCachedObject = lCache.getCachedObject(1L);
    assertEquals(lShortTermObject1, lCachedObject);
    lCachedObject = lCache.getCachedObject(2L);
    assertEquals(lShortTermObject2, lCachedObject);

    int lLongTermTTL = lCacheTTLSeconds * 1000 * 2;
    CacheableObject lLongTermObject1 = new CacheableObject(3, lLongTermTTL);
    lCache.cacheObject(lLongTermObject1);
    lCachedObject = lCache.getCachedObject(3L);
    assertEquals(lLongTermObject1, lCachedObject);

    List<Long> lAllKeys = lCache.getAllKeys();
    assertTrue(lAllKeys.contains(1L));
    assertTrue(lAllKeys.contains(2L));
    assertTrue(lAllKeys.contains(3L));
    assertEquals(3, lAllKeys.size());

    List<CacheableObject> lAllCachedObjects = lCache.getAllCachedObjects();
    assertTrue(lAllCachedObjects.contains(lShortTermObject1));
    assertTrue(lAllCachedObjects.contains(lShortTermObject2));
    assertTrue(lAllCachedObjects.contains(lLongTermObject1));
    assertEquals(3, lAllCachedObjects.size());

    // Test eviction strategies.

    // Test eviction of individual entries
    Thread.sleep(lShortTermTTL + 5);
    lCachedObject = lCache.getCachedObject(1L);
    assertNull(lCachedObject);
    lCachedObject = lCache.getCachedObject(2L);
    assertNull(lCachedObject);
    lCachedObject = lCache.getCachedObject(3L);
    assertEquals(lLongTermObject1, lCachedObject);

    // Test overall eviction of whole cache.
    Thread.sleep(lCacheTTLSeconds * 1000);
    lCachedObject = lCache.getCachedObject(1L);
    assertNull(lCachedObject);
    lCachedObject = lCache.getCachedObject(2L);
    assertNull(lCachedObject);
    lCachedObject = lCache.getCachedObject(3L);
    assertNull(lCachedObject);

    // Test exception handling
    try {
      lCache.cacheObject(null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    try {
      lCache.getCachedObject(null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testCachePerformance( ) {
    int lCacheTTLSeconds = 1000;
    SimpleObjectCache<Long, CacheableObject> lCache = new SimpleObjectCache<>(lCacheTTLSeconds);

    int lLoops = 1000000;
    List<CacheableObject> lObjects = new ArrayList<>(lLoops);
    int lShortTermTTL = 1000000;
    PerformanceTools lPerformanceTools = Tools.getPerformanceTools();
    Stopwatch lStopwatch = lPerformanceTools.createStopwatch("Object creation", TimePrecision.NANOS);
    lStopwatch.start();
    for (int i = 0; i < lLoops; i++) {
      lObjects.add(new CacheableObject(i, lShortTermTTL));
    }
    lStopwatch.stopAndTrace(lLoops);

    lStopwatch = lPerformanceTools.createStopwatch("Add to cache", TimePrecision.NANOS);
    lStopwatch.start();
    for (int i = 0; i < lLoops; i++) {
      lCache.cacheObject(lObjects.get(i));
    }
    lStopwatch.stopAndTrace(lLoops);

    lStopwatch = lPerformanceTools.createStopwatch("Read from cache", TimePrecision.NANOS);
    lStopwatch.start();
    for (int i = 0; i < lLoops; i++) {
      CacheableObject lCachedObject = lCache.getCachedObject((long) i);
      assertNotNull(lCachedObject);
    }
    lStopwatch.stopAndTrace(lLoops);
  }

  @Test
  public void testCacheToolsAccess( ) {
    CacheTools lCacheTools = Tools.getCacheTools();
    Cache<Long, CacheableObject> lCache = lCacheTools.createTimeBasedCache(1);
    assertNotNull(lCache);
    assertEquals(SimpleObjectCache.class, lCache.getClass());

    CacheableObject lObject = new CacheableObject(1, 100);
    lCache.cacheObject(lObject);
    assertEquals(lObject, lCache.getCachedObject(1L));
  }
}
