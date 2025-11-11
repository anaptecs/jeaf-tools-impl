/**
 * Copyright 2004 - 2018 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Configuration;
import javax.validation.Constraint;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.MessageInterpolator;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import javax.validation.groups.Default;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.MethodDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.validation.ValidationResult;
import com.anaptecs.jeaf.tools.api.validation.ValidationTools;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.checks.Check;
import com.anaptecs.jeaf.xfun.api.messages.MessageRepository;
import com.anaptecs.jeaf.xfun.api.trace.Trace;

/**
 * Class implements JEAF's Validation Tools.
 * 
 * @author JEAF Development Team
 * @version JEAF Release 1.3
 */
@ToolsImplementation(toolsInterface = ValidationTools.class)
public class ValidationToolsImpl implements ValidationTools {
  /**
   * Validator that is used for all validations.
   */
  private final Validator validator;

  /**
   * Constructor.
   */
  public ValidationToolsImpl( ) {
    // Create new validator and also register JEAF's special message interpolator.
    try {
      Configuration<?> lConfiguration = Validation.byDefaultProvider().configure();
      MessageInterpolator lDefaultMessageInterpolator = lConfiguration.getDefaultMessageInterpolator();
      lConfiguration.messageInterpolator(new JEAFMessageInterpolator(lDefaultMessageInterpolator));
      ValidatorFactory lValidatorFactory = lConfiguration.buildValidatorFactory();
      validator = lValidatorFactory.getValidator();
    }
    // A runtime exception might occur in case that no bean validation library is in the classpath.
    catch (RuntimeException e) {
      Trace lTrace = XFun.getTrace();
      lTrace.error(
          "Unable to initialize JEAF Validation-Tools. Most likely this is a classpath issue. Please ensure that you have all required libraries of your validation library inside your classpath.");
      lTrace.error(e.getMessage());
      throw e;
    }
  }

  /**
   * Method checks if the passed method requires validation of its request parameters.
   * 
   * @param pMethod Method that should be checked. The parameter must not be null.
   * @return boolean Method returns true if a validation for the parameters of the method is required and false
   * otherwise.
   */
  @Override
  public boolean requiresRequestValidation( Method pMethod ) {
    // Check parameter
    Check.checkInvalidParameterNull(pMethod, "pMethod");

    // Resolve constraints for the passed method. If no constraints are defined then null will be returned.
    MethodDescriptor lMethodConstraints = this.getMethodConstraints(pMethod);

    // Method defines validation constraints.
    boolean lRequiresRequestValidation;
    if (lMethodConstraints != null) {
      lRequiresRequestValidation = lMethodConstraints.hasConstrainedParameters();
    }
    // Method does not have any validation constraints.
    else {
      lRequiresRequestValidation = false;
    }

    // Return result.
    return lRequiresRequestValidation;
  }

  /**
   * Method checks if the passed method requires validation of its response.
   * 
   * @param pMethod Method that should be checked. The parameter must not be null.
   * @return boolean Method returns true if a validation for the response of the method is required and false otherwise.
   */
  @Override
  public boolean requiresResponseValidation( Method pMethod ) {
    // Check parameter
    Check.checkInvalidParameterNull(pMethod, "pMethod");

    // Resolve constraints for the passed method. If no constraints are defined then null will be returned.
    MethodDescriptor lMethodConstraints = this.getMethodConstraints(pMethod);

    // Method defines validation constraints.
    boolean lRequiresResponseValidation;
    if (lMethodConstraints != null) {
      lRequiresResponseValidation = lMethodConstraints.hasConstrainedReturnValue();
    }
    // Method does not have any validation constraints.
    else {
      lRequiresResponseValidation = false;
    }

    // Return result.
    return lRequiresResponseValidation;
  }

  /**
   * Method checks if the passed field requires validation of its response.
   * 
   * @param pField Field that should be checked. The parameter must not be null.
   * @return boolean Method returns true if a validation for the field is required and false otherwise.
   */
  @Override
  public boolean requiresValidation( Field pField ) {
    // Check parameter
    Check.checkInvalidParameterNull(pField, "pField");

    // Resolve constraints for the passed field. If no constraints are defined then null will be returned.
    BeanDescriptor lConstraints = validator.getConstraintsForClass(pField.getDeclaringClass());
    PropertyDescriptor lFieldConstraints = lConstraints.getConstraintsForProperty(pField.getName());

    // Field defines some kind of constraints. Either it has its own constraints or it defines @Valid
    boolean lRequiresValidation;
    if (lFieldConstraints != null) {
      lRequiresValidation = true;
    }
    // Field does not require validation.
    else {
      lRequiresValidation = false;
    }

    return lRequiresValidation;
  }

