/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.string.StringTools;
import org.junit.jupiter.api.Test;

public class StringToolsTest {
  @Test
  public void testIsEmpty( ) {
    StringTools lStringTools = Tools.getStringTools();

    assertTrue(lStringTools.isEmpty(null));
    assertTrue(lStringTools.isEmpty(""));
    assertTrue(lStringTools.isEmpty(" "));
    assertTrue(lStringTools.isEmpty(" \n "));
    assertTrue(lStringTools.isEmpty(" \t "));
    assertFalse(lStringTools.isEmpty("abc"));
    assertFalse(lStringTools.isEmpty(" abc "));
  }

  @Test
  public void testIsRealString( ) {
    StringTools lStringTools = Tools.getStringTools();

    assertFalse(lStringTools.isRealString(null));
    assertFalse(lStringTools.isRealString(""));
    assertFalse(lStringTools.isRealString(" "));
    assertFalse(lStringTools.isRealString(" \n "));
    assertFalse(lStringTools.isRealString(" \t "));
    assertTrue(lStringTools.isRealString("abc"));
    assertTrue(lStringTools.isRealString(" abc "));
  }

  @Test
  public void testIsBlank( ) {
    StringTools lStringTools = Tools.getStringTools();

    assertTrue(lStringTools.isBlank(null));
    assertTrue(lStringTools.isBlank(""));
    assertTrue(lStringTools.isBlank(" "));
    assertTrue(lStringTools.isBlank(" \n "));
    assertTrue(lStringTools.isBlank(" \t "));
    assertFalse(lStringTools.isBlank("abc"));
    assertFalse(lStringTools.isBlank(" abc "));
  }

  @Test
  public void testIsNotBlank( ) {
    StringTools lStringTools = Tools.getStringTools();

    assertFalse(lStringTools.isNotBlank(null));
    assertFalse(lStringTools.isNotBlank(""));
    assertFalse(lStringTools.isNotBlank(" "));
    assertFalse(lStringTools.isNotBlank(" \n "));
    assertFalse(lStringTools.isNotBlank(" \t "));
    assertTrue(lStringTools.isNotBlank("abc"));
    assertTrue(lStringTools.isNotBlank(" abc "));
  }

  @Test
  public void testContainsOnlyDigits( ) {
    StringTools lStringTools = Tools.getStringTools();
    assertTrue(lStringTools.containsDigitsOnly("12344"));
    assertFalse(lStringTools.containsDigitsOnly(" 12344 \t"));
    assertFalse(lStringTools.containsDigitsOnly(" 12 "));
    assertFalse(lStringTools.containsDigitsOnly("-12"));
    assertFalse(lStringTools.containsDigitsOnly("1.2"));
  }

