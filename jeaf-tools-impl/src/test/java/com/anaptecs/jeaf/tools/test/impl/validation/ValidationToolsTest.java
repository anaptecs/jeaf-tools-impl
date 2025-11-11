/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.MessageInterpolator;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.validation.ValidationResult;
import com.anaptecs.jeaf.tools.api.validation.ValidationTools;
import com.anaptecs.jeaf.tools.impl.validation.JEAFMessageInterpolator;
import com.anaptecs.jeaf.tools.test.impl.validation.ValidationTestObject.Builder;
import org.junit.jupiter.api.Test;

public class ValidationToolsTest {
  public static final String TEST_VALIDATION_MESSAGE_CODE =
      Integer.toString(ValidationTestMessages.TEST_VALIDATION_MESSAGE.getErrorCodeValue());

  static Map<String, Method> methodsMap = new HashMap<>();
  static {

    Method[] lMethods = ValidationTestService.class.getMethods();
    for (Method lNext : lMethods) {
      methodsMap.put(lNext.getName(), lNext);
    }
  }

  @DecimalMax(value = "", message = "199920")
  @DecimalMin(value = "")
  @Digits(fraction = 0, integer = 0)
  @Email
  @Future
  @FutureOrPresent
  @Max(value = 0)
  @Min(value = 0)
  @Negative
  @NegativeOrZero
  @NotBlank
  @NotEmpty
  @NotNull
  @Null
  @Past
  @PastOrPresent
  @Pattern(regexp = "")
  @Positive
  @PositiveOrZero
  @Size
  @Valid
  private boolean fieldWithValidationAnnotations;

  @Deprecated
  private boolean filedWithoutValidationAnnotations;

