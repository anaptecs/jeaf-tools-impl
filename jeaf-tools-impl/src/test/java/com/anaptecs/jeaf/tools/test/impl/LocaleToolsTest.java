/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Locale;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.locale.LocaleTools;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import com.anaptecs.jeaf.xfun.api.errorhandling.SystemException;
import org.junit.jupiter.api.Test;

public class LocaleToolsTest {

  /**
   * Method tests method Tools.createLocale(...)
   */
  @Test
  public void testCreateLocale( ) {
    Locale lLocale = LocaleTools.getLocaleTools().createLocale("_DE");
    assertEquals("", lLocale.getLanguage(), "Locale has wrong langage.");
    assertEquals(Locale.GERMANY.getCountry(), lLocale.getCountry(), "Locale has wrong country.");
    assertEquals("", lLocale.getVariant(), "Locale has wrong variant.");

    lLocale = Tools.getLocaleTools().createLocale("de");
    assertEquals(Locale.GERMAN.getLanguage(), lLocale.getLanguage(), "Locale has wrong langage.");
    assertEquals("", lLocale.getCountry(), "Locale has wrong country.");
    assertEquals("", lLocale.getVariant(), "Locale has wrong variant.");

    lLocale = Tools.getLocaleTools().createLocale("de_DE");
    assertEquals(Locale.GERMAN.getLanguage(), lLocale.getLanguage(), "Locale has wrong langage.");
    assertEquals(Locale.GERMANY.getCountry(), lLocale.getCountry(), "Locale has wrong country.");
    assertEquals("", lLocale.getVariant(), "Locale has wrong variant.");

    lLocale = Tools.getLocaleTools().createLocale("de_DE_Swabian");
    assertEquals(Locale.GERMAN.getLanguage(), lLocale.getLanguage(), "Locale has wrong langage.");
    assertEquals(Locale.GERMANY.getCountry(), lLocale.getCountry(), "Locale has wrong country.");
    assertEquals("Swabian", lLocale.getVariant(), "Locale has wrong variant.");

    lLocale = Tools.getLocaleTools().createLocale("de__Swabian");
    assertEquals(Locale.GERMAN.getLanguage(), lLocale.getLanguage(), "Locale has wrong langage.");
    assertEquals("", lLocale.getCountry(), "Locale has wrong country.");
    assertEquals("Swabian", lLocale.getVariant(), "Locale has wrong variant.");

    lLocale = Tools.getLocaleTools().createLocale("_DE_Swabian");
    assertEquals("", lLocale.getLanguage(), "Locale has wrong langage.");
    assertEquals(Locale.GERMANY.getCountry(), lLocale.getCountry(), "Locale has wrong country.");
    assertEquals("Swabian", lLocale.getVariant(), "Locale has wrong variant.");

    //
    // Test method with invalid locale strings.
    //

    // Test with variant only.
    try {
      lLocale = Tools.getLocaleTools().createLocale("__Swabian");
      fail("A variant only is not a valid locale string.");
    }
    catch (SystemException e) {
      // Check exception.
      assertEquals(ToolsMessages.LANGUAGE_OR_COUNTRY_MISSING, e.getErrorCode(), "Wrong error code.");
    }

    // Test with empty string.
    try {
      lLocale = Tools.getLocaleTools().createLocale("");
      fail("An empty string is not a valid locale string.");
    }
    catch (SystemException e) {
      // Check exception.
      assertEquals(ToolsMessages.LANGUAGE_OR_COUNTRY_MISSING, e.getErrorCode(), "Wrong error code.");
    }

    // Test without locale information.
    try {
      lLocale = Tools.getLocaleTools().createLocale("___");
      fail("Empty locale information not detected.");
    }
    catch (SystemException e) {
      // Check exception.
      assertEquals(ToolsMessages.LANGUAGE_OR_COUNTRY_MISSING, e.getErrorCode(), "Wrong error code.");
    }

    // Test with invalid locale format.
    try {
      lLocale = Tools.getLocaleTools().createLocale("de_DE_abc_def_ghi");
      fail("Invalid string format not detected.");
    }
    catch (SystemException e) {
      // Check exception.
      assertEquals(ToolsMessages.INVALID_LOCALE_STRING_FORMAT, e.getErrorCode(), "Exception has wrong error code.");
    }
  }

  @Test
  void testISOConversion( ) {
    Locale lGermany = Locale.GERMANY;
    assertEquals("DE", lGermany.getCountry());
    assertEquals("DEU", lGermany.getISO3Country());
    LocaleTools lLocaleTools = Tools.getLocaleTools();
    Locale lLocale = lLocaleTools.getLocaleFromCountryCode(lGermany.getCountry());
    assertEquals("DE", lLocale.getCountry());
    assertEquals("DEU", lLocale.getISO3Country());
    lLocale = lLocaleTools.getLocaleFromCountryCode(lGermany.getISO3Country());
    assertEquals("DE", lLocale.getCountry());
    assertEquals("DEU", lLocale.getISO3Country());

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