  /**
   * Method checks if the passed annotation is a validation annotation.
   * 
   * @param pAnnotation Validation that should be checked. The parameter must not be null.
   * @return boolean true if the annotation is a validation annotation and false otherwise.
   */
  @Override
  public boolean isValidationAnnotation( Annotation pAnnotation ) {
    // Check parameter

    // In order to detect if a annotation is a validation annotation the annotation itself has to have an annotation of
    // type "javax.validation.Constraint" or it must be of type javax.validation.Valid.

    // @Valid
    boolean lIsValidationAnnotation = false;
    if (pAnnotation instanceof Valid) {
      lIsValidationAnnotation = true;
    }
    // May be its any other validation annotation.
    else {
      // Therefore, we first have to resolve the annotations of the passed annotation.
      Class<? extends Annotation> lAnnotationType = pAnnotation.annotationType();
      Annotation[] lAnnotations = lAnnotationType.getAnnotations();

      // Afterwards we have to analyze all annotations of the passed annotation.
      for (Annotation lNext : lAnnotations) {
        Class<? extends Annotation> lType = lNext.annotationType();

        // If the current annotation is of type "Constraint" then we found an validation annotation.
        boolean lIsConstraintAnnontation = Constraint.class.isAssignableFrom(lType);
        if (lIsConstraintAnnontation == true) {
          lIsValidationAnnotation = true;
          break;
        }
      }
    }

    // Return result of check.
    return lIsValidationAnnotation;
  }

  /**
   * Method checks if the passed list contains at least one validation annotations.
   * 
   * @param pAnnotations List of annotations that should be checked. The parameter must not be null.
   * @return boolean true if at least one annotation inside the list is a validation annotation.
   */
  @Override
  public boolean containsValidationAnnotation( List<Annotation> pAnnotations ) {
    // Check parameter
    Check.checkInvalidParameterNull(pAnnotations, "pAnnotations");

    // Check all annotations
    boolean lContainsValidation = false;
    for (Annotation lNextAnnotation : pAnnotations) {
      boolean lValidationAnnotation = this.isValidationAnnotation(lNextAnnotation);
      if (lValidationAnnotation == true) {
        lContainsValidation = true;
        break;
      }
    }

    // Return result of check.
    return lContainsValidation;
  }

  /**
   * Method checks if the passed array contains at least one validation annotations.
   * 
   * @param pAnnotations Array of annotations that should be checked. The parameter must not be null.
   * @return boolean true if at least one annotation inside the array is a validation annotation.
   */
  @Override
  public boolean containsValidationAnnotation( Annotation[] pAnnotations ) {
    // Check parameter
    Check.checkInvalidParameterNull(pAnnotations, "pAnnotations");

    // Check all annotations
    boolean lContainsValidation = false;
    for (Annotation lNextAnnotation : pAnnotations) {
      boolean lValidationAnnotation = this.isValidationAnnotation(lNextAnnotation);
      if (lValidationAnnotation == true) {
        lContainsValidation = true;
        break;
      }
    }

    // Return result of check.
    return lContainsValidation;
  }

  /**
   * Method checks the constraints on the passed object.
   * 
   * @param pObject Object whose constraints should be checked. The parameter must not be null.
   * @return {@link ValidationResult} Object representing the result of the constraint validation. The method never
   * returns null.
   */
  @Override
  public <T> ValidationResult<T> validate( T pObject ) {
    Set<ConstraintViolation<T>> lConstraintViolations = this.validateObject(pObject);
    return new ValidationResult<>(pObject, lConstraintViolations);
  }

