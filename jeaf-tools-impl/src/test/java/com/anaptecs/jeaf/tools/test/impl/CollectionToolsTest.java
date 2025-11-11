/*
 * anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * Copyright 2004 - 2014 All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.collections.CollectionTools;
import org.junit.jupiter.api.Test;

/**
 * Test class to test the functionality of class com.anaptecs.jeaf.fwk.util.Tools.
 *
 * @author JEAF Development Team
 * @version 1.0
 */
public class CollectionToolsTest {
  /**
   * Method tests Collection Tools toString(...) implementation.
   */
  @Test
  public void testToString( ) {
    List<String> lStrings = new ArrayList<>();
    lStrings.add("Hello");
    lStrings.add(null);
    lStrings.add("World");
    lStrings.add("!");

    CollectionTools lCollectionTools = CollectionTools.getCollectionTools();
    assertEquals("HelloWorld!", lCollectionTools.toString(lStrings, ""), "Wrong string representation of collection");
    assertEquals("Hello World !", lCollectionTools.toString(lStrings, " "),
        "Wrong string representation of collection");
    assertEquals("Hello, World, !", lCollectionTools.toString(lStrings, ", "),
        "Wrong string representation of collection");
  }

  @Test
  public void testAddAll( ) {
    CollectionTools lCollectionTools = Tools.getCollectionTools();

    List<CharSequence> lStringList = new ArrayList<>();
    String[] lStrings = new String[] { "abc", "def" };
    boolean lAdded = lCollectionTools.addAll(lStringList, lStrings);
    assertTrue(lAdded);
    assertEquals("abc", lStringList.get(0));
    assertEquals("def", lStringList.get(1));

    lAdded = lCollectionTools.addAll(lStringList, "efg");
    assertTrue(lAdded);
    assertEquals("abc", lStringList.get(0));
    assertEquals("def", lStringList.get(1));
    assertEquals("efg", lStringList.get(2));

    lAdded = lCollectionTools.addAll(lStringList, (String[]) null);
    assertFalse(lAdded);

    lAdded = lCollectionTools.addAll(lStringList, new String[] {});
    assertFalse(lAdded);

    try {
      lCollectionTools.addAll(null, "xyz");
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testUnmodifiableCollectionClone( ) {
    CollectionTools lCollectionTools = Tools.getCollectionTools();

    Collection<String> lCollection = new ArrayList<>();
    lCollection.add("Hello");
    Collection<String> lClone = lCollectionTools.unmodifiableClone(lCollection);
    assertEquals(1, lClone.size());
    assertEquals("Hello", lClone.iterator().next());

    // Test that clone is really clone.
    lCollection.clear();
    assertTrue(lCollection.isEmpty());
    assertEquals(1, lClone.size());
    assertEquals("Hello", lClone.iterator().next());

    // Ensure that clone is immutable.
    try {
      lClone.add("Let's try it.");
      fail("Exception expected.");
    }
    catch (UnsupportedOperationException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testUnmodifiableListClone( ) {
    CollectionTools lCollectionTools = Tools.getCollectionTools();

    List<String> lList = new ArrayList<>();
    lList.add("Hello");
    List<String> lClone = lCollectionTools.unmodifiableClone(lList);
    assertEquals(1, lClone.size());
    assertEquals("Hello", lClone.iterator().next());

    // Test that clone is really clone.
    lList.clear();
    assertTrue(lList.isEmpty());
    assertEquals(1, lClone.size());
    assertEquals("Hello", lClone.iterator().next());

    // Ensure that clone is immutable.
    try {
      lClone.add("Let's try it.");
      fail("Exception expected.");
    }
    catch (UnsupportedOperationException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testUnmodifiableSetClone( ) {
    CollectionTools lCollectionTools = Tools.getCollectionTools();

    Set<String> lSet = new HashSet<>();
    lSet.add("Hello");
    Set<String> lClone = lCollectionTools.unmodifiableClone(lSet);
    assertEquals(1, lClone.size());
    assertEquals("Hello", lClone.iterator().next());

    // Test that clone is really clone.
    lSet.clear();
    assertTrue(lSet.isEmpty());
    assertEquals(1, lClone.size());
    assertEquals("Hello", lClone.iterator().next());

    // Ensure that clone is immutable.
    try {
      lClone.add("Let's try it.");
      fail("Exception expected.");
    }
    catch (UnsupportedOperationException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testUnmodifiableSortedSetClone( ) {
    CollectionTools lCollectionTools = Tools.getCollectionTools();

    SortedSet<String> lSortedSet = new TreeSet<>();
    lSortedSet.add("Hello");
    SortedSet<String> lClone = lCollectionTools.unmodifiableClone(lSortedSet);
    assertEquals(1, lClone.size());
    assertEquals("Hello", lClone.iterator().next());

    // Test that clone is really clone.
    lSortedSet.clear();
    assertTrue(lSortedSet.isEmpty());
    assertEquals(1, lClone.size());
    assertEquals("Hello", lClone.iterator().next());

    // Ensure that clone is immutable.
    try {
      lClone.add("Let's try it.");
      fail("Exception expected.");
    }
    catch (UnsupportedOperationException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testUnmodifiableMapClone( ) {
    CollectionTools lCollectionTools = Tools.getCollectionTools();

    Map<String, String> lMap = new HashMap<>();
    lMap.put("Key", "Hello");
    Map<String, String> lClone = lCollectionTools.unmodifiableClone(lMap);
    assertEquals(1, lClone.size());
    assertEquals("Hello", lClone.get("Key"));

    // Test that clone is really clone.
    lMap.clear();
    assertTrue(lMap.isEmpty());
    assertEquals(1, lClone.size());
    assertEquals("Hello", lClone.get("Key"));

    // Ensure that clone is immutable.
    try {
      lClone.put("Key2", "Let's try it.");
      fail("Exception expected.");
    }
    catch (UnsupportedOperationException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testFilterCollection( ) {
    CollectionTools lCollectionTools = Tools.getCollectionTools();

    List<CharSequence> lStrings = new ArrayList<>();
    lStrings.add("String 1");
    lStrings.add(new StringBuffer("Buffer 1"));
    lStrings.add(new StringBuffer("Buffer 2"));
    lStrings.add("String 2");

    List<String> lStringList = lCollectionTools.filter(lStrings, String.class);
    assertEquals(2, lStringList.size());
    assertEquals("String 1", lStringList.get(0));
    assertEquals("String 2", lStringList.get(1));

    List<StringBuffer> lStringBuffers = lCollectionTools.filter(lStrings, StringBuffer.class);
    assertEquals(2, lStringBuffers.size());
    assertEquals("Buffer 1", lStringBuffers.get(0).toString());
    assertEquals("Buffer 2", lStringBuffers.get(1).toString());

    // Test error handling
    try {
      lCollectionTools.filter((Collection<?>) null, String.class);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lCollectionTools.filter(lStrings, null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testFilterMap( ) {
    CollectionTools lCollectionTools = Tools.getCollectionTools();

    Map<String, CharSequence> lMap = new HashMap<>();
    lMap.put("1", "String 1");
    lMap.put("2", new StringBuffer("Buffer 1"));
    lMap.put("3", new StringBuffer("Buffer 2"));
    lMap.put("4", "String 2");

    List<String> lStringList = lCollectionTools.filter(lMap, String.class);
    assertEquals(2, lStringList.size());
    assertEquals("String 1", lStringList.get(0));
    assertEquals("String 2", lStringList.get(1));

    List<StringBuffer> lStringBuffers = lCollectionTools.filter(lMap, StringBuffer.class);
    assertEquals(2, lStringBuffers.size());
    assertEquals("Buffer 1", lStringBuffers.get(0).toString());
    assertEquals("Buffer 2", lStringBuffers.get(1).toString());

    // Test error handling
    try {
      lCollectionTools.filter((Collection<?>) null, String.class);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lCollectionTools.filter(lMap, null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }
}
