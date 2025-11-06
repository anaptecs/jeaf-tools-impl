/*
 * anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * Copyright 2004 - 2013 All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.regexp;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.regexp.RegExpTools;
import com.anaptecs.jeaf.xfun.api.checks.Check;

/**
 * This class implements the regular expression tools interface for all full featured java environments such as JSE and
 * JEE.
 * 
 * @author JEAF Development Team
 * @version JEAF Release 1.2
 */
@ToolsImplementation(toolsInterface = RegExpTools.class)
public class RegExpToolsImpl implements RegExpTools {
  /**
   * In order to avoid to many compiles of patterns. The class has a cache of already compiled patterns.
   */
  private Map<String, Pattern> patternCache = new HashMap<>();

  /**
   * Method checks whether the passed characters match to the passed regular expression pattern. Thereby the caller has
   * to keep in mind that there may be differences in the syntax of the regular expressions depending on the runtime
   * environment.
   * 
   * @param pCharacters Characters that should be checked against the passed pattern. The parameter must not be null.
   * @param pRegExpPattern Regular expression pattern. The parameter must not be null.
   * @return boolean The method returns true if the passed characters match to to passed pattern and false in all other
   * cases.
   */
  public boolean matchesPattern( String pCharacters, String pRegExpPattern ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pCharacters, "pCharacters");
    Check.checkInvalidParameterNull(pRegExpPattern, "pRegExpPattern");

    // Compile pattern if required.
    Pattern lPattern = patternCache.computeIfAbsent(pRegExpPattern, p -> Pattern.compile(pRegExpPattern));

    // Check if pattern and characters match.
    return lPattern.matcher(pCharacters).matches();
  }
}