  /**
   * Method checks the constraints on the passed object.
   * 
   * @param pObject Object whose constraints should be checked. The parameter must not be null.
   * @return {@link Set} Set containing all constraint violations. The method never returns null.
   */
  @Override
  public <T> Set<ConstraintViolation<T>> validateObject( T pObject ) {
    // Check parameter
    Check.checkInvalidParameterNull(pObject, "pObject");

    // Execute validation and return result.
    return validator.validate(pObject);
  }

  /**
   * Method enforces that the passed object is valid. In case that the object is not valid a
   * ConstraintViolationException will be thrown.
   * 
   * @param pObject Object that should be validated. The parameter must not be null
   * @throws ConstraintViolationException Exception describing which violations failed.
   */
  @Override
  public void enforceObjectValidation( Object pObject ) throws ConstraintViolationException {
    // Run parameter validation.
    Set<ConstraintViolation<Object>> lViolations = this.validateObject(pObject);

    // Check if constraint check failed.
    if (lViolations.isEmpty() == false) {
      // As the constraint check fails we have to throw an exception.
      throw new ConstraintViolationException(ToolsMessages.OBJECT_VALIDATION_FAILED.toString(), lViolations);
    }
  }

  /**
   * Method checks the constraints of all objects that are inside the passed collection.
   * 
   * @param pObjects Collection with all objects whose constraints should be checked. The parameter may be null.
   * @return {@link Set} Set with all constraint violations that were found for the passed objects. The method never
   * returns null.
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T> Set<ConstraintViolation<T>> validateObjects( Collection<T> pObjects ) {
    // Validate all passed objects and return result.
    T[] lArray;
    Set<ConstraintViolation<T>> lViolations;
    if (pObjects != null) {
      lArray = (T[]) pObjects.toArray();
      lViolations = this.validateObjects(lArray);
    }
    else {
      lViolations = Collections.emptySet();
    }
    return lViolations;
  }

  /**
   * Method checks the constraints of all objects that are inside the passed array.
   * 
   * @param pObjects Array with all objects whose constraints should be checked. The parameter may be null.
   * @return {@link Set} Set with all constraint violations that were found for the passed objects. The method never
   * returns null.
   */
  @Override
  public <T> Set<ConstraintViolation<T>> validateObjects( T[] pObjects ) {
    // Check for null.
    Set<ConstraintViolation<T>> lViolations;

    // Real array was passed
    if (pObjects != null) {
      // Check constraints on all passed objects.
      lViolations = new HashSet<>();
      for (T pObjectToValidate : pObjects) {
        // Validate next object
        Set<ConstraintViolation<T>> lNextViolations = this.validateObject(pObjectToValidate);

        // If there were any violations then we collect them in the set for all objects.
        if (lNextViolations.isEmpty() == false) {
          lViolations.addAll(lNextViolations);
        }
      }
    }
    // Null was passed as parameter, so we return an empty set.
    else {
      lViolations = Collections.emptySet();
    }

    // Return result of constraint check
    return lViolations;
  }

  /**
   * Validates all constraints placed on the parameters of the given method.
   *
   * @param <T> the type hosting the method to validate
   * @param pObject the object on which the method to validate is invoked
   * @param pMethod the method for which the parameter constraints is validated
   * @param pParameterValues the values provided by the caller for the given method's parameters
   * @param pGroups the group or list of groups targeted for validation (defaults to {@link Default})
   * @return a set with the constraint violations caused by this validation; will be empty if no error occurs, but never
   * {@code null}
   * @throws IllegalArgumentException if {@code null} is passed for any of the parameters or if parameters don't match
   * with each other
   * @throws ValidationException if a non recoverable error happens during the validation process
   * 
   * @see ExecutableValidator#validateConstructorParameters(java.lang.reflect.Constructor, Object[], Class...)
   */
  @Override
  public <T> Set<ConstraintViolation<T>> validateParameters( T pObject, Method pMethod, Object[] pParameterValues,
      Class<?>... pGroups ) {

    // Check parameters
    Check.checkInvalidParameterNull(pObject, "pObject");
    Check.checkInvalidParameterNull(pMethod, "pMethod");
    Check.checkInvalidParameterNull(pParameterValues, "pParameterValues");

    // Run validation and return result.
    return validator.forExecutables().validateParameters(pObject, pMethod, pParameterValues, pGroups);
  }

