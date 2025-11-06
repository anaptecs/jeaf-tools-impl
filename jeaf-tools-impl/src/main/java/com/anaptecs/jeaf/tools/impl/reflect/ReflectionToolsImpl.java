/*
 * anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * Copyright 2004 - 2013 All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.reflect.ReflectionTools;
import com.anaptecs.jeaf.xfun.api.XFunMessages;
import com.anaptecs.jeaf.xfun.api.checks.Check;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;

/**
 * Class provides useful helper methods.
 * 
 * @author JEAF Development Team
 * @version JEAF Release 1.2
 */
@ToolsImplementation(toolsInterface = ReflectionTools.class)
public final class ReflectionToolsImpl implements ReflectionTools {
  /**
   * Constructor of this class is private in order to ensure that no instances of this class can be created.
   */
  public ReflectionToolsImpl( ) {
    // Nothing to do.
  }

  /**
   * Method loads the class with the passed name and returns its class object.
   * 
   * @param pClassName Name of the class that should be loaded. The parameter must not be null.
   * @return Class Class object of the class with the passed name. The method never returns null.
   * @throws JEAFSystemException if the class with the passed name could not be found in the applications classpath.
   */
  @Override
  public Class<?> loadClass( String pClassName ) throws JEAFSystemException {
    // Check parameter for null.
    Check.checkInvalidParameterNull(pClassName, "pClassName");

    // Try to load class and return its class object.
    try {
      return Class.forName(pClassName);
    }
    // Provided class name is invalid.
    catch (ClassNotFoundException e) {
      throw new JEAFSystemException(XFunMessages.CLASS_NOT_LOADABLE, e, pClassName);
    }
  }

  /**
   * Method loads the class with the passed name and returns its class object.
   * 
   * @param pClassName Name of the class that should be loaded. The parameter must not be null.
   * @param pType Type that is expected as return type. The parameter must not be null.
   * @return Class Class object of the class with the passed name. The method never returns null.
   * @throws JEAFSystemException if the class with the passed name could not be found in the applications classpath.
   */
  @SuppressWarnings("unchecked")
  public <T> Class<? extends T> loadClass( String pClassName, Class<T> pType ) throws JEAFSystemException {
    return (Class<? extends T>) this.loadClass(pClassName);
  }

  /**
   * Method checks if the class with the passed name is loadable from the class path.
   * 
   * @param pClassName Name of the class that should be loaded. The parameter must not be null.
   * @return boolean The method returns true if the class is loadable and false in all other cases.
   */
  @Override
  public boolean isClassLoadable( String pClassName ) {
    // Check parameter for null.
    Check.checkInvalidParameterNull(pClassName, "pClassName");

    // Try to load class.
    boolean lIsLoadable;
    try {
      Class.forName(pClassName);
      lIsLoadable = true;
    }
    // Provided class could not be loaded.
    catch (ClassNotFoundException e) {
      lIsLoadable = false;
    }
    return lIsLoadable;
  }

  /**
   * Method creates a new instance of the passed class.
   * 
   * @param <T> Type parameter for class that should be created.
   * @param pClass Class of which a new instance should be created. The parameter must not be null.
   * @return Object Created instance of the passed class. The method never returns null.
   * @throws JEAFSystemException if the passed class does not provide a parameterless public constructor.
   */
  @Override
  public <T> T newInstance( Class<T> pClass ) throws JEAFSystemException {
    return this.newInstance(pClass, false);
  }

  /**
   * Method creates a new instance of the passed class.
   * 
   * @param <T> Type parameter for class that should be created.
   * @param pClass Class of which a new instance should be created. The parameter must not be null.
   * @param pMakeAccessible If the parameter is set to true, the method will create a new instance of the passed class
   * ignoring the access modifier of the default constructor.
   * @return Object Created instance of the passed class. The method never returns null.
   * @throws JEAFSystemException if the passed class does not provide a parameterless public constructor.
   */
  @Override
  public <T> T newInstance( Class<T> pClass, boolean pMakeAccessible ) throws JEAFSystemException {
    // Check parameter for null.
    Check.checkInvalidParameterNull(pClass, "pClass");

    // Try to create new instance.
    try {
      Constructor<T> lConstructor = pClass.getDeclaredConstructor();
      if (pMakeAccessible == true) {
        lConstructor.setAccessible(true);
      }
      return lConstructor.newInstance();
    }
    // Unable to create new instance of class.
    catch (ReflectiveOperationException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_CREATE_NEW_INSTANCE, e, pClass.getName());
    }
  }

  /**
   * Method creates a new instance of the passed class. The created object has to be assignable to the passed class.
   * 
   * @param <T> Type
   * @param pClass Class of which a new instance should be created. The parameter must not be null.
   * @param pAssignableClass Class to which the created object has to be assignable to. The parameter must not be null.
   * @return Object Created instance of the passed class. The method never returns null.
   */
  @Override
  public <T> T newInstance( Class<T> pClass, Class<?> pAssignableClass ) {
    // Check both parameters for null.
    Check.checkInvalidParameterNull(pClass, "pClass");
    Check.checkInvalidParameterNull(pAssignableClass, "pAssignableClass");

    // Check if the class of the object to create can be casted to the passed class.
    if (pAssignableClass.isAssignableFrom(pClass) == true) {
      // Create new instance of the passed class.
      return this.newInstance(pClass);
    }
    // Objects of type pClass are not assignable to type pAssignableClass.
    else {
      String[] lParams = new String[] { pClass.getName(), pAssignableClass.getName() };
      throw new JEAFSystemException(ToolsMessages.CLASS_NOT_ASSIGNABLE, lParams);
    }
  }

  /**
   * Method creates a new instance of the class with the passed name. The created object has to be assignable to the
   * passed class.
   * 
   * @param <T> Type
   * @param pClassName Name of the class of which a new instance should be created. The parameter must not be null.
   * @param pAssignableClass Class to which the created object has to be assignable to. The parameter must not be null.
   * @return Object Created instance of the passed class. The method never returns null.
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T> T newInstance( String pClassName, Class<T> pAssignableClass ) {
    // Check parameters for null.
    Check.checkInvalidParameterNull(pClassName, "pClassName");
    Check.checkInvalidParameterNull(pAssignableClass, "pAssignableClass");

    // Load class and create a new instance.
    Class<?> lClass = this.loadClass(pClassName);
    return (T) this.newInstance(lClass, pAssignableClass);
  }

  /**
   * Method returns a map with the passed classes as key and the annotation of the passed type as value. If the class
   * does not have the passed annotation then the class will not be inside the returned map.
   * 
   * @param pClasses List of classes whose annotation of the passed type should be returned. The parameter must not be
   * null.
   * @param pAnnotationType Type of the annotation that should be returned. The parameter must not be null.
   * @return {@link Map} Map with one the passed classes as key and annotation of the passed type as value. The method
   * never returns null.
   */
  @Override
  public <T extends Annotation> Map<Class<?>, T> getAnnotations( List<Class<?>> pClasses, Class<T> pAnnotationType ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pClasses, "pClasses");
    Check.checkInvalidParameterNull(pAnnotationType, "pAnnotationType");

    // Check all passed classed for the requested annotation.
    Map<Class<?>, T> lAnnotationsMap = new HashMap<>();
    for (Class<?> lNextClass : pClasses) {
      T lAnnotation = lNextClass.getAnnotation(pAnnotationType);
      if (lAnnotation != null) {
        lAnnotationsMap.put(lNextClass, lAnnotation);
      }
    }
    return lAnnotationsMap;
  }
}