  @Test
  public void testSplit( ) {
    StringTools lStringTools = Tools.getStringTools();
    List<String> lSplitResult = lStringTools.split("boo:and:foo", ":", true);
    assertEquals(3, lSplitResult.size());
    assertEquals("boo", lSplitResult.get(0));
    assertEquals("and", lSplitResult.get(1));
    assertEquals("foo", lSplitResult.get(2));

    lSplitResult = lStringTools.split(" Hello # World #!", "#", false);
    assertEquals(3, lSplitResult.size());
    assertEquals(" Hello ", lSplitResult.get(0));
    assertEquals(" World ", lSplitResult.get(1));
    assertEquals("!", lSplitResult.get(2));

    lSplitResult = lStringTools.split(" Hello # World #!", "#", true);
    assertEquals(3, lSplitResult.size());
    assertEquals("Hello", lSplitResult.get(0));
    assertEquals("World", lSplitResult.get(1));
    assertEquals("!", lSplitResult.get(2));

    lSplitResult = lStringTools.split(null, ":", false);
    assertEquals(0, lSplitResult.size());

    try {
      lStringTools.split("abc", null, false);
      fail("Expection expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testEndsWith( ) {
    StringTools lStringTools = Tools.getStringTools();

    List<String> lSuffixes = new ArrayList<>();
    lSuffixes.add("txt");
    lSuffixes.add("doc");
    lSuffixes.add(".xyz");
    assertFalse(lStringTools.endsWith("Hello", lSuffixes));
    assertTrue(lStringTools.endsWith("Hellotxt", lSuffixes));
    assertTrue(lStringTools.endsWith(".xyz", lSuffixes));
    assertFalse(lStringTools.endsWith(" doc ", lSuffixes));
    assertFalse(lStringTools.endsWith("", lSuffixes));

    // Test exception handling
    try {
      lStringTools.endsWith(null, lSuffixes);
      fail();
    }
    catch (IllegalArgumentException e) {
      // Nothing to check.
    }

    try {
      lStringTools.endsWith("", null);
      fail();
    }
    catch (IllegalArgumentException e) {
      // Nothing to check.
    }

    try {
      lStringTools.endsWith(null, null);
      fail();
    }
    catch (IllegalArgumentException e) {
      // Nothing to check.
    }
  }

  @Test
  public void testCompareTo( ) {
    StringTools lStringTools = Tools.getStringTools();

    // Test case sensitive compare
    assertEquals(0, lStringTools.compareTo(null, null));
    assertEquals(-1, lStringTools.compareTo(null, "a"));
    assertEquals(1, lStringTools.compareTo("a", null));
    assertEquals(0, lStringTools.compareTo("a", "a"));
    assertEquals(-1, lStringTools.compareTo("a", "b"));
    assertEquals(1, lStringTools.compareTo("b", "a"));
    assertEquals(32, lStringTools.compareTo("a", "A"));
    assertEquals(-32, lStringTools.compareTo("A", "a"));

    // Test compare ignoring cases
    assertEquals(0, lStringTools.compareToIgnoreCase(null, null));
    assertEquals(-1, lStringTools.compareToIgnoreCase(null, "a"));
    assertEquals(1, lStringTools.compareToIgnoreCase("a", null));
    assertEquals(0, lStringTools.compareToIgnoreCase("a", "a"));
    assertEquals(-1, lStringTools.compareToIgnoreCase("a", "b"));
    assertEquals(1, lStringTools.compareToIgnoreCase("b", "a"));
    assertEquals(0, lStringTools.compareToIgnoreCase("a", "A"));
    assertEquals(0, lStringTools.compareToIgnoreCase("A", "a"));
    assertEquals(-1, lStringTools.compareToIgnoreCase("A", "b"));
  }

  @Test
  public void testGetClassNamesAsString( ) {
    // Test method with class array
    StringTools lStringTools = Tools.getStringTools();
    assertEquals("java.lang.String, java.lang.Integer",
        lStringTools.getClassNamesAsString(new Class<?>[] { String.class, Integer.class }));
    assertEquals("java.lang.String", lStringTools.getClassNamesAsString(new Class<?>[] { String.class }, "none"));
    assertEquals("java.lang.String:java.lang.Integer",
        lStringTools.getClassNamesAsString(new Class<?>[] { String.class, Integer.class }, "", ":"));

    assertEquals("none", lStringTools.getClassNamesAsString(new Class<?>[] {}, "none", ":"));
    assertEquals("none", lStringTools.getClassNamesAsString((Class<?>[]) null, "none", ":"));
    assertNull(lStringTools.getClassNamesAsString((Class<?>[]) null, null, ":"));

    try {
      lStringTools.getClassNamesAsString(new Class<?>[] { String.class, Integer.class }, "", null);
      fail();
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    assertEquals("java.lang.String, java.lang.Integer",
        lStringTools.getClassNamesAsString(Arrays.asList(new Class<?>[] { String.class, Integer.class })));
    assertEquals("java.lang.String",
        lStringTools.getClassNamesAsString(Arrays.asList(new Class<?>[] { String.class }), "none"));
    assertEquals("java.lang.String:java.lang.Integer",
        lStringTools.getClassNamesAsString(Arrays.asList(new Class<?>[] { String.class, Integer.class }), "", ":"));

    assertEquals("none", lStringTools.getClassNamesAsString(Arrays.asList(new Class<?>[] {}), "none", ":"));
    assertEquals("none", lStringTools.getClassNamesAsString((List<Class<?>>) null, "none", ":"));
    assertNull(lStringTools.getClassNamesAsString((List<Class<?>>) null, null, ":"));

    try {
      lStringTools.getClassNamesAsString(Arrays.asList(new Class<?>[] { String.class, Integer.class }), "", null);
      fail();
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }
}
