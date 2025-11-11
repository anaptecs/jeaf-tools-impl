/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.lang.AutoBoxingTools;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AutoBoxingToolsTest {
  @Test
  @Order(10)
  public void testBooleanAutoBoxing( ) {
    // Test handling of primitives.
    AutoBoxingTools lTools = Tools.getAutoBoxingTools();
    assertNotNull(lTools, "AutoBoxingTools not available.");

    // Test auto boxing from primitive to wrapper
    assertEquals(Boolean.TRUE, lTools.autoBox(true));
    assertEquals(Boolean.FALSE, lTools.autoBox(false));

    // Test auto boxing from wrapper to primitive
    assertTrue(lTools.autoBox(Boolean.TRUE));
    assertFalse(lTools.autoBox(Boolean.FALSE));
    try {
      lTools.autoBox((Boolean) null);
      fail("Expection expected when 'null' is passed as wrapper type.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    // Test auto boxing from wrapper to primitive (handling of null values)
    assertTrue(lTools.autoBox((Boolean) null, Boolean.TRUE));
    assertFalse(lTools.autoBox((Boolean) null, Boolean.FALSE));
    assertTrue(lTools.autoBox(Boolean.TRUE, Boolean.FALSE));

    // Test handling of arrays.
    Boolean[] lWrapperArray = new Boolean[] { Boolean.TRUE, Boolean.TRUE, Boolean.FALSE };
    boolean[] lExpectedPrimitiveArray = new boolean[] { true, true, false };
    assertTrue(Arrays.equals(lExpectedPrimitiveArray, lTools.autoBox(lWrapperArray)));
    assertNull(lTools.autoBox((Boolean[]) null));

    boolean[] lPrimitiveArray = new boolean[] { false, false, false, true, false };
    Boolean[] lExpectedWrapperArray =
        new Boolean[] { Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE };
    assertTrue(Arrays.equals(lExpectedWrapperArray, lTools.autoBox(lPrimitiveArray)));
    assertNull(lTools.autoBox((boolean[]) null));
  }

  @Test
  @Order(20)
  public void testByteAutoBoxing( ) {
    // Test handling of primitives.
    AutoBoxingTools lTools = Tools.getAutoBoxingTools();
    assertNotNull(lTools, "AutoBoxingTools not available.");

    // Test auto boxing from primitive to wrapper
    assertEquals(Byte.valueOf((byte) 33), lTools.autoBox((byte) 33));
    assertNotEquals(Byte.valueOf((byte) 33), lTools.autoBox((byte) 42));

    // Test auto boxing from wrapper to primitive
    assertEquals(1, lTools.autoBox(Byte.valueOf((byte) 1)));
    assertEquals(47, lTools.autoBox(Byte.valueOf((byte) 47)));

    try {
      lTools.autoBox((Byte) null);
      fail("Expection expected when 'null' is passed as wrapper type.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    // Test auto boxing from wrapper to primitive (handling of null values)
    assertEquals((byte) 6, lTools.autoBox((Byte) null, (byte) 6));
    assertEquals((byte) -99, lTools.autoBox((Byte) null, (byte) -99));
    assertEquals((byte) 33, lTools.autoBox(Byte.valueOf((byte) 33), (byte) -99));

    // Test auto boxing of wrapper arrays to primitives
    Byte[] lWrapperArray = new Byte[] { Byte.valueOf((byte) 12), Byte.valueOf((byte) -1), Byte.valueOf((byte) 22) };
    byte[] lExpectedPrimitiveArray = new byte[] { 12, -1, 22 };
    assertTrue(Arrays.equals(lExpectedPrimitiveArray, lTools.autoBox(lWrapperArray)));
    assertNull(lTools.autoBox((Byte[]) null));

    // Test auto boxing of primitive arrays to wrappers
    byte[] lPrimitiveArray = new byte[] { -7, 22, 33, 44, 11 };
    Byte[] lExpectedWrapperArray = new Byte[] { Byte.valueOf((byte) -7), Byte.valueOf((byte) 22),
      Byte.valueOf((byte) 33), Byte.valueOf((byte) 44), Byte.valueOf((byte) 11) };
    assertTrue(Arrays.equals(lExpectedWrapperArray, lTools.autoBox(lPrimitiveArray)));
    assertNull(lTools.autoBox((byte[]) null));
  }

  @Test
  @Order(30)
  public void testShortAutoBoxing( ) {
    // Test handling of primitives.
    AutoBoxingTools lTools = Tools.getAutoBoxingTools();
    assertNotNull(lTools, "AutoBoxingTools not available.");

    // Test auto boxing from primitive to wrapper
    assertEquals(Short.valueOf((short) 33333), lTools.autoBox((short) 33333));
    assertNotEquals(Short.valueOf((short) 33), lTools.autoBox((short) 42));

    // Test auto boxing from wrapper to primitive
    assertEquals(1, lTools.autoBox(Short.valueOf((short) 1)));
    assertEquals(47, lTools.autoBox(Short.valueOf((short) 47)));

    try {
      lTools.autoBox((Short) null);
      fail("Expection expected when 'null' is passed as wrapper type.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    // Test auto boxing from wrapper to primitive (handling of null values)
    assertEquals((short) 6, lTools.autoBox((Short) null, (short) 6));
    assertEquals((short) -99, lTools.autoBox((Short) null, (short) -99));
    assertEquals((short) 33, lTools.autoBox(Short.valueOf((short) 33), (byte) -99));

    // Test auto boxing of wrapper arrays to primitives
    Short[] lWrapperArray =
        new Short[] { Short.valueOf((short) 12), Short.valueOf((short) -1), Short.valueOf((short) 22) };
    short[] lExpectedPrimitiveArray = new short[] { 12, -1, 22 };
    assertTrue(Arrays.equals(lExpectedPrimitiveArray, lTools.autoBox(lWrapperArray)));
    assertNull(lTools.autoBox((Short[]) null));

    // Test auto boxing of primitive arrays to wrappers
    short[] lPrimitiveArray = new short[] { -7, 22, 33, 44, 11 };
    Short[] lExpectedWrapperArray = new Short[] { Short.valueOf((short) -7), Short.valueOf((short) 22),
      Short.valueOf((short) 33), Short.valueOf((short) 44), Short.valueOf((short) 11) };
    assertTrue(Arrays.equals(lExpectedWrapperArray, lTools.autoBox(lPrimitiveArray)));
    assertNull(lTools.autoBox((short[]) null));
  }

  @Test
  @Order(40)
  public void testIntegerAutoBoxing( ) {
    // Test handling of primitives.
    AutoBoxingTools lTools = Tools.getAutoBoxingTools();
    assertNotNull(lTools, "AutoBoxingTools not available.");

    // Test auto boxing from primitive to wrapper
    assertEquals(Integer.valueOf(333333), lTools.autoBox(333333));
    assertNotEquals(Integer.valueOf(33), lTools.autoBox(42));

    // Test auto boxing from wrapper to primitive
    assertEquals(1, lTools.autoBox(Integer.valueOf(1)));
    assertEquals(47, lTools.autoBox(Integer.valueOf(47)));

    try {
      lTools.autoBox((Integer) null);
      fail("Expection expected when 'null' is passed as wrapper type.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    // Test auto boxing from wrapper to primitive (handling of null values)
    assertEquals(6, lTools.autoBox((Integer) null, 6));
    assertEquals(-99, lTools.autoBox((Integer) null, -99));
    assertEquals(33, lTools.autoBox(Integer.valueOf(33), (byte) -99));

    // Test auto boxing of wrapper arrays to primitives
    Integer[] lWrapperArray = new Integer[] { Integer.valueOf(12), Integer.valueOf(-1), Integer.valueOf(22) };
    int[] lExpectedPrimitiveArray = new int[] { 12, -1, 22 };
    assertTrue(Arrays.equals(lExpectedPrimitiveArray, lTools.autoBox(lWrapperArray)));
    assertNull(lTools.autoBox((Integer[]) null));

    // Test auto boxing of primitive arrays to wrappers
    int[] lPrimitiveArray = new int[] { -7, 22, 33, 44, 11 };
    Integer[] lExpectedWrapperArray = new Integer[] { Integer.valueOf(-7), Integer.valueOf(22), Integer.valueOf(33),
      Integer.valueOf(44), Integer.valueOf(11) };
    assertTrue(Arrays.equals(lExpectedWrapperArray, lTools.autoBox(lPrimitiveArray)));
    assertNull(lTools.autoBox((int[]) null));
  }

  @Test
  @Order(50)
  public void testLongAutoBoxing( ) {
    // Test handling of primitives.
    AutoBoxingTools lTools = Tools.getAutoBoxingTools();
    assertNotNull(lTools, "AutoBoxingTools not available.");

    // Test auto boxing from primitive to wrapper
    assertEquals(Long.valueOf(333333333), lTools.autoBox((long) 333333333));
    assertNotEquals(Long.valueOf(33), lTools.autoBox((long) 42));

    // Test auto boxing from wrapper to primitive
    assertEquals(1, lTools.autoBox(Long.valueOf(1)));
    assertEquals(47, lTools.autoBox(Long.valueOf(47)));

    try {
      lTools.autoBox((Long) null);
      fail("Expection expected when 'null' is passed as wrapper type.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    // Test auto boxing from wrapper to primitive (handling of null values)
    assertEquals(6, lTools.autoBox((Long) null, 6));
    assertEquals(-99, lTools.autoBox((Long) null, -99));
    assertEquals(33, lTools.autoBox(Long.valueOf(33), -99));

    // Test auto boxing of wrapper arrays to primitives
    Long[] lWrapperArray = new Long[] { Long.valueOf(12), Long.valueOf(-1), Long.valueOf(22) };
    long[] lExpectedPrimitiveArray = new long[] { 12, -1, 22 };
    assertTrue(Arrays.equals(lExpectedPrimitiveArray, lTools.autoBox(lWrapperArray)));
    assertNull(lTools.autoBox((Long[]) null));

    // Test auto boxing of primitive arrays to wrappers
    long[] lPrimitiveArray = new long[] { -7, 22, 33, 44, 11 };
    Long[] lExpectedWrapperArray = new Long[] { Long.valueOf(-7), Long.valueOf(22),
      Long.valueOf(33), Long.valueOf(44), Long.valueOf(11) };
    assertTrue(Arrays.equals(lExpectedWrapperArray, lTools.autoBox(lPrimitiveArray)));
    assertNull(lTools.autoBox((long[]) null));
  }

  @Test
  @Order(60)
  public void testFloatAutoBoxing( ) {
    // Test handling of primitives.
    AutoBoxingTools lTools = Tools.getAutoBoxingTools();
    assertNotNull(lTools, "AutoBoxingTools not available.");

    // Test auto boxing from primitive to wrapper
    assertEquals(Float.valueOf((float) 33333.3333), lTools.autoBox((float) 33333.3333));
    assertNotEquals(Float.valueOf(33), lTools.autoBox((float) 4.2));

    // Test auto boxing from wrapper to primitive
    assertEquals((float) 1.11, lTools.autoBox(Float.valueOf((float) 1.11)));
    assertEquals(47.0, lTools.autoBox(Float.valueOf((float) 47.0)));

    try {
      lTools.autoBox((Float) null);
      fail("Expection expected when 'null' is passed as wrapper type.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    // Test auto boxing from wrapper to primitive (handling of null values)
    assertEquals((float) 6.97, lTools.autoBox((Float) null, (float) 6.97));
    assertEquals((float) -99.1, lTools.autoBox((Float) null, (float) -99.1));
    assertEquals(33, lTools.autoBox(Float.valueOf(33), -99));

    // Test auto boxing of wrapper arrays to primitives
    Float[] lWrapperArray =
        new Float[] { Float.valueOf((float) 12.0), Float.valueOf((float) -1.9), Float.valueOf((float) -22.0) };
    float[] lExpectedPrimitiveArray = new float[] { (float) 12.0, (float) -1.9, (float) -22.0 };
    assertTrue(Arrays.equals(lExpectedPrimitiveArray, lTools.autoBox(lWrapperArray)));
    assertNull(lTools.autoBox((Float[]) null));

    // Test auto boxing of primitive arrays to wrappers
    float[] lPrimitiveArray = new float[] { -7, 22, 33, 44, 11 };
    Float[] lExpectedWrapperArray = new Float[] { Float.valueOf(-7), Float.valueOf(22),
      Float.valueOf(33), Float.valueOf(44), Float.valueOf(11) };
    assertTrue(Arrays.equals(lExpectedWrapperArray, lTools.autoBox(lPrimitiveArray)));
    assertNull(lTools.autoBox((float[]) null));
  }

  @Test
  @Order(70)
  public void testDoubleAutoBoxing( ) {
    // Test handling of primitives.
    AutoBoxingTools lTools = Tools.getAutoBoxingTools();
    assertNotNull(lTools, "AutoBoxingTools not available.");

    // Test auto boxing from primitive to wrapper
    assertEquals(Double.valueOf(33333.3333), lTools.autoBox(33333.3333));
    assertNotEquals(Double.valueOf(33), lTools.autoBox(4.2));

    // Test auto boxing from wrapper to primitive
    assertEquals(1.11, lTools.autoBox(Double.valueOf(1.11)));
    assertEquals(47.0, lTools.autoBox(Double.valueOf(47.0)));

    try {
      lTools.autoBox((Double) null);
      fail("Expection expected when 'null' is passed as wrapper type.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    // Test auto boxing from wrapper to primitive (handling of null values)
    assertEquals(6.97, lTools.autoBox((Double) null, 6.97));
    assertEquals(-99.1, lTools.autoBox((Double) null, -99.1));
    assertEquals(33, lTools.autoBox(Double.valueOf(33), -99));

    // Test auto boxing of wrapper arrays to primitives
    Double[] lWrapperArray =
        new Double[] { Double.valueOf(12.0), Double.valueOf(-1.9), Double.valueOf(-22.0) };
    double[] lExpectedPrimitiveArray = new double[] { 12.0, -1.9, -22.0 };
    assertTrue(Arrays.equals(lExpectedPrimitiveArray, lTools.autoBox(lWrapperArray)));
    assertNull(lTools.autoBox((Double[]) null));

    // Test auto boxing of primitive arrays to wrappers
    double[] lPrimitiveArray = new double[] { -7, 22, 33, 44, 11 };
    Double[] lExpectedWrapperArray = new Double[] { Double.valueOf(-7), Double.valueOf(22),
      Double.valueOf(33), Double.valueOf(44), Double.valueOf(11) };
    assertTrue(Arrays.equals(lExpectedWrapperArray, lTools.autoBox(lPrimitiveArray)));
    assertNull(lTools.autoBox((double[]) null));
  }

  @Test
  @Order(80)
  public void testCharacterAutoBoxing( ) {
    // Test handling of primitives.
    AutoBoxingTools lTools = Tools.getAutoBoxingTools();
    assertNotNull(lTools, "AutoBoxingTools not available.");

    // Test auto boxing from primitive to wrapper
    assertEquals(Character.valueOf('a'), lTools.autoBox('a'));
    assertNotEquals(Character.valueOf('x'), lTools.autoBox('y'));

    // Test auto boxing from wrapper to primitive
    assertEquals('b', lTools.autoBox(Character.valueOf('b')));
    assertEquals('!', lTools.autoBox(Character.valueOf('!')));

    try {
      lTools.autoBox((Character) null);
      fail("Expection expected when 'null' is passed as wrapper type.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    // Test auto boxing from wrapper to primitive (handling of null values)
    assertEquals('6', lTools.autoBox((Character) null, Character.valueOf('6')));
    assertEquals((char) 9, lTools.autoBox((Character) null, Character.valueOf((char) 9)));
    assertEquals('v', lTools.autoBox(Character.valueOf('v'), 'z'));

    // Test auto boxing of wrapper arrays to primitives
    Character[] lWrapperArray =
        new Character[] { Character.valueOf('A'), Character.valueOf('B'), Character.valueOf('C') };
    char[] lExpectedPrimitiveArray = new char[] { 'A', 'B', 'C' };
    assertTrue(Arrays.equals(lExpectedPrimitiveArray, lTools.autoBox(lWrapperArray)));
    assertNull(lTools.autoBox((Character[]) null));

    // Test auto boxing of primitive arrays to wrappers
    char[] lPrimitiveArray = new char[] { 'a', 'x', 'd', 'c', 'y' };
    Character[] lExpectedWrapperArray = new Character[] { Character.valueOf('a'), Character.valueOf('x'),
      Character.valueOf('d'), Character.valueOf('c'), Character.valueOf('y') };
    assertTrue(Arrays.equals(lExpectedWrapperArray, lTools.autoBox(lPrimitiveArray)));
    assertNull(lTools.autoBox((char[]) null));
  }

}