  /**
   * Method validates the passed parameters according to the defined bean validation. If at least one validation fails
   * then a ConstraintViolationException will be thrown.
   * 
   * @param <T> the type hosting the method to validate
   * @param pObject the object on which the method to validate is invoked
   * @param pMethod the method for which the parameter constraints is validated
   * @param pParameterValues the values provided by the caller for the given method's parameters
   * @param pGroups the group or list of groups targeted for validation (defaults to {@link Default})
   * @return a set with the constraint violations caused by this validation; will be empty if no error occurs, but never
   * {@code null}
   * 
   * @see ExecutableValidator#validateReturnValue(Object, Method, Object, Class...)
   */
  @Override
  public <T> void enforceParameterValidation( T pObject, Method pMethod, Object[] pParameterValues,
      Class<?>... pGroups )
    throws ConstraintViolationException {

    // Run parameter validation.
    Set<ConstraintViolation<T>> lViolations = this.validateParameters(pObject, pMethod, pParameterValues, pGroups);

    // Check if constraint check failed.
    if (lViolations.isEmpty() == false) {
      // As the constraint check fails we have to throw an exception.
      MessageRepository lRepo = XFun.getMessageRepository();
      String lMessage = lRepo.getMessage(ToolsMessages.REQUEST_VALIDATION_FAILED, Integer.toString(lViolations.size()));
      throw new ConstraintViolationException(lMessage, lViolations);
    }
  }

  /**
   * Validates all return value constraints of the given method.
   *
   * @param <T> the type hosting the method to validate
   * @param pObject the object on which the method to validate is invoked
   * @param pMethod the method for which the return value constraints is validated
   * @param pReturnValue the value returned by the given method
   * @param pGroups the group or list of groups targeted for validation (defaults to {@link Default})
   * @return a set with the constraint violations caused by this validation; will be empty if no error occurs, but never
   * {@code null}
   * @throws IllegalArgumentException if {@code null} is passed for any of the object, method or groups parameters or if
   * parameters don't match with each other
   * @throws ValidationException if a non recoverable error happens during the validation process
   */
  @Override
  public <T> Set<ConstraintViolation<T>> validateReturnValue( T pObject, Method pMethod, Object pReturnValue,
      Class<?>... pGroups ) {

    // Check parameters
    Check.checkInvalidParameterNull(pObject, "pObject");
    Check.checkInvalidParameterNull(pMethod, "pMethod");

    // Run validation and return result.
    return validator.forExecutables().validateReturnValue(pObject, pMethod, pReturnValue, pGroups);
  }

  /**
   * Method validates the passed return value according to the defined bean validation. If at least one validation fails
   * then a ConstraintViolationException will be thrown.
   * 
   * @param <T>
   * @param pObject
   * @param pMethod
   * @param pReturnValue
   * @param pGroups
   */
  @Override
  public <T> void enforceReturnValueValidation( T pObject, Method pMethod, Object pReturnValue, Class<?>... pGroups ) {
    // Run return value validation
    Set<ConstraintViolation<T>> lViolations = this.validateReturnValue(pObject, pMethod, pReturnValue, pGroups);

    // Check if constraint check failed.
    if (lViolations.isEmpty() == false) {
      // As the constraint check fails we have to throw an exception.
      MessageRepository lRepo = XFun.getMessageRepository();
      String lMessage =
          lRepo.getMessage(ToolsMessages.RESPONSE_VALIDATION_FAILED, Integer.toString(lViolations.size()));
      throw new ConstraintViolationException(lMessage, lViolations);
    }
  }

  /**
   * Method returns the available Java Bean validator implementation.
   * 
   * @return {@link Validator} Validator implementation that can be used for validations. The method never returns null.
   */
  @Override
  public Validator getValidator( ) {
    return validator;
  }

  /**
   * Method resolves the constraints for the passed method.
   * 
   * @param pMethod Method whose constraints should be resolved. The parameter must not be null.
   * @return {@link MethodDescriptor} Object with meta information about validation of this method. The method may
   * return null if no validation rules are defined at all.
   */
  private MethodDescriptor getMethodConstraints( Method pMethod ) {
    BeanDescriptor lConstraints = validator.getConstraintsForClass(pMethod.getDeclaringClass());
    return lConstraints.getConstraintsForMethod(pMethod.getName(), pMethod.getParameterTypes());
  }
}
