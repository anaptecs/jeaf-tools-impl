/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.locale.LocaleTools;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import com.anaptecs.jeaf.xfun.api.errorhandling.SystemException;

import junit.framework.TestCase;

public class LocaleToolsTest {

  /**
   * Method tests method Tools.createLocale(...)
   */
  @Test
  public void testCreateLocale( ) {
    Locale lLocale = LocaleTools.getLocaleTools().createLocale("_DE");
    TestCase.assertEquals("Locale has wrong langage.", "", lLocale.getLanguage());
    TestCase.assertEquals("Locale has wrong country.", Locale.GERMANY.getCountry(), lLocale.getCountry());
    TestCase.assertEquals("Locale has wrong variant.", "", lLocale.getVariant());

    lLocale = Tools.getLocaleTools().createLocale("de");
    TestCase.assertEquals("Locale has wrong langage.", Locale.GERMAN.getLanguage(), lLocale.getLanguage());
    TestCase.assertEquals("Locale has wrong country.", "", lLocale.getCountry());
    TestCase.assertEquals("Locale has wrong variant.", "", lLocale.getVariant());

    lLocale = Tools.getLocaleTools().createLocale("de_DE");
    TestCase.assertEquals("Locale has wrong langage.", Locale.GERMAN.getLanguage(), lLocale.getLanguage());
    TestCase.assertEquals("Locale has wrong country.", Locale.GERMANY.getCountry(), lLocale.getCountry());
    TestCase.assertEquals("Locale has wrong variant.", "", lLocale.getVariant());

    lLocale = Tools.getLocaleTools().createLocale("de_DE_Swabian");
    TestCase.assertEquals("Locale has wrong langage.", Locale.GERMAN.getLanguage(), lLocale.getLanguage());
    TestCase.assertEquals("Locale has wrong country.", Locale.GERMANY.getCountry(), lLocale.getCountry());
    TestCase.assertEquals("Locale has wrong variant.", "Swabian", lLocale.getVariant());

    lLocale = Tools.getLocaleTools().createLocale("de__Swabian");
    TestCase.assertEquals("Locale has wrong langage.", Locale.GERMAN.getLanguage(), lLocale.getLanguage());
    TestCase.assertEquals("Locale has wrong country.", "", lLocale.getCountry());
    TestCase.assertEquals("Locale has wrong variant.", "Swabian", lLocale.getVariant());

    lLocale = Tools.getLocaleTools().createLocale("_DE_Swabian");
    TestCase.assertEquals("Locale has wrong langage.", "", lLocale.getLanguage());
    TestCase.assertEquals("Locale has wrong country.", Locale.GERMANY.getCountry(), lLocale.getCountry());
    TestCase.assertEquals("Locale has wrong variant.", "Swabian", lLocale.getVariant());

    //
    // Test method with invalid locale strings.
    //

    // Test with variant only.
    try {
      lLocale = Tools.getLocaleTools().createLocale("__Swabian");
      TestCase.fail("A variant only is not a valid locale string.");
    }
    catch (SystemException e) {
      // Check exception.
      TestCase.assertEquals("Wrong error code.", ToolsMessages.LANGUAGE_OR_COUNTRY_MISSING, e.getErrorCode());
    }

    // Test with empty string.
    try {
      lLocale = Tools.getLocaleTools().createLocale("");
      TestCase.fail("An empty string is not a valid locale string.");
    }
    catch (SystemException e) {
      // Check exception.
      TestCase.assertEquals("Wrong error code.", ToolsMessages.LANGUAGE_OR_COUNTRY_MISSING, e.getErrorCode());
    }

    // Test without locale information.
    try {
      lLocale = Tools.getLocaleTools().createLocale("___");
      TestCase.fail("Empty locale information not detected.");
    }
    catch (SystemException e) {
      // Check exception.
      TestCase.assertEquals("Wrong error code.", ToolsMessages.LANGUAGE_OR_COUNTRY_MISSING, e.getErrorCode());
    }

    // Test with invalid locale format.
    try {
      lLocale = Tools.getLocaleTools().createLocale("de_DE_abc_def_ghi");
      TestCase.fail("Invalid string format not detected.");
    }
    catch (SystemException e) {
      // Check exception.
      TestCase.assertEquals("Exception has wrong error code.", ToolsMessages.INVALID_LOCALE_STRING_FORMAT,
          e.getErrorCode());
    }
  }

  @Test
  void testISOConversion( ) {
    Locale lGermany = Locale.GERMANY;
    assertEquals("DE", lGermany.getCountry());
    assertEquals("DEU", lGermany.getISO3Country());
    LocaleTools lLocaleTools = Tools.getLocaleTools();
    Locale lLocale = lLocaleTools.getLocaleFromCountryCode(lGermany.getCountry());
    assertEquals(lGermany, lLocale);
    lLocale = lLocaleTools.getLocaleFromCountryCode(lGermany.getISO3Country());
    assertEquals(lGermany, lLocale);

    // Test invalid country code.
    try {
      lLocaleTools.getLocaleFromCountryCode("GER");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.INVALID_COUNTRY_CODE, e.getErrorCode());
      assertEquals(1, e.getMessageParameters().length);
      assertEquals("GER", e.getMessageParameters()[0]);
    }

    // Test null handling
    try {
      lLocaleTools.getLocaleFromCountryCode(null);
    }
    catch (IllegalArgumentException e) {
      assertEquals("'pCountryCode' must not be null.", e.getMessage());
    }
  }

  @Test
  public void testGetCurrentLocale( ) {
    // In our case the default locale provide will be used thus the default locale should be returned.
    Locale lCurrentLocale = Tools.getLocaleTools().getCurrentLocale();
    assertEquals(Locale.getDefault(), lCurrentLocale);
  }
}
