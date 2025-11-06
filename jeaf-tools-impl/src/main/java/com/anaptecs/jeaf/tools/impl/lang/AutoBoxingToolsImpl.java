/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.lang;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.lang.AutoBoxingTools;
import com.anaptecs.jeaf.xfun.api.checks.Check;

/**
 * Class provides a default implementation for JEAF's AutoBoxingTools. The implementation can be used in any
 * environment.
 * 
 * @author JEAF Development Team
 */
@ToolsImplementation(toolsInterface = AutoBoxingTools.class)
public class AutoBoxingToolsImpl implements AutoBoxingTools {
  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter must not be null.
   * @return Primitive value of the passed wrapper.
   */
  @Override
  public boolean autoBox( Boolean pWrapper ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pWrapper, "pWrapper");

    // Return primitive value.
    return pWrapper.booleanValue();
  }

  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter may be null.
   * @param pNullDefault Default value that will be used in case that the passed wrapper is null.
   * @return Primitive value of the passed wrapper or its null default value.
   */
  @Override
  public boolean autoBox( Boolean pWrapper, boolean pNullDefault ) {
    boolean lValue;

    // Real value passed.
    if (pWrapper != null) {
      lValue = pWrapper.booleanValue();
    }
    // Fallback to passed default value.
    else {
      lValue = pNullDefault;
    }
    return lValue;
  }

  /**
   * Method converts the passed primitive type into its wrapper type.
   * 
   * @param pPrimitive Primitive type that should be converted.
   * @return Wrapper type into which the primitive type was converted. The method never returns null.
   */
  @Override
  public Boolean autoBox( boolean pPrimitive ) {
    return Boolean.valueOf(pPrimitive);
  }

  /**
   * Method converts the passed array of wrapper types into an array of its primitive types.
   * 
   * @param pWrapper Array of wrapper types the should be converted. The parameter may be null.
   * @return Array of primitive types. If the array of wrapper types is null then the method will also return null.
   */
  @Override
  public boolean[] autoBox( Boolean[] pWrapper ) {
    // Real array was passed.
    boolean[] lArray;
    if (pWrapper != null) {
      lArray = new boolean[pWrapper.length];
      for (int i = 0; i < pWrapper.length; i++) {
        lArray[i] = pWrapper[i];
      }
    }
    // null was passed. So we will also return null.
    else {
      lArray = null;
    }

    // Return primitive array
    return lArray;
  }

  /**
   * Method converts the passed primitive array into an array of wrapper types.
   * 
   * @param pPrimitive Array of primitive types that should be converted. The parameter may be null.
   * @return Array of wrapper types. If the passed array is null then the method will also return null.
   */
  @Override
  public Boolean[] autoBox( boolean[] pPrimitive ) {
    Boolean[] lArray;
    if (pPrimitive != null) {
      lArray = new Boolean[pPrimitive.length];
      for (int i = 0; i < pPrimitive.length; i++) {
        lArray[i] = pPrimitive[i];
      }
    }
    else {
      lArray = null;
    }
    return lArray;
  }

  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter must not be null.
   * @return Primitive value of the passed wrapper.
   */
  @Override
  public byte autoBox( Byte pWrapper ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pWrapper, "pWrapper");

    // Return primitive value.
    return pWrapper.byteValue();

  }

  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter may be null.
   * @param pNullDefault Default value that will be used in case that the passed wrapper is null.
   * @return Primitive value of the passed wrapper or its null default value.
   */
  @Override
  public byte autoBox( Byte pWrapper, byte pNullDefault ) {
    byte lValue;

    // Real value passed.
    if (pWrapper != null) {
      lValue = pWrapper.byteValue();
    }
    // Fallback to passed default value.
    else {
      lValue = pNullDefault;
    }
    return lValue;
  }

  /**
   * Method converts the passed primitive type into its wrapper type.
   * 
   * @param pPrimitive Primitive type that should be converted.
   * @return Wrapper type into which the primitive type was converted. The method never returns null.
   */
  @Override
  public Byte autoBox( byte pPrimitive ) {
    return Byte.valueOf(pPrimitive);
  }

  /**
   * Method converts the passed array of wrapper types into an array of its primitive types.
   * 
   * @param pWrapper Array of wrapper types the should be converted. The parameter may be null.
   * @return Array of primitive types. If the array of wrapper types is null then the method will also return null.
   */
  @Override
  public byte[] autoBox( Byte[] pWrapper ) {
    // Real array was passed.
    byte[] lArray;
    if (pWrapper != null) {
      lArray = new byte[pWrapper.length];
      for (int i = 0; i < pWrapper.length; i++) {
        lArray[i] = pWrapper[i];
      }
    }
    // null was passed. So we will also return null.
    else {
      lArray = null;
    }

    // Return primitive array
    return lArray;
  }

  /**
   * Method converts the passed primitive array into an array of wrapper types.
   * 
   * @param pPrimitive Array of primitive types that should be converted. The parameter may be null.
   * @return Array of wrapper types. If the passed array is null then the method will also return null.
   */
  @Override
  public Byte[] autoBox( byte[] pPrimitive ) {
    Byte[] lArray;
    if (pPrimitive != null) {
      lArray = new Byte[pPrimitive.length];
      for (int i = 0; i < pPrimitive.length; i++) {
        lArray[i] = pPrimitive[i];
      }
    }
    else {
      lArray = null;
    }
    return lArray;
  }

  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter must not be null.
   * @return Primitive value of the passed wrapper.
   */
  @Override
  public short autoBox( Short pWrapper ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pWrapper, "pWrapper");

    // Return primitive value.
    return pWrapper.shortValue();
  }

  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter may be null.
   * @param pNullDefault Default value that will be used in case that the passed wrapper is null.
   * @return Primitive value of the passed wrapper or its null default value.
   */
  @Override
  public short autoBox( Short pWrapper, short pNullDefault ) {
    short lValue;

    // Real value passed.
    if (pWrapper != null) {
      lValue = pWrapper.shortValue();
    }
    // Fallback to passed default value.
    else {
      lValue = pNullDefault;
    }
    return lValue;
  }

  /**
   * Method converts the passed primitive type into its wrapper type.
   * 
   * @param pPrimitive Primitive type that should be converted.
   * @return Wrapper type into which the primitive type was converted. The method never returns null.
   */
  @Override
  public Short autoBox( short pPrimitive ) {
    return Short.valueOf(pPrimitive);
  }

  /**
   * Method converts the passed array of wrapper types into an array of its primitive types.
   * 
   * @param pWrapper Array of wrapper types the should be converted. The parameter may be null.
   * @return Array of primitive types. If the array of wrapper types is null then the method will also return null.
   */
  @Override
  public short[] autoBox( Short[] pWrapper ) {
    // Real array was passed.
    short[] lArray;
    if (pWrapper != null) {
      lArray = new short[pWrapper.length];
      for (int i = 0; i < pWrapper.length; i++) {
        lArray[i] = pWrapper[i];
      }
    }
    // null was passed. So we will also return null.
    else {
      lArray = null;
    }

    // Return primitive array
    return lArray;
  }

  /**
   * Method converts the passed primitive array into an array of wrapper types.
   * 
   * @param pPrimitive Array of primitive types that should be converted. The parameter may be null.
   * @return Array of wrapper types. If the passed array is null then the method will also return null.
   */
  @Override
  public Short[] autoBox( short[] pPrimitive ) {
    Short[] lArray;
    if (pPrimitive != null) {
      lArray = new Short[pPrimitive.length];
      for (int i = 0; i < pPrimitive.length; i++) {
        lArray[i] = pPrimitive[i];
      }
    }
    else {
      lArray = null;
    }
    return lArray;
  }

  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter must not be null.
   * @return Primitive value of the passed wrapper.
   */
  @Override
  public int autoBox( Integer pWrapper ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pWrapper, "pWrapper");

    // Return primitive value.
    return pWrapper.intValue();
  }

  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter may be null.
   * @param pNullDefault Default value that will be used in case that the passed wrapper is null.
   * @return Primitive value of the passed wrapper or its null default value.
   */
  @Override
  public int autoBox( Integer pWrapper, int pNullDefault ) {
    int lValue;

    // Real value passed.
    if (pWrapper != null) {
      lValue = pWrapper.intValue();
    }
    // Fallback to passed default value.
    else {
      lValue = pNullDefault;
    }
    return lValue;
  }

  /**
   * Method converts the passed primitive type into its wrapper type.
   * 
   * @param pPrimitive Primitive type that should be converted.
   * @return Wrapper type into which the primitive type was converted. The method never returns null.
   */
  @Override
  public Integer autoBox( int pPrimitive ) {
    return Integer.valueOf(pPrimitive);
  }

  /**
   * Method converts the passed array of wrapper types into an array of its primitive types.
   * 
   * @param pWrapper Array of wrapper types the should be converted. The parameter may be null.
   * @return Array of primitive types. If the array of wrapper types is null then the method will also return null.
   */
  @Override
  public int[] autoBox( Integer[] pWrapper ) {
    // Real array was passed.
    int[] lArray;
    if (pWrapper != null) {
      lArray = new int[pWrapper.length];
      for (int i = 0; i < pWrapper.length; i++) {
        lArray[i] = pWrapper[i];
      }
    }
    // null was passed. So we will also return null.
    else {
      lArray = null;
    }

    // Return primitive array
    return lArray;
  }

  /**
   * Method converts the passed primitive array into an array of wrapper types.
   * 
   * @param pPrimitive Array of primitive types that should be converted. The parameter may be null.
   * @return Array of wrapper types. If the passed array is null then the method will also return null.
   */
  @Override
  public Integer[] autoBox( int[] pPrimitive ) {
    Integer[] lArray;
    if (pPrimitive != null) {
      lArray = new Integer[pPrimitive.length];
      for (int i = 0; i < pPrimitive.length; i++) {
        lArray[i] = pPrimitive[i];
      }
    }
    else {
      lArray = null;
    }
    return lArray;
  }

  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter must not be null.
   * @return Primitive value of the passed wrapper.
   */
  @Override
  public long autoBox( Long pWrapper ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pWrapper, "pWrapper");

    // Return primitive value.
    return pWrapper.longValue();
  }

  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter may be null.
   * @param pNullDefault Default value that will be used in case that the passed wrapper is null.
   * @return Primitive value of the passed wrapper or its null default value.
   */
  @Override
  public long autoBox( Long pWrapper, long pNullDefault ) {
    long lValue;

    // Real value passed.
    if (pWrapper != null) {
      lValue = pWrapper.longValue();
    }
    // Fallback to passed default value.
    else {
      lValue = pNullDefault;
    }
    return lValue;
  }

  /**
   * Method converts the passed primitive type into its wrapper type.
   * 
   * @param pPrimitive Primitive type that should be converted.
   * @return Wrapper type into which the primitive type was converted. The method never returns null.
   */
  @Override
  public Long autoBox( long pPrimitive ) {
    return Long.valueOf(pPrimitive);
  }

  /**
   * Method converts the passed array of wrapper types into an array of its primitive types.
   * 
   * @param pWrapper Array of wrapper types the should be converted. The parameter may be null.
   * @return Array of primitive types. If the array of wrapper types is null then the method will also return null.
   */
  @Override
  public long[] autoBox( Long[] pWrapper ) {
    // Real array was passed.
    long[] lArray;
    if (pWrapper != null) {
      lArray = new long[pWrapper.length];
      for (int i = 0; i < pWrapper.length; i++) {
        lArray[i] = pWrapper[i];
      }
    }
    // null was passed. So we will also return null.
    else {
      lArray = null;
    }

    // Return primitive array
    return lArray;
  }

  /**
   * Method converts the passed primitive array into an array of wrapper types.
   * 
   * @param pPrimitive Array of primitive types that should be converted. The parameter may be null.
   * @return Array of wrapper types. If the passed array is null then the method will also return null.
   */
  @Override
  public Long[] autoBox( long[] pPrimitive ) {
    Long[] lArray;
    if (pPrimitive != null) {
      lArray = new Long[pPrimitive.length];
      for (int i = 0; i < pPrimitive.length; i++) {
        lArray[i] = pPrimitive[i];
      }
    }
    else {
      lArray = null;
    }
    return lArray;
  }

  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter must not be null.
   * @return Primitive value of the passed wrapper.
   */
  @Override
  public float autoBox( Float pWrapper ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pWrapper, "pWrapper");

    // Return primitive value.
    return pWrapper.floatValue();
  }

  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter may be null.
   * @param pNullDefault Default value that will be used in case that the passed wrapper is null.
   * @return Primitive value of the passed wrapper or its null default value.
   */
  @Override
  public float autoBox( Float pWrapper, float pNullDefault ) {
    float lValue;

    // Real value passed.
    if (pWrapper != null) {
      lValue = pWrapper.floatValue();
    }
    // Fallback to passed default value.
    else {
      lValue = pNullDefault;
    }
    return lValue;
  }

  /**
   * Method converts the passed primitive type into its wrapper type.
   * 
   * @param pPrimitive Primitive type that should be converted.
   * @return Wrapper type into which the primitive type was converted. The method never returns null.
   */
  @Override
  public Float autoBox( float pPrimitive ) {
    return Float.valueOf(pPrimitive);
  }

  /**
   * Method converts the passed array of wrapper types into an array of its primitive types.
   * 
   * @param pWrapper Array of wrapper types the should be converted. The parameter may be null.
   * @return Array of primitive types. If the array of wrapper types is null then the method will also return null.
   */
  @Override
  public float[] autoBox( Float[] pWrapper ) {
    // Real array was passed.
    float[] lArray;
    if (pWrapper != null) {
      lArray = new float[pWrapper.length];
      for (int i = 0; i < pWrapper.length; i++) {
        lArray[i] = pWrapper[i];
      }
    }
    // null was passed. So we will also return null.
    else {
      lArray = null;
    }

    // Return primitive array
    return lArray;
  }

  /**
   * Method converts the passed primitive array into an array of wrapper types.
   * 
   * @param pPrimitive Array of primitive types that should be converted. The parameter may be null.
   * @return Array of wrapper types. If the passed array is null then the method will also return null.
   */
  @Override
  public Float[] autoBox( float[] pPrimitive ) {
    Float[] lArray;
    if (pPrimitive != null) {
      lArray = new Float[pPrimitive.length];
      for (int i = 0; i < pPrimitive.length; i++) {
        lArray[i] = pPrimitive[i];
      }
    }
    else {
      lArray = null;
    }
    return lArray;
  }

  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter must not be null.
   * @return Primitive value of the passed wrapper.
   */
  @Override
  public double autoBox( Double pWrapper ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pWrapper, "pWrapper");

    // Return primitive value.
    return pWrapper.doubleValue();
  }

  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter may be null.
   * @param pNullDefault Default value that will be used in case that the passed wrapper is null.
   * @return Primitive value of the passed wrapper or its null default value.
   */
  @Override
  public double autoBox( Double pWrapper, double pNullDefault ) {
    double lValue;

    // Real value passed.
    if (pWrapper != null) {
      lValue = pWrapper.doubleValue();
    }
    // Fallback to passed default value.
    else {
      lValue = pNullDefault;
    }
    return lValue;
  }

  /**
   * Method converts the passed primitive type into its wrapper type.
   * 
   * @param pPrimitive Primitive type that should be converted.
   * @return Wrapper type into which the primitive type was converted. The method never returns null.
   */
  @Override
  public Double autoBox( double pPrimitive ) {
    return Double.valueOf(pPrimitive);
  }

  /**
   * Method converts the passed array of wrapper types into an array of its primitive types.
   * 
   * @param pWrapper Array of wrapper types the should be converted. The parameter may be null.
   * @return Array of primitive types. If the array of wrapper types is null then the method will also return null.
   */
  @Override
  public double[] autoBox( Double[] pWrapper ) {
    // Real array was passed.
    double[] lArray;
    if (pWrapper != null) {
      lArray = new double[pWrapper.length];
      for (int i = 0; i < pWrapper.length; i++) {
        lArray[i] = pWrapper[i];
      }
    }
    // null was passed. So we will also return null.
    else {
      lArray = null;
    }

    // Return primitive array
    return lArray;
  }

  /**
   * Method converts the passed primitive array into an array of wrapper types.
   * 
   * @param pPrimitive Array of primitive types that should be converted. The parameter may be null.
   * @return Array of wrapper types. If the passed array is null then the method will also return null.
   */
  @Override
  public Double[] autoBox( double[] pPrimitive ) {
    Double[] lArray;
    if (pPrimitive != null) {
      lArray = new Double[pPrimitive.length];
      for (int i = 0; i < pPrimitive.length; i++) {
        lArray[i] = pPrimitive[i];
      }
    }
    else {
      lArray = null;
    }
    return lArray;
  }

  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter must not be null.
   * @return Primitive value of the passed wrapper.
   */
  @Override
  public char autoBox( Character pWrapper ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pWrapper, "pWrapper");

    // Return primitive value.
    return pWrapper.charValue();
  }

  /**
   * Method converts the passed wrapper into its primitive type.
   * 
   * @param pWrapper Java wrapper type that should be converted into its primitive type. The parameter may be null.
   * @param pNullDefault Default value that will be used in case that the passed wrapper is null.
   * @return Primitive value of the passed wrapper or its null default value.
   */
  @Override
  public char autoBox( Character pWrapper, char pNullDefault ) {
    char lValue;

    // Real value passed.
    if (pWrapper != null) {
      lValue = pWrapper.charValue();
    }
    // Fallback to passed default value.
    else {
      lValue = pNullDefault;
    }
    return lValue;
  }

  /**
   * Method converts the passed primitive type into its wrapper type.
   * 
   * @param pPrimitive Primitive type that should be converted.
   * @return Wrapper type into which the primitive type was converted. The method never returns null.
   */
  @Override
  public Character autoBox( char pPrimitive ) {
    return Character.valueOf(pPrimitive);
  }

  /**
   * Method converts the passed array of wrapper types into an array of its primitive types.
   * 
   * @param pWrapper Array of wrapper types the should be converted. The parameter may be null.
   * @return Array of primitive types. If the array of wrapper types is null then the method will also return null.
   */
  @Override
  public char[] autoBox( Character[] pWrapper ) {
    // Real array was passed.
    char[] lArray;
    if (pWrapper != null) {
      lArray = new char[pWrapper.length];
      for (int i = 0; i < pWrapper.length; i++) {
        lArray[i] = pWrapper[i];
      }
    }
    // null was passed. So we will also return null.
    else {
      lArray = null;
    }

    // Return primitive array
    return lArray;
  }

  /**
   * Method converts the passed primitive array into an array of wrapper types.
   * 
   * @param pPrimitive Array of primitive types that should be converted. The parameter may be null.
   * @return Array of wrapper types. If the passed array is null then the method will also return null.
   */
  @Override
  public Character[] autoBox( char[] pPrimitive ) {
    Character[] lArray;
    if (pPrimitive != null) {
      lArray = new Character[pPrimitive.length];
      for (int i = 0; i < pPrimitive.length; i++) {
        lArray[i] = pPrimitive[i];
      }
    }
    else {
      lArray = null;
    }
    return lArray;
  }
}
