/*
 * anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * Copyright 2004 - 2013 All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.locale;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.locale.LocaleTools;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.checks.Check;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;

/**
 * Class implements locale tools.
 * 
 * This class also works with data about primary languages of countries. This data is taken from geonames.org and is
 * licensed under a Creative Commons Attribution 4.0 License ({@link https://creativecommons.org/licenses/by/4.0/}). The
 * Data is provided "as is" without warranty or any representation of accuracy, timeliness or completeness.
 * 
 * 
 * @author JEAF Development Team
 * @version 1.0
 */
@ToolsImplementation(toolsInterface = LocaleTools.class)
public final class LocaleToolsImpl implements LocaleTools {
  /**
   * Map contains all available countries and their alpha-2 and alpha-3 country codes. As many countries have multiple
   * languages in this case the primary language is used.
   */
  private final Map<String, Locale> countries;

  /**
   * Constructor of this class is private in order to ensure that no instances of this class can be created.
   */
  public LocaleToolsImpl( ) {
    // Load primary languages of all countries.
    Map<String, String> lPrimaryLanguages = getPrimaryLanguages();

    // Process all available locales
    Map<String, Locale> lCountries = new HashMap<>();
    for (Locale lNext : Locale.getAvailableLocales()) {
      String lCountryCode = lNext.getCountry();

      if (lCountryCode.isEmpty() == false) {
        // Check if language of current locale is the primary language of country.
        String lPrimaryLanguage = lPrimaryLanguages.get(lCountryCode);
        if (lPrimaryLanguage != null) {
          if (lPrimaryLanguage.equals(lNext.getLanguage())) {
            lCountries.put(lCountryCode, lNext);

            try {
              lCountries.put(lNext.getISO3Country(), lNext);
            }
            catch (MissingResourceException e) {
              // Nothing to do.
            }
          }
        }
        // Hoping that there are not multiple locales for the same country
        else {
          XFun.getTrace().debug("No primary language found for " + lNext.toString());
          lCountries.put(lCountryCode, lNext);

          try {
            lCountries.put(lNext.getISO3Country(), lNext);
          }
          catch (MissingResourceException e) {
            // Nothing to do.
          }
        }
      }
    }
    countries = Collections.unmodifiableMap(lCountries);
  }

  /**
   * Method resolves the primary languages for most known countries
   * 
   * @return
   */
  private Map<String, String> getPrimaryLanguages( ) {
    ResourceBundle lProperties = ResourceBundle.getBundle("PrimaryLanguagesALPHA-2");
    Map<String, String> lPrimaryLanguages = new HashMap<>();
    Enumeration<String> lKeys = lProperties.getKeys();
    while (lKeys.hasMoreElements()) {
      String lNextKey = lKeys.nextElement();
      lPrimaryLanguages.put(lNextKey, lProperties.getString(lNextKey));
    }
    return lPrimaryLanguages;
  }

  /**
   * Constant for the separator for the parts of a string representation of a locale.
   */
  public static final String LOCALE_SEPERATOR = "_";

  /**
   * Method converts a string representation of a locale to a real Java Locale object. The string has to have the format
   * as created by method <code>java.util.Locale.toString()</code>. Since Java does not support the validation of
   * locales this method only provides an elementary validation. According to the locale definition of the JDK a locale
   * consists of three parts, the language, the county and the variant, where a language or a country is required.
   * 
   * @param pLocaleString String that should be converted to a locale object. The parameter must not be null.
   * @return Locale Created locale object. The method never returns null.
   * @throws JEAFSystemException if the passed locale string has an invalid format or does neither define a language nor
   * a country.
   */
  @Override
  public Locale createLocale( String pLocaleString ) throws JEAFSystemException {
    // Check parameter
    Check.checkInvalidParameterNull(pLocaleString, "pLocaleString");

    StringTokenizer lTokenizer = new StringTokenizer(pLocaleString, LOCALE_SEPERATOR, true);
    int lTokenCount = lTokenizer.countTokens();

    // Check number of tokens. If the string consists of more than 5 tokens then its format is invalid.
    final int lMaxTokens = 5;
    if (lTokenCount <= lMaxTokens) {
      String[] lTokens = new String[] { "", "", "" };
      int i = 0;
      while (lTokenizer.hasMoreTokens()) {
        String lNextToken = lTokenizer.nextToken();

        // Check if next token is locale data or a separator.
        if (LOCALE_SEPERATOR.equals(lNextToken) == false) {
          lTokens[i] = lNextToken;
        }
        else {
          i++;
        }
      }
      // Get language, country and variant.
      String lLanguage = lTokens[0].trim();
      String lCountry = lTokens[1].trim();
      String lVariant = lTokens[2].trim();

      // Check if either a language or a country was provided.
      if (lLanguage.length() > 0 || lCountry.length() > 0) {
        return new Locale(lLanguage, lCountry, lVariant);
      }
      // Locale string does neither define a language nor country.
      else {
        String[] lParams = new String[] { pLocaleString };
        throw new JEAFSystemException(ToolsMessages.LANGUAGE_OR_COUNTRY_MISSING, lParams);
      }
    }
    // Locale string has an invalid format.
    else {
      String[] lParams = new String[] { pLocaleString };
      throw new JEAFSystemException(ToolsMessages.INVALID_LOCALE_STRING_FORMAT, lParams);
    }
  }

  /**
   * Method resolves a matching locale object representing the country of the passed country code. The passed code can
   * either be ISO 3166 alpha-2 or alpha-3 country code.
   * 
   * Please be aware that in multiple countries more than one language is spoken. So it is not always possible find the
   * one and only language per country. In this case the primary language of an country is chosen. Therefore data from
   * geonames.org is used.
   * 
   * @param pCountryCode Alpha-2 or alpha-3 country code that is used to identify the matching locale. The parameter
   * must not be null.
   * @return {@link Locale} Locale representing the country with the passed country code. The method never returns null.
   * @throws JEAFSystemException in case that the country code is neither a valid alpha-2 nor alpha-3 code.
   */
  @Override
  public Locale getLocaleFromCountryCode( String pCountryCode ) throws JEAFSystemException {
    // Check parameter
    Check.checkInvalidParameterNull(pCountryCode, "pCountryCode");

    // Try to resolve country by its country code.
    Locale lCountry = countries.get(pCountryCode);

    // Ensure that the country code is valid.
    if (lCountry != null) {
      return lCountry;
    }
    else {
      throw new JEAFSystemException(ToolsMessages.INVALID_COUNTRY_CODE, pCountryCode);
    }
  }

  /**
   * Method returns the current locale. How the current locale is determined depends on the environment and the used
   * locale provider implementation.
   * 
   * @return Locale Locale that is appropriate in the current context. The method never returns null.
   */
  @Override
  public Locale getCurrentLocale( ) {
    return XFun.getLocaleProvider().getCurrentLocale();
  }
}
