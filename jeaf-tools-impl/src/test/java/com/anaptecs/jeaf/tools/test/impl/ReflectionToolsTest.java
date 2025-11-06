/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.junit.jupiter.api.Test;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.reflect.ReflectionTools;
import com.anaptecs.jeaf.tools.test.impl.reflect.AbstractClass;
import com.anaptecs.jeaf.tools.test.impl.reflect.ClassWithProtectedConstructor;
import com.anaptecs.jeaf.tools.test.impl.reflect.TestAnnotation;
import com.anaptecs.jeaf.tools.test.impl.reflect.TestClass1;
import com.anaptecs.jeaf.tools.test.impl.reflect.TestClass2;
import com.anaptecs.jeaf.tools.test.impl.reflect.TestInterface1;
import com.anaptecs.jeaf.xfun.api.XFunMessages;
import com.anaptecs.jeaf.xfun.api.errorhandling.ErrorCode;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import com.anaptecs.jeaf.xfun.api.errorhandling.SystemException;

import junit.framework.TestCase;

public class ReflectionToolsTest {

  /**
   * Method tests method Tools.newInstance(...).
   */
  @Test
  public void testNewInstance( ) {
    // Test valid variations.
    ReflectionTools lReflectionTools = Tools.getReflectionTools();
    TestCase.assertNotNull(lReflectionTools.newInstance(String.class));
    TestCase.assertNotNull(lReflectionTools.newInstance(Vector.class));

    // Test invalid variations.
    ErrorCode lErrorCode = ToolsMessages.UNABLE_TO_CREATE_NEW_INSTANCE;

    // Class with no public empty constructor.
    try {
      lReflectionTools.newInstance(List.class);
      TestCase.fail("Expected SystemException to be thrown.");
    }
    catch (SystemException e) {
      TestCase.assertEquals("Exception has wrong ErrorCode", lErrorCode, e.getErrorCode());
    }
    // Class object of primitive type.
    try {
      lReflectionTools.newInstance(Boolean.TYPE);
      TestCase.fail("Expected SystemException to be thrown.");
    }
    catch (SystemException e) {
      TestCase.assertEquals("Exception has wrong ErrorCode", lErrorCode, e.getErrorCode());
    }
    // Passed class object is null.
    try {
      lReflectionTools.newInstance(null);
      TestCase.fail("Expected InvalidParameterException to be thrown.");
    }
    catch (IllegalArgumentException e) {
      // No special checks needed.
    }

    // Create instance of abstract class
    try {
      lReflectionTools.newInstance(AbstractClass.class.getName(), AbstractClass.class);
      TestCase.fail("Expected Exception to be thrown.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_CREATE_NEW_INSTANCE, e.getErrorCode());
      assertEquals(InstantiationException.class, e.getCause().getClass());
    }

    // Test check for assignablity
    ArrayList<?> lNewInstance = lReflectionTools.newInstance(ArrayList.class, List.class);
    assertNotNull(lNewInstance);
    lNewInstance = (ArrayList<?>) lReflectionTools.newInstance(ArrayList.class.getName(), List.class);
    assertNotNull(lNewInstance);

    try {
      lReflectionTools.newInstance(ArrayList.class, Map.class);
      TestCase.fail("Expected Exception to be thrown.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.CLASS_NOT_ASSIGNABLE, e.getErrorCode());
    }

    // Test make accessible feature
    try {
      lReflectionTools.newInstance(ClassWithProtectedConstructor.class, false);
      fail("Exception expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_CREATE_NEW_INSTANCE, e.getErrorCode());
    }

    ClassWithProtectedConstructor lNewProtectedInstance =
        lReflectionTools.newInstance(ClassWithProtectedConstructor.class, true);
    assertNotNull(lNewProtectedInstance);

  }

  @Test
  public void testClassLoading( ) {
    ReflectionTools lReflectionTools = Tools.getReflectionTools();

    Class<?> lLoadedClass = lReflectionTools.loadClass(String.class.getName());
    assertEquals(String.class, lLoadedClass);

    try {
      lReflectionTools.loadClass("I.m.not.available");
    }
    catch (JEAFSystemException e) {
      assertEquals(XFunMessages.CLASS_NOT_LOADABLE, e.getErrorCode());
    }

    try {
      lReflectionTools.loadClass(null);
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    @SuppressWarnings("rawtypes")
    Class<? extends List> lListClass = lReflectionTools.loadClass(ArrayList.class.getName(), List.class);
    assertEquals(ArrayList.class, lListClass);

    assertTrue(lReflectionTools.isClassLoadable(String.class.getName()));
    assertFalse(lReflectionTools.isClassLoadable("I.m.not.available"));

    try {
      lReflectionTools.isClassLoadable(null);
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

  }

  @Test
  public void testGetAnnotations( ) {
    ReflectionTools lReflectionTools = Tools.getReflectionTools();

    // Test good cases
    List<Class<?>> lClasses = new ArrayList<>();
    lClasses.add(TestClass2.class);
    lClasses.add(TestClass2.class);
    lClasses.add(String.class);
    lClasses.add(TestInterface1.class);
    lClasses.add(TestClass1.class);
    Map<Class<?>, com.anaptecs.jeaf.tools.test.impl.reflect.TestAnnotation> lMap =
        lReflectionTools.getAnnotations(lClasses, TestAnnotation.class);
    TestCase.assertEquals(3, lMap.size());
    TestCase.assertTrue("Class 'TestClass2' missing.", lMap.containsKey(TestClass2.class));
    TestCase.assertTrue("Class 'TestClass1' missing.", lMap.containsKey(TestClass1.class));
    TestCase.assertTrue("Class 'TestInterface1' missing.", lMap.containsKey(TestInterface1.class));
    TestCase.assertFalse("Class 'TestInterface1' missing.", lMap.containsKey(String.class));

    // Test error handling.
    try {
      lReflectionTools.getAnnotations(lClasses, null);
    }
    catch (IllegalArgumentException e) {
    }
  }
}