  @Test
  public void testReflectionAnalysisMechanism( ) throws ReflectiveOperationException, SecurityException {

    ValidationTools lValidationTools = Tools.getValidationTools();
    assertNotNull(lValidationTools);
    Method lMethod = methodsMap.get("validateInNOutput");
    assertTrue(lValidationTools.requiresRequestValidation(lMethod));
    assertTrue(lValidationTools.requiresResponseValidation(lMethod));

    lMethod = methodsMap.get("noMethodValidation");
    assertFalse(lValidationTools.requiresRequestValidation(lMethod));
    assertFalse(lValidationTools.requiresResponseValidation(lMethod));

    Field lField = ValidationTestObject.class.getDeclaredField("myEMail");
    assertTrue(lValidationTools.requiresValidation(lField));
    lField = ValidationTestObject.class.getDeclaredField("testClass");
    assertTrue(lValidationTools.requiresValidation(lField));
    lField = ValidationTestObject.class.getDeclaredField("class2");
    assertTrue(lValidationTools.requiresValidation(lField));

    lField = ValidationTestObject.class.getDeclaredField("notValidated");
    assertFalse(lValidationTools.requiresValidation(lField));

    // Test exception handling
    try {
      lValidationTools.requiresRequestValidation(null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lValidationTools.requiresResponseValidation(null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lValidationTools.requiresValidation(null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    // Test detection of validation annotations.
    lField = ValidationToolsTest.class.getDeclaredField("fieldWithValidationAnnotations");
    assertEquals(21, lField.getAnnotations().length);
    Annotation[] lValidationAnnotations = lField.getAnnotations();
    for (Annotation lNext : lValidationAnnotations) {
      assertTrue(lValidationTools.isValidationAnnotation(lNext),
          lNext.annotationType().getName() + " is not a validation annotation.");
    }

    lField = ValidationToolsTest.class.getDeclaredField("filedWithoutValidationAnnotations");
    assertEquals(1, lField.getAnnotations().length);
    Annotation[] lOtherAnnotations = lField.getAnnotations();
    for (Annotation lNext : lOtherAnnotations) {
      assertFalse(lValidationTools.isValidationAnnotation(lNext),
          lNext.annotationType().getName() + " is a validation annotation.");
    }

    assertFalse(lValidationTools.containsValidationAnnotation(lOtherAnnotations));
    assertTrue(lValidationTools.containsValidationAnnotation(lValidationAnnotations));
    List<Annotation> lMixedAnnotations = new ArrayList<>();
    lMixedAnnotations.addAll(Arrays.asList(lOtherAnnotations));
    lMixedAnnotations.add(lValidationAnnotations[3]);
    assertTrue(lValidationTools.containsValidationAnnotation(lMixedAnnotations));
    Annotation[] lArray = lMixedAnnotations.toArray(new Annotation[] {});
    assertTrue(lValidationTools.containsValidationAnnotation(lArray));

    // Test handling of empty list / array
    assertFalse(lValidationTools.containsValidationAnnotation(new Annotation[] {}));
    assertFalse(lValidationTools.containsValidationAnnotation(new ArrayList<>()));

    // Test exception handling.
    try {
      lValidationTools.containsValidationAnnotation((Annotation[]) null);
      fail();
    }
    catch (IllegalArgumentException e) {
      assertEquals("'pAnnotations' must not be null.", e.getMessage());
    }
    try {
      lValidationTools.containsValidationAnnotation((List<Annotation>) null);
      fail();
    }
    catch (IllegalArgumentException e) {
      assertEquals("'pAnnotations' must not be null.", e.getMessage());
    }
  }

  @Test
  public void testObjectValidation( ) {
    ValidationTools lValidationTools = Tools.getValidationTools();
    Validator lValidator = lValidationTools.getValidator();
    assertNotNull(lValidator);

    Builder lBuilder = ValidationTestObject.Builder.newBuilder();
    lBuilder.setMyEMail("jeaf@anaptecs.de");
    ValidationTestObject lObject = lBuilder.build();
    TestClass lValidTestClass = new TestClass();
    lValidTestClass.setString("Hello");
    lObject.setClass2(lValidTestClass);
    Set<ConstraintViolation<ValidationTestObject>> lViolations = lValidationTools.validateObject(lObject);
    assertEquals(0, lViolations.size());

    List<ValidationTestObject> lObjects = new ArrayList<>();
    lObjects.add(lObject);
    lViolations = lValidationTools.validateObjects(lObjects);
    assertEquals(0, lViolations.size());

    ValidationResult<TestClass> lResult1 = lValidationTools.validate(lValidTestClass);
    assertEquals(lValidTestClass, lResult1.getValidatedObject());
    assertEquals(false, lResult1.hasConstrainViolations());
    assertEquals(0, lResult1.getConstraintViolations().size());

    lValidationTools.enforceObjectValidation(lValidTestClass);

    lObject.setMyEMail(null);
    lViolations = lValidationTools.validateObject(lObject);
    assertEquals(1, lViolations.size());
    ConstraintViolation<ValidationTestObject> lConstraintViolation = lViolations.iterator().next();
    assertEquals("myEMail", lConstraintViolation.getPropertyPath().toString());

    ValidationResult<ValidationTestObject> lResult2 = lValidationTools.validate(lObject);
    assertEquals(lObject, lResult2.getValidatedObject());
    assertEquals(true, lResult2.hasConstrainViolations());
    assertEquals(1, lResult2.getConstraintViolations().size());
    lConstraintViolation = lResult2.getConstraintViolations().get(0);
    assertEquals("myEMail", lConstraintViolation.getPropertyPath().toString());

    try {
      lValidationTools.enforceObjectValidation(lObject);
    }
    catch (ConstraintViolationException e) {
      assertEquals("1055", e.getMessage());
      assertEquals("myEMail", e.getConstraintViolations().iterator().next().getPropertyPath().toString());
    }

    lViolations = lValidationTools.validateObjects(lObjects);
    assertEquals(1, lViolations.size());

    // Check exception handling
    try {
      lValidationTools.validateObject(null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    lViolations = lValidationTools.validateObjects((Collection<ValidationTestObject>) null);
    assertEquals(0, lViolations.size());
    lViolations = lValidationTools.validateObjects((ValidationTestObject[]) null);
    assertEquals(0, lViolations.size());
  }

  @Test
  public void testRequestValidation( ) {
    ValidationTools lValidationTools = Tools.getValidationTools();

    // Test parameter validation.
    Method lMethod = methodsMap.get("noMethodValidation");
    Set<ConstraintViolation<ValidationTestServiceImpl>> lViolations = lValidationTools
        .validateParameters(new ValidationTestServiceImpl(), lMethod, new Object[] { "Hello", Boolean.FALSE });
    assertEquals(0, lViolations.size());

    lValidationTools.enforceParameterValidation(new ValidationTestServiceImpl(), lMethod,
        new Object[] { "Hello", Boolean.FALSE });

    lMethod = methodsMap.get("validateInNOutput");
    lViolations =
        lValidationTools.validateParameters(new ValidationTestServiceImpl(), lMethod, new Object[] { null, "" });
    assertEquals(2, lViolations.size());

    try {
      lValidationTools.enforceParameterValidation(new ValidationTestServiceImpl(), lMethod, new Object[] { null, "" });
      fail("Exception expected.");
    }
    catch (ConstraintViolationException e) {
      assertEquals(2, e.getConstraintViolations().size());
    }

    // Test return value validation
    lMethod = methodsMap.get("validateInputOnly");
    lViolations = lValidationTools.validateReturnValue(new ValidationTestServiceImpl(), lMethod, null);
    assertEquals(0, lViolations.size());

    lValidationTools.enforceReturnValueValidation(new ValidationTestServiceImpl(), lMethod, null);

    lMethod = methodsMap.get("validateOutputOnly");
    lViolations = lValidationTools.validateReturnValue(new ValidationTestServiceImpl(), lMethod, null);
    assertEquals(1, lViolations.size());
    assertEquals("199920", lViolations.iterator().next().getMessage());

    try {
      lValidationTools.enforceReturnValueValidation(new ValidationTestServiceImpl(), lMethod, null);
      fail("Exception expected.");
    }
    catch (ConstraintViolationException e) {
      assertEquals(1, e.getConstraintViolations().size());
    }

    // Test exception handling
    try {
      lValidationTools.validateParameters(null, lMethod, new Object[] {});
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lValidationTools.validateParameters(new ValidationTestServiceImpl(), null, new Object[] {});
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lValidationTools.validateParameters(new ValidationTestServiceImpl(), lMethod, null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    try {
      lValidationTools.enforceParameterValidation(null, lMethod, new Object[] {});
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lValidationTools.enforceParameterValidation(new ValidationTestServiceImpl(), null, new Object[] {});
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lValidationTools.enforceParameterValidation(new ValidationTestServiceImpl(), lMethod, null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    try {
      lValidationTools.validateReturnValue(null, lMethod, null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lValidationTools.validateReturnValue(new ValidationTestServiceImpl(), null, null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lValidationTools.enforceReturnValueValidation(null, lMethod, null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lValidationTools.enforceReturnValueValidation(new ValidationTestServiceImpl(), null, null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

  }

  @Test
  public void testMessageInterpolator( ) {
    Configuration<?> lConfiguration = Validation.byDefaultProvider().configure();
    MessageInterpolator lDefaultMessageInterpolator = lConfiguration.getDefaultMessageInterpolator();
    JEAFMessageInterpolator lInterpolator = new JEAFMessageInterpolator(lDefaultMessageInterpolator);
    String lMessage = lInterpolator.interpolate("199920", null);
    assertEquals("199920", lMessage);

    lMessage = lInterpolator.interpolate("99999", null);
    assertEquals("99999", lMessage);

    lMessage = lInterpolator.interpolate("Hello12345", null, null);
    assertEquals("Hello12345", lMessage);

    lMessage = lInterpolator.interpolate("1230000045", null, null);
    assertEquals("1230000045", lMessage);

  }

}

class ValidationTestServiceImpl implements ValidationTestService {

  @Override
  public String validateInNOutput(String pString1, String pString2) {
    return null;
  }

  @Override
  public String notValidOutput(String pString1, String pString2) {
    return null;
  }

  @Override
  public String validateOutputOnly(String pString1, String pString2) {
    return null;
  }

  @Override
  public String validateInputOnly(String pString1, int pInteger) {
    return null;
  }

  @Override
  public String noMethodValidation(String pString1, boolean pBool) {
    return null;
  }

  @Override
  public void createValidationTestObject(ValidationTestObject pTestObject) {
  }

}
