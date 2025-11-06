/*
 * anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * Copyright 2004 - 2013 All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.string.StringTools;
import com.anaptecs.jeaf.xfun.api.checks.Check;

/**
 * This class implements the string tools interface.
 * 
 * @author JEAF Development Team
 * @version JEAF Release 1.3
 */
@ToolsImplementation(toolsInterface = StringTools.class)
public class StringToolsImpl implements StringTools {
  private static final String DEFAULT_DELIMITER = ", ";

  /**
   * <p>
   * Method checks if the passed string object is null or empty.
   * </p>
   * 
   * <pre>
   * isEmpty(null) = true
   * isEmpty("") = true
   * isEmpty(" ") = true
   * isEmpty("abc") = false
   * isEmpty(" abc ") = false
   * </pre>
   * 
   * @param pString The string object to be checked. This parameter may be null.
   * @return {@code true} if the passed string object is null or empty, false otherwise.
   * @see org.apache.commons.lang3.StringUtils
   */
  @Override
  public boolean isEmpty( String pString ) {
    return pString == null || pString.trim().length() == 0;
  }

  /**
   * <p>
   * Method checks if the passed string object is not null or not empty.
   * </p>
   * 
   * <pre>
   * isRealString(null) = false
   * isRealString("") = false
   * isRealString(" ") = false
   * isRealString("abc") = true
   * isRealString(" abc ") = true
   * </pre>
   * 
   * @param pString The string object to be checked. This parameter may be null.
   * @return {@code true} if the passed string object is not null or not empty, false otherwise.
   * @see org.apache.commons.lang3.StringUtils
   */
  @Override
  public boolean isRealString( String pString ) {
    return !this.isEmpty(pString);
  }

  /**
   * <p>
   * Method checks if the passed string object is null or empty or whitespace.
   * </p>
   * 
   * <pre>
   * isEmpty(null) = true
   * isEmpty("") = true
   * isEmpty(" ") = true
   * isEmpty("abc") = false
   * isEmpty(" abc ") = false
   * </pre>
   * 
   * @param pString The string object to be checked. This parameter may be null.
   * @return {@code true} if the passed string object is null or empty or whitespace, false otherwise.
   * @see org.apache.commons.lang3.StringUtils
   */
  @Override
  public boolean isBlank( String pString ) {
    boolean lIsBlank;
    if (pString == null) {
      lIsBlank = true;
    }
    else {
      lIsBlank = pString.trim().isEmpty();
    }
    return lIsBlank;
  }

  /**
   * <p>
   * Method checks if the passed string object is not null or not empty or not whitespace.
   * </p>
   * 
   * <pre>
   * isNotBlank(null) = false
   * isNotBlank("") = false
   * isNotBlank(" ") = false
   * isNotBlank("abc") = true
   * isNotBlank(" abc ") = true
   * </pre>
   * 
   * @param pString The string object to be checked. This parameter may be null.
   * @return {@code true} if the passed string object is not null or not empty or not whitespace, false otherwise.
   * @see org.apache.commons.lang3.StringUtils
   */
  @Override
  public boolean isNotBlank( String pString ) {
    return !this.isBlank(pString);
  }

  /**
   * Method checks if the passed string only consists of digits. Digits only means no other characters even not decimal
   * delimiter or sign.
   * 
   * @param pString String that should be checked for digits only. The parameter must not be null.
   * @return {@link Boolean} Method returns true if the string only consists of digits.
   */
  @Override
  public boolean containsDigitsOnly( String pString ) {
    // Check parameter
    Check.checkInvalidParameterNull(pString, "pString");

    // Check if the parameter only consists of digits.
    boolean lContainsDigitsOnly = true;
    for (char lNextCharacter : pString.toCharArray()) {
      if (Character.isDigit(lNextCharacter) == false) {
        lContainsDigitsOnly = false;
        break;
      }
    }
    return lContainsDigitsOnly;
  }

  /**
   * Method splits the passed string using the passed regex using method {@link String#split(String)}. In addition its
   * also possible to trim the split strings.
   * 
   * @param pString String that should be split. The parameter must not be null.
   * @param pRegex Regex that should be used to split the passed string. The parameter must not be null. Check java doc
   * of {@link String#split(String)} in order to see how the regex has to be defined.
   * @param pTrim Parameter defines if the resulting strings should be trimmed or not.
   * @return {@link List} List contain all split string in the same order as they occurred in the passed string. Method
   * never returns null.
   */
  @Override
  public List<String> split( String pString, String pRegex, boolean pTrim ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pRegex, pRegex);

    List<String> lStrings;
    if (pString != null) {
      // Split the string using the passed regex.
      String[] lResultArray = pString.split(pRegex);
      lStrings = new ArrayList<>(lResultArray.length);
      for (int i = 0; i < lResultArray.length; i++) {
        if (pTrim == true) {
          lStrings.add(lResultArray[i].trim());
        }
        else {
          lStrings.add(lResultArray[i]);
        }
      }
    }
    // Passed string was null thus we will return an empty list.
    else {
      lStrings = Collections.emptyList();
    }
    return lStrings;
  }

  /**
   * Method checks if the passed string has the same ending as one of the passed strings.
   * 
   * @param pString String that should be compared. The parameter must not be null.
   * @param pList List of string which may have the same ending as <code>pString</code> This parameter must not be null.
   * @return boolean The method returns true if at least of the passed strings from the list has the same ending as the
   * passed one and false in all other cases.
   */
  @Override
  public boolean endsWith( String pString, List<String> pList ) {
    // Check parameters
    Check.checkInvalidParameterNull(pString, pString);
    Check.checkInvalidParameterNull(pList, "pList");

    boolean lEndsWith = false;
    for (String lNextString : pList) {
      if (pString.endsWith(lNextString)) {
        lEndsWith = true;
        break;
      }
    }
    return lEndsWith;
  }

  /**
   * Method compares the passed strings ignoring cases according to the definitions of the interface {@link Comparable}.
   * 
   * @param pString1 First string that should be compared. The parameter may be null.
   * @param pString2 Second string that should be compared. The parameter may be null.
   * @return int Result of the compare. For further details see {@link Comparable#compareTo(Object)}
   */
  @Override
  public int compareToIgnoreCase( String pString1, String pString2 ) {
    return this.compareTo(pString1, pString2, true);
  }

  /**
   * Method compares the passed strings according to the definitions of the interface {@link Comparable}.
   * 
   * @param pString1 First string that should be compared. The parameter may be null.
   * @param pString2 Second string that should be compared. The parameter may be null.
   * @return int Result of the compare. For further details see {@link Comparable#compareTo(Object)}
   */
  @Override
  public int compareTo( String pString1, String pString2 ) {
    return this.compareTo(pString1, pString2, false);
  }

  private int compareTo( String pString1, String pString2, boolean pIgnoreCase ) {
    int lResult;
    // Both strings are not null.
    if (pString1 != null && pString2 != null) {
      // Compare ignoring cases.
      if (pIgnoreCase == true) {
        lResult = pString1.compareToIgnoreCase(pString2);
      }
      // Compare exactly.
      else {
        lResult = pString1.compareTo(pString2);
      }
    }
    // Both strings are null
    else if (pString1 == null && pString2 == null) {
      lResult = 0;
    }
    // First string is only null
    else if (pString1 == null) {
      lResult = -1;
    }
    // Second string is only null
    else {
      lResult = 1;
    }
    return lResult;
  }

  /**
   * Method returns the names of all passed classes as a comma separated string.
   * 
   * @param pClasses Collection with all classes. The parameter may be null.
   * @return {@link String} String containing a comma separated list of all class names or an empty string if
   * <code>pClasses</code> is empty or null.
   */
  @Override
  public String getClassNamesAsString( Collection<Class<?>> pClasses ) {
    return this.getClassNamesAsString(pClasses, "");
  }

  /**
   * Method returns the names of all passed classes as a comma separated string.
   * 
   * @param pClasses Collection with all classes. The parameter may be null.
   * @param pEmptyText String that is used in case that the passed collection is empty or null. The parameter may be
   * null.
   * @return {@link String} String containing a comma separated list of all class names or an empty string if
   * <code>pClasses</code> is empty or null.
   */
  @Override
  public String getClassNamesAsString( Collection<Class<?>> pClasses, String pEmptyText ) {
    return this.getClassNamesAsString(pClasses, pEmptyText, DEFAULT_DELIMITER);
  }

  /**
   * Method returns the names of all passed classes as a comma separated string.
   * 
   * @param pClasses Collection with all classes. The parameter may be null.
   * @param pEmptyText String that is used in case that the passed collection is empty or null. The parameter may be
   * null.
   * @param pDelimiter Delimiter that should be used. The parameter must not be null.
   * @return {@link String} String containing a comma separated list of all class names or an empty string if
   * <code>pClasses</code> is empty or null.
   */
  @Override
  public String getClassNamesAsString( Collection<Class<?>> pClasses, String pEmptyText, String pDelimiter ) {
    // Check parameter
    Check.checkInvalidParameterNull(pDelimiter, "pDelimiter");

    String lClassNames;
    if (pClasses != null) {
      StringBuilder lBuilder = new StringBuilder();
      if (pClasses.isEmpty() == false) {
        Iterator<Class<?>> lIterator = pClasses.iterator();
        while (lIterator.hasNext()) {
          Class<?> lNext = lIterator.next();
          lBuilder.append(lNext.getName());
          if (lIterator.hasNext()) {
            lBuilder.append(pDelimiter);
          }
        }
        lClassNames = lBuilder.toString();
      }
      else {
        lClassNames = pEmptyText;
      }
    }
    else {
      lClassNames = pEmptyText;
    }
    return lClassNames;
  }

  /**
   * Method returns the names of all passed classes as a comma separated string.
   * 
   * @param pClasses Array with all classes. The parameter may be null.
   * @return {@link String} String containing a comma separated list of all class names or an empty string if
   * <code>pClasses</code> is empty or null.
   */
  @Override
  public String getClassNamesAsString( Class<?>[] pClasses ) {
    return this.getClassNamesAsString(pClasses, "", DEFAULT_DELIMITER);
  }

  /**
   * Method returns the names of all passed classes as a comma separated string.
   * 
   * @param pClasses Array with all classes. The parameter may be null.
   * @param pEmptyText String that is used in case that the passed collection is empty or null. The parameter may be
   * null.
   * @return {@link String} String containing a comma separated list of all class names or an empty string if
   * <code>pClasses</code> is empty or null.
   */
  @Override
  public String getClassNamesAsString( Class<?>[] pClasses, String pEmptyText ) {
    return this.getClassNamesAsString(pClasses, pEmptyText, DEFAULT_DELIMITER);
  }

  /**
   * Method returns the names of all passed classes as a comma separated string.
   * 
   * @param pClasses Array with all classes. The parameter may be null.
   * @param pEmptyText String that is used in case that the passed collection is empty or null. The parameter may be
   * null.
   * @param pDelimiter Delimiter that should be used. The parameter must not be null.
   * @return {@link String} String containing a comma separated list of all class names or an empty string if
   * <code>pClasses</code> is empty or null.
   */
  @Override
  public String getClassNamesAsString( Class<?>[] pClasses, String pEmptyText, String pDelimiter ) {
    List<Class<?>> lList;
    if (pClasses != null) {
      lList = Arrays.asList(pClasses);
    }
    else {
      lList = null;
    }
    return this.getClassNamesAsString(lList, pEmptyText, pDelimiter);
  }
}