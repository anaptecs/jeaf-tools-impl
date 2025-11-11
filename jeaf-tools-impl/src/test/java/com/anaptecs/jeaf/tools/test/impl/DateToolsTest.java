/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.date.DateFormatStyle;
import com.anaptecs.jeaf.tools.api.date.DateTools;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import com.anaptecs.jeaf.xfun.api.info.JavaRelease;
import org.junit.jupiter.api.Test;

public class DateToolsTest {
  /** Constants for 1 day, 1 hour, 1 minute and 1 second in milliseconds. */
  private static final long ONE_DAY = 24 * 60 * 60 * 1000;

  private static final long ONE_HOUR = 60 * 60 * 1000;

  private static final long ONE_MINUTE = 60 * 1000;

  private static final long ONE_SECOND = 1000;

  @Test
  public void testDateConversions( ) throws ParseException {
    DateTools lDateTools = DateTools.getDateTools();
    Date lDate = lDateTools.toDate(2000, 1, 1);
    GregorianCalendar lCalendar = new GregorianCalendar();
    lCalendar.setTime(lDate);
    assertEquals("2000-01-01", lDateTools.toDateString(lDate));
    assertEquals(2000, lCalendar.get(Calendar.YEAR));
    assertEquals(Calendar.JANUARY, lCalendar.get(Calendar.MONTH));
    assertEquals(1, lCalendar.get(Calendar.DAY_OF_MONTH));
    assertEquals(0, lCalendar.get(Calendar.HOUR));
    assertEquals(0, lCalendar.get(Calendar.MINUTE));
    assertEquals(0, lCalendar.get(Calendar.SECOND));
    assertEquals(0, lCalendar.get(Calendar.MILLISECOND));

    lDate = lDateTools.toDate(2000, 1, 1, 13, 47, 22);
    assertEquals("2000-01-01 13:47:22.000", lDateTools.toTimestampString(lDate));

    lDate = lDateTools.toDate(2000, 1, 1, 13, 47, 22, 998);
    assertEquals("2000-01-01 13:47:22.998", lDateTools.toTimestampString(lDate));

    lDate = lDateTools.toDate(2000, -1, 1, 13, 47, 22, 1024);
    assertEquals("1999-11-01 13:47:23.024", lDateTools.toTimestampString(lDate));

    // Convert string to date
    lDate = lDateTools.toDate("2002-13-01 00:00:00.000");
    assertEquals("2003-01-01 00:00:00.000", lDateTools.toTimestampString(lDate));

    // Test exception handling.
    try {
      lDateTools.toDate("2002-03-01 00.00:00");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.INVALID_TIMESTAMP_FORMAT, e.getErrorCode());
    }

    // Test null handling
    try {
      lDateTools.toDate((String) null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lDateTools.toDate((Calendar) null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lDateTools.toDate((FileTime) null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    lDate = lDateTools.toDate(2000, 1, 1, 13, 47, 22, 998);
    FileTime lFileTime = FileTime.fromMillis(lDate.getTime());
    Date lNewDate = lDateTools.toDate(lFileTime);
    assertEquals(lDate, lNewDate);
    assertEquals("2000-01-01 13:47:22.998", lDateTools.toTimestampString(lNewDate));

    assertTrue(lDateTools.isDateValid("2002-03-01 00:00:00.000"));
    assertTrue(lDateTools.isDateValid("1999-11-01 13:47:23.024"));
    assertFalse(lDateTools.isDateValid("1999-11-01 13:47:23,024"));
    assertFalse(lDateTools.isDateValid("12.12.2011 13:47:23,024"));
    assertFalse(lDateTools.isDateValid(""));
    assertFalse(lDateTools.isDateValid(null));
  }

  @Test
  public void testCalendarConversions( ) {
    DateTools lDateTools = DateTools.getDateTools();
    Date lDate = lDateTools.toDate(2000, 1, 1);

    Calendar lCalendar = lDateTools.toCalendar(lDate);
    assertEquals("2000-01-01 00:00:00.000", lDateTools.toTimestampString(lCalendar));
    lDate = lDateTools.toDate(2021, 1, 1, 13, 47, 22, 998);
    lCalendar = lDateTools.toCalendar(lDate);
    assertEquals("2021-01-01 13:47:22.998", lDateTools.toTimestampString(lCalendar));

    FileTime lFileTime = FileTime.fromMillis(lDate.getTime());
    lCalendar = lDateTools.toCalendar(lFileTime);
    assertEquals("2021-01-01 13:47:22.998", lDateTools.toTimestampString(lCalendar));

    // Convert string to date
    lCalendar = lDateTools.toCalendar("2002-13-01 00:00:00.000");
    assertEquals("2003-01-01 00:00:00.000", lDateTools.toTimestampString(lCalendar));

    // Test exception handling.
    try {
      lDateTools.toCalendar("2002-03-01 00.00:00");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.INVALID_TIMESTAMP_FORMAT, e.getErrorCode());
    }

    // Test null handling
    try {
      lDateTools.toCalendar((String) null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lDateTools.toCalendar((Date) null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lDateTools.toCalendar((FileTime) null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testCreateCalendar( ) {
    DateTools lDateTools = DateTools.getDateTools();
    long lNow = System.currentTimeMillis();
    Calendar lCalendar = lDateTools.newCalendar();
    assertTrue(lCalendar.getTimeInMillis() >= lNow);
    assertTrue(lCalendar.getTimeInMillis() - lNow < 10);
    assertEquals(GregorianCalendar.class, lCalendar.getClass());

    Date lDate = lDateTools.toDate(2000, 1, 1, 13, 47, 22);
    lCalendar = lDateTools.newCalendar(lDate.getTime());
    assertEquals(lDateTools.toCalendar(lDate), lCalendar);
  }

  /**
   * Test case test the calculation methods of JEAF's Date Tools.
   */
  @Test
  public void testDateToolsCalculation( ) {
    // Prepare test data.
    long lNow = System.currentTimeMillis() / 1000 * 1000;
    Date lOneDayAhead = new Date(lNow + ONE_DAY + 10 * ONE_SECOND);
    Date lManyDaysAhead = new Date(lNow + 70 * ONE_DAY + 10 * ONE_SECOND);
    Date lOneMinuteAhead = new Date(lNow + ONE_MINUTE + 10 * ONE_SECOND);
    Date lSecondsAhead = new Date(lNow + 5 * ONE_SECOND);
    Date lOneDayBehind = new Date(lNow - ONE_DAY - 10 * ONE_SECOND);
    Date lOneHourBehind = new Date(lNow - ONE_HOUR - 10 * ONE_SECOND);
    Date lOneMinuteBehind = new Date(lNow - ONE_MINUTE - 10 * ONE_SECOND);
    Date lSecondsBehind = new Date(lNow - 5 * ONE_SECOND);
    Date lManyDaysBehind = new Date(lNow - 32 * ONE_DAY - 10 * ONE_SECOND);

    //
    // Test calculation with java.util.Date
    //

    // Test calculation in days.
    DateTools lDateTools = DateTools.getDateTools();
    assertEquals(1, lDateTools.calculateTimeDifferenceInDays(lOneDayAhead), "Date not one day ahead.");
    assertEquals(70, lDateTools.calculateTimeDifferenceInDays(lManyDaysAhead), "Date not 70 days ahead.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInDays(lOneMinuteAhead), "Date not zero days ahead.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInDays(lSecondsAhead), "Date not zero days ahead.");
    assertEquals(-1, lDateTools.calculateTimeDifferenceInDays(lOneDayBehind), "Date not one day behind.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInDays(lOneHourBehind), "Date not zero days behind.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInDays(lOneMinuteBehind), "Date not zero days behind.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInDays(lSecondsBehind), "Date not zero days behind.");
    assertEquals(-32, lDateTools.calculateTimeDifferenceInDays(lManyDaysBehind), "Date not zero days behind.");

    // Test calculation in hours.
    assertEquals(24, lDateTools.calculateTimeDifferenceInHours(lOneDayAhead), "Date not one day ahead.");
    assertEquals(1680, lDateTools.calculateTimeDifferenceInHours(lManyDaysAhead), "Date not 70 day ahead.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInHours(lOneMinuteAhead), "Date not zero days ahead.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInHours(lSecondsAhead), "Date not zero days ahead.");
    assertEquals(-24, lDateTools.calculateTimeDifferenceInHours(lOneDayBehind), "Date not one day behind.");
    assertEquals(-1, lDateTools.calculateTimeDifferenceInHours(lOneHourBehind), "Date not zero days behind.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInHours(lOneMinuteBehind), "Date not zero days behind.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInHours(lSecondsBehind), "Date not zero days behind.");
    assertEquals(-768, lDateTools.calculateTimeDifferenceInHours(lManyDaysBehind), "Date not zero days behind.");

    // Test calculation in minutes.
    assertEquals(1440, lDateTools.calculateTimeDifferenceInMinutes(lOneDayAhead), "Date not one day ahead.");
    assertEquals(100800, lDateTools.calculateTimeDifferenceInMinutes(lManyDaysAhead), "Date not 70 day ahead.");
    assertEquals(1, lDateTools.calculateTimeDifferenceInMinutes(lOneMinuteAhead), "Date not zero days ahead.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInMinutes(lSecondsBehind), "Date not zero days behind.");
    assertEquals(-1440, lDateTools.calculateTimeDifferenceInMinutes(lOneDayBehind), "Date not one day behind.");
    assertEquals(-60, lDateTools.calculateTimeDifferenceInMinutes(lOneHourBehind), "Date not zero days behind.");
    assertEquals(-1, lDateTools.calculateTimeDifferenceInMinutes(lOneMinuteBehind), "Date not zero days behind.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInMinutes(lSecondsBehind), "Date not zero days behind.");
    assertEquals(-46080, lDateTools.calculateTimeDifferenceInMinutes(lManyDaysBehind), "Date not zero days behind.");

    // Test calculation in minutes.
    assertTrue(1440 * 60 <= lDateTools.calculateTimeDifferenceInSeconds(lOneDayAhead), "Date not one day ahead.");
    assertTrue(1440 * 60 + 10 > lDateTools.calculateTimeDifferenceInSeconds(lOneDayAhead), "Date not one day ahead.");
    assertTrue(100800 * 60 < lDateTools.calculateTimeDifferenceInSeconds(lManyDaysAhead), "Date not 70 day ahead.");
    assertTrue(100800 * 60 + 10 > lDateTools.calculateTimeDifferenceInSeconds(lManyDaysAhead),
        "Date not 70 day ahead.");

    assertTrue(1 * 60 < lDateTools.calculateTimeDifferenceInSeconds(lOneMinuteAhead), "Date not zero days ahead.");
    assertTrue(1 * 60 + 10 > lDateTools.calculateTimeDifferenceInSeconds(lOneMinuteAhead), "Date not zero days ahead.");
    assertTrue(-6 < lDateTools.calculateTimeDifferenceInSeconds(lSecondsBehind), "Date not zero days behind.");
    assertTrue(0 > lDateTools.calculateTimeDifferenceInSeconds(lSecondsBehind), "Date not zero days behind.");
    assertTrue(-1440 * 60 > lDateTools.calculateTimeDifferenceInSeconds(lOneDayBehind), "Date not one day behind.");
    assertTrue(-1440 * 60 - 11 < lDateTools.calculateTimeDifferenceInSeconds(lOneDayBehind),
        "Date not one day behind.");
    assertTrue(-60 * 60 > lDateTools.calculateTimeDifferenceInSeconds(lOneHourBehind), "Date not zero days behind.");
    assertTrue(-60 * 60 - 11 < lDateTools.calculateTimeDifferenceInSeconds(lOneHourBehind),
        "Date not zero days behind.");

    assertTrue(-1 * 60 > lDateTools.calculateTimeDifferenceInSeconds(lOneMinuteBehind), "Date not zero days behind.");

    assertTrue(0 > lDateTools.calculateTimeDifferenceInSeconds(lSecondsBehind), "Date not zero days behind.");
    assertTrue(-11 < lDateTools.calculateTimeDifferenceInSeconds(lSecondsBehind), "Date not zero days behind.");

    assertTrue(-46080 * 60 > lDateTools.calculateTimeDifferenceInSeconds(lManyDaysBehind),
        "Date not zero days behind.");
    assertTrue(-46080 * 60 - 11 < lDateTools.calculateTimeDifferenceInSeconds(lManyDaysBehind),
        "Date not zero days behind.");

    //
    // Test calculation with java.util.Calendar
    //

    // Test calculation in days.
    assertEquals(1, lDateTools.calculateTimeDifferenceInDays(lDateTools.toCalendar(lOneDayAhead)),
        "Date not zero days behind.");
    assertEquals(70, lDateTools.calculateTimeDifferenceInDays(lDateTools.toCalendar(lManyDaysAhead)),
        "Date not 70 days ahead.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInDays(lDateTools.toCalendar(lOneMinuteAhead)),
        "Date not zero days ahead.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInDays(lDateTools.toCalendar(lSecondsAhead)),
        "Date not zero days ahead.");
    assertEquals(-1, lDateTools.calculateTimeDifferenceInDays(lDateTools.toCalendar(lOneDayBehind)),
        "Date not one day behind.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInDays(lDateTools.toCalendar(lOneHourBehind)),
        "Date not zero days behind.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInDays(lDateTools.toCalendar(lOneMinuteBehind)),
        "Date not zero days behind.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInDays(lDateTools.toCalendar(lSecondsBehind)),
        "Date not zero days behind.");
    assertEquals(-32, lDateTools.calculateTimeDifferenceInDays(lDateTools.toCalendar(lManyDaysBehind)),
        "Date not zero days behind.");

    // Test calculation in hours.
    assertEquals(24, lDateTools.calculateTimeDifferenceInHours(lDateTools.toCalendar(lOneDayAhead)),
        "Date not one day ahead.");
    assertEquals(1680, lDateTools.calculateTimeDifferenceInHours(lDateTools.toCalendar(lManyDaysAhead)),
        "Date not 70 days ahead.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInHours(lDateTools.toCalendar(lOneMinuteAhead)),
        "Date not zero days ahead.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInHours(lDateTools.toCalendar(lSecondsAhead)),
        "Date not zero days ahead.");
    assertEquals(-24, lDateTools.calculateTimeDifferenceInHours(lDateTools.toCalendar(lOneDayBehind)),
        "Date not one day behind.");
    assertEquals(-1, lDateTools.calculateTimeDifferenceInHours(lDateTools.toCalendar(lOneHourBehind)),
        "Date not zero days behind.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInHours(lDateTools.toCalendar(lOneMinuteBehind)),
        "Date not zero days behind.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInHours(lDateTools.toCalendar(lSecondsBehind)),
        "Date not zero days behind.");
    assertEquals(-768, lDateTools.calculateTimeDifferenceInHours(lDateTools.toCalendar(lManyDaysBehind)),
        "Date not zero days behind.");

    // Test calculation in minutes.
    assertEquals(1440, lDateTools.calculateTimeDifferenceInMinutes(lDateTools.toCalendar(lOneDayAhead)),
        "Date not one day ahead.");
    assertEquals(100800, lDateTools.calculateTimeDifferenceInMinutes(lDateTools.toCalendar(lManyDaysAhead)),
        "Date not 70 day ahead.");
    assertEquals(1, lDateTools.calculateTimeDifferenceInMinutes(lDateTools.toCalendar(lOneMinuteAhead)),
        "Date not zero days ahead.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInMinutes(lDateTools.toCalendar(lSecondsBehind)),
        "Date not zero days behind.");
    assertEquals(-1440, lDateTools.calculateTimeDifferenceInMinutes(lDateTools.toCalendar(lOneDayBehind)),
        "Date not one day behind.");
    assertEquals(-60, lDateTools.calculateTimeDifferenceInMinutes(lDateTools.toCalendar(lOneHourBehind)),
        "Date not zero days behind.");
    assertEquals(-1, lDateTools.calculateTimeDifferenceInMinutes(lDateTools.toCalendar(lOneMinuteBehind)),
        "Date not zero days behind.");
    assertEquals(0, lDateTools.calculateTimeDifferenceInMinutes(lDateTools.toCalendar(lSecondsBehind)),
        "Date not zero days behind.");
    assertEquals(-46080, lDateTools.calculateTimeDifferenceInMinutes(lDateTools.toCalendar(lManyDaysBehind)),
        "Date not zero days behind.");

    assertTrue(1440 * 60 <= lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lOneDayAhead)),
        "Date not one day ahead.");
    assertTrue(1440 * 60 + 10 > lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lOneDayAhead)),
        "Date not one day ahead.");
    assertTrue(100800 * 60 < lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lManyDaysAhead)),
        "Date not 70 day ahead.");
    assertTrue(100800 * 60 + 10 > lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lManyDaysAhead)),
        "Date not 70 day ahead.");

    assertTrue(1 * 60 < lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lOneMinuteAhead)),
        "Date not zero days ahead.");
    assertTrue(1 * 60 + 10 > lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lOneMinuteAhead)),
        "Date not zero days ahead.");

    assertTrue(-6 < lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lSecondsBehind)),
        "Date not zero days behind.");
    assertTrue(0 > lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lSecondsBehind)),
        "Date not zero days behind.");

    assertTrue(-1440 * 60 > lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lOneDayBehind)),
        "Date not one day behind.");
    assertTrue(-1440 * 60 - 11 < lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lOneDayBehind)),
        "Date not one day behind.");

    assertTrue(-60 * 60 > lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lOneHourBehind)),
        "Date not zero days behind.");

    assertTrue(-60 * 60 - 11 < lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lOneHourBehind)),
        "Date not zero days behind.");

    assertTrue(-1 * 60 > lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lOneMinuteBehind)),
        "Date not zero days behind.");

    assertTrue(0 > lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lSecondsBehind)),
        "Date not zero days behind.");
    assertTrue(-11 < lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lSecondsBehind)),
        "Date not zero days behind.");

    assertTrue(-46080 * 60 > lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lManyDaysBehind)),
        "Date not zero days behind.");
    assertTrue(-46080 * 60 - 11 < lDateTools.calculateTimeDifferenceInSeconds(lDateTools.toCalendar(lManyDaysBehind)),
        "Date not zero days behind.");
  }

  @Test
  public void testAddToCalculations( ) {
    DateTools lDateTools = DateTools.getDateTools();

    Calendar lCalendar = lDateTools.newCalendar();
    Calendar lNewCalendar = lDateTools.addDaysToActualCalendar(5);
    lCalendar.add(Calendar.DAY_OF_MONTH, 5);
    assertEquals(lCalendar.get(Calendar.DAY_OF_MONTH), lNewCalendar.get(Calendar.DAY_OF_MONTH));

    Date lDate = lDateTools.addDaysToActualDate(-2);
    lCalendar = lDateTools.newCalendar();
    lCalendar.add(Calendar.DAY_OF_MONTH, -2);
    assertEquals(lDateTools.toDateString(lCalendar), lDateTools.toDateString(lDate));
  }

  @Test
  public void testStringConversions( ) {
    DateTools lDateTools = DateTools.getDateTools();

    Date lDate = lDateTools.toDate(2020, 12, 23, 13, 17, 05, 199);
    Calendar lCalendar = lDateTools.toCalendar(lDate);

    // Timestamps
    assertEquals("2020-12-23 13:17:05.199", lDateTools.toTimestampString(lDate));
    assertEquals("2020-12-23 13:17:05.199", lDateTools.toTimestampString(lCalendar));

    // Date only
    assertEquals("2020-12-23", lDateTools.toDateString(lDate));
    assertEquals("2020-12-23", lDateTools.toDateString(lCalendar));

    assertEquals("2020-12-23 13:17:05", lDateTools.toDateTimeString(lDate));
    assertEquals("2020-12-23 13:17:05", lDateTools.toDateTimeString(lCalendar));

    assertEquals("2020-12-23 13:17", lDateTools.toShortDateTimeString(lDate));
    assertEquals("2020-12-23 13:17", lDateTools.toShortDateTimeString(lCalendar));

  }

  @Test
  public void testLocalizedDateTimeString( ) {
    DateTools lDateTools = DateTools.getDateTools();

    Date lDate = lDateTools.toDate(2020, 12, 23, 13, 17, 05, 199);
    Calendar lCalendar = lDateTools.toCalendar(lDate);

    Locale lCurrentLocale = XFun.getLocaleProvider().getCurrentLocale();

    // Test handling of null values
    assertEquals("", lDateTools.toLocalizedDateTimeString((Date) null, Locale.GERMANY, DateFormatStyle.SHORT));
    assertEquals("", lDateTools.toLocalizedDateTimeString((Calendar) null, Locale.GERMANY, DateFormatStyle.SHORT));

    // Test short representation
    String lDefaultExpected = lDateTools.toLocalizedDateTimeString(lDate, lCurrentLocale, DateFormatStyle.SHORT);
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateTimeString(lDate, DateFormatStyle.SHORT));
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateTimeString(lCalendar, DateFormatStyle.SHORT));

    // Unfortunately date behavior changed with Java 11
    if (XFun.getInfoProvider().getJavaRuntimeEnvironment().isEqualOrHigher(JavaRelease.JAVA_21)) {
      assertEquals("23.12.20, 13:17",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.GERMANY, DateFormatStyle.SHORT));
      assertEquals("23.12.20, 13:17",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.GERMANY, DateFormatStyle.SHORT));
      assertEquals("12/23/20, 1:17 PM", lDateTools.toLocalizedDateTimeString(lDate, Locale.US, DateFormatStyle.SHORT));
      assertEquals("12/23/20, 1:17 PM",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.US, DateFormatStyle.SHORT));
      assertEquals("23/12/2020 13:17",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.FRANCE, DateFormatStyle.SHORT));
      assertEquals("23/12/2020 13:17",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.FRANCE, DateFormatStyle.SHORT));
    }
    // Unfortunately date behavior changed with Java 11
    else if (XFun.getInfoProvider().getJavaRuntimeEnvironment().isEqualOrHigher(JavaRelease.JAVA_11)) {
      assertEquals("23.12.20, 13:17",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.GERMANY, DateFormatStyle.SHORT));
      assertEquals("23.12.20, 13:17",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.GERMANY, DateFormatStyle.SHORT));
      assertEquals("12/23/20, 1:17 PM", lDateTools.toLocalizedDateTimeString(lDate, Locale.US, DateFormatStyle.SHORT));
      assertEquals("12/23/20, 1:17 PM",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.US, DateFormatStyle.SHORT));
      assertEquals("23/12/2020 13:17",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.FRANCE, DateFormatStyle.SHORT));
      assertEquals("23/12/2020 13:17",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.FRANCE, DateFormatStyle.SHORT));
    }
    // Before Java 11 this is expected.
    else {
      assertEquals("23.12.20 13:17",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.GERMANY, DateFormatStyle.SHORT));
      assertEquals("23.12.20 13:17",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.GERMANY, DateFormatStyle.SHORT));
      assertEquals("12/23/20 1:17 PM", lDateTools.toLocalizedDateTimeString(lDate, Locale.US, DateFormatStyle.SHORT));
      assertEquals("12/23/20 1:17 PM",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.US, DateFormatStyle.SHORT));
      assertEquals("23/12/20 13:17", lDateTools.toLocalizedDateTimeString(lDate, Locale.FRANCE, DateFormatStyle.SHORT));
      assertEquals("23/12/20 13:17",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.FRANCE, DateFormatStyle.SHORT));
    }

    // Test medium representation
    lDefaultExpected = lDateTools.toLocalizedDateTimeString(lDate, lCurrentLocale, DateFormatStyle.MEDIUM);
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateTimeString(lDate, DateFormatStyle.MEDIUM));
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateTimeString(lCalendar, DateFormatStyle.MEDIUM));

    if (XFun.getInfoProvider().getJavaRuntimeEnvironment().isEqualOrHigher(JavaRelease.JAVA_21)) {
      assertEquals("23.12.2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.GERMANY, DateFormatStyle.MEDIUM));
      assertEquals("23.12.2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.GERMANY, DateFormatStyle.MEDIUM));
      assertEquals("Dec 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.US, DateFormatStyle.MEDIUM));
      assertEquals("Dec 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.US, DateFormatStyle.MEDIUM));
      assertEquals("23 déc. 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.FRANCE, DateFormatStyle.MEDIUM));
      assertEquals("23 déc. 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.FRANCE, DateFormatStyle.MEDIUM));
    }
    else if (XFun.getInfoProvider().getJavaRuntimeEnvironment().isEqualOrHigher(JavaRelease.JAVA_17)) {
      assertEquals("23.12.2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.GERMANY, DateFormatStyle.MEDIUM));
      assertEquals("23.12.2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.GERMANY, DateFormatStyle.MEDIUM));
      assertEquals("Dec 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.US, DateFormatStyle.MEDIUM));
      assertEquals("Dec 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.US, DateFormatStyle.MEDIUM));
      assertEquals("23 déc. 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.FRANCE, DateFormatStyle.MEDIUM));
      assertEquals("23 déc. 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.FRANCE, DateFormatStyle.MEDIUM));
    }
    else if (XFun.getInfoProvider().getJavaRuntimeEnvironment().isEqualOrHigher(JavaRelease.JAVA_11)) {
      assertEquals("23.12.2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.GERMANY, DateFormatStyle.MEDIUM));
      assertEquals("23.12.2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.GERMANY, DateFormatStyle.MEDIUM));
      assertEquals("Dec 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.US, DateFormatStyle.MEDIUM));
      assertEquals("Dec 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.US, DateFormatStyle.MEDIUM));
      assertEquals("23 déc. 2020 à 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.FRANCE, DateFormatStyle.MEDIUM));
      assertEquals("23 déc. 2020 à 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.FRANCE, DateFormatStyle.MEDIUM));
    }
    else {
      assertEquals("23.12.2020 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.GERMANY, DateFormatStyle.MEDIUM));
      assertEquals("23.12.2020 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.GERMANY, DateFormatStyle.MEDIUM));
      assertEquals("Dec 23, 2020 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.US, DateFormatStyle.MEDIUM));
      assertEquals("Dec 23, 2020 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.US, DateFormatStyle.MEDIUM));
      assertEquals("23 déc. 2020 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.FRANCE, DateFormatStyle.MEDIUM));
      assertEquals("23 déc. 2020 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.FRANCE, DateFormatStyle.MEDIUM));
    }

    // Test long representation
    lDefaultExpected = lDateTools.toLocalizedDateTimeString(lDate, lCurrentLocale, DateFormatStyle.LONG);
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateTimeString(lDate, DateFormatStyle.LONG));
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateTimeString(lCalendar, DateFormatStyle.LONG));
    if (XFun.getInfoProvider().getJavaRuntimeEnvironment().isEqualOrHigher(JavaRelease.JAVA_21)) {
      assertEquals("23. Dezember 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.GERMANY, DateFormatStyle.LONG));
      assertEquals("23. Dezember 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.GERMANY, DateFormatStyle.LONG));
      assertEquals("December 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.US, DateFormatStyle.LONG));
      assertEquals("December 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.US, DateFormatStyle.LONG));
      assertEquals("23 décembre 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.FRANCE, DateFormatStyle.LONG));
      assertEquals("23 décembre 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.FRANCE, DateFormatStyle.LONG));
    }
    else if (XFun.getInfoProvider().getJavaRuntimeEnvironment().isEqualOrHigher(JavaRelease.JAVA_17)) {
      assertEquals("23. Dezember 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.GERMANY, DateFormatStyle.LONG));
      assertEquals("23. Dezember 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.GERMANY, DateFormatStyle.LONG));
      assertEquals("December 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.US, DateFormatStyle.LONG));
      assertEquals("December 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.US, DateFormatStyle.LONG));
      assertEquals("23 décembre 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.FRANCE, DateFormatStyle.LONG));
      assertEquals("23 décembre 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.FRANCE, DateFormatStyle.LONG));
    }
    else if (XFun.getInfoProvider().getJavaRuntimeEnvironment().isEqualOrHigher(JavaRelease.JAVA_11)) {
      assertEquals("23. Dezember 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.GERMANY, DateFormatStyle.LONG));
      assertEquals("23. Dezember 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.GERMANY, DateFormatStyle.LONG));
      assertEquals("December 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.US, DateFormatStyle.LONG));
      assertEquals("December 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.US, DateFormatStyle.LONG));
      assertEquals("23 décembre 2020 à 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.FRANCE, DateFormatStyle.LONG));
      assertEquals("23 décembre 2020 à 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.FRANCE, DateFormatStyle.LONG));
    }
    else {
      assertEquals("23. Dezember 2020 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.GERMANY, DateFormatStyle.LONG));
      assertEquals("23. Dezember 2020 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.GERMANY, DateFormatStyle.LONG));
      assertEquals("December 23, 2020 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.US, DateFormatStyle.LONG));
      assertEquals("December 23, 2020 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.US, DateFormatStyle.LONG));
      assertEquals("23 décembre 2020 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.FRANCE, DateFormatStyle.LONG));
      assertEquals("23 décembre 2020 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.FRANCE, DateFormatStyle.LONG));
    }

    // Test full representation
    lDefaultExpected = lDateTools.toLocalizedDateTimeString(lDate, lCurrentLocale, DateFormatStyle.FULL);
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateTimeString(lDate, DateFormatStyle.FULL));
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateTimeString(lCalendar, DateFormatStyle.FULL));

    if (XFun.getInfoProvider().getJavaRuntimeEnvironment().isEqualOrHigher(JavaRelease.JAVA_21)) {
      assertEquals("Mittwoch, 23. Dezember 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.GERMANY, DateFormatStyle.FULL));
      assertEquals("Mittwoch, 23. Dezember 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.GERMANY, DateFormatStyle.FULL));
      assertEquals("Wednesday, December 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.US, DateFormatStyle.FULL));
      assertEquals("Wednesday, December 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.US, DateFormatStyle.FULL));
      assertEquals("mercredi 23 décembre 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.FRANCE, DateFormatStyle.FULL));
      assertEquals("mercredi 23 décembre 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.FRANCE, DateFormatStyle.FULL));
    }
    else if (XFun.getInfoProvider().getJavaRuntimeEnvironment().isEqualOrHigher(JavaRelease.JAVA_17)) {
      assertEquals("Mittwoch, 23. Dezember 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.GERMANY, DateFormatStyle.FULL));
      assertEquals("Mittwoch, 23. Dezember 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.GERMANY, DateFormatStyle.FULL));
      assertEquals("Wednesday, December 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.US, DateFormatStyle.FULL));
      assertEquals("Wednesday, December 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.US, DateFormatStyle.FULL));
      assertEquals("mercredi 23 décembre 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.FRANCE, DateFormatStyle.FULL));
      assertEquals("mercredi 23 décembre 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.FRANCE, DateFormatStyle.FULL));
    }
    else if (XFun.getInfoProvider().getJavaRuntimeEnvironment().isEqualOrHigher(JavaRelease.JAVA_11)) {
      assertEquals("Mittwoch, 23. Dezember 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.GERMANY, DateFormatStyle.FULL));
      assertEquals("Mittwoch, 23. Dezember 2020, 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.GERMANY, DateFormatStyle.FULL));
      assertEquals("Wednesday, December 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.US, DateFormatStyle.FULL));
      assertEquals("Wednesday, December 23, 2020, 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.US, DateFormatStyle.FULL));
      assertEquals("mercredi 23 décembre 2020 à 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.FRANCE, DateFormatStyle.FULL));
      assertEquals("mercredi 23 décembre 2020 à 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.FRANCE, DateFormatStyle.FULL));
    }
    else {
      assertEquals("Mittwoch, 23. Dezember 2020 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.GERMANY, DateFormatStyle.FULL));
      assertEquals("Mittwoch, 23. Dezember 2020 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.GERMANY, DateFormatStyle.FULL));
      assertEquals("Wednesday, December 23, 2020 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.US, DateFormatStyle.FULL));
      assertEquals("Wednesday, December 23, 2020 1:17:05 PM",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.US, DateFormatStyle.FULL));
      assertEquals("mercredi 23 décembre 2020 13:17:05",
          lDateTools.toLocalizedDateTimeString(lDate, Locale.FRANCE, DateFormatStyle.FULL));
      assertEquals("mercredi 23 décembre 2020 13:17:05",
          lDateTools.toLocalizedDateTimeString(lCalendar, Locale.FRANCE, DateFormatStyle.FULL));
    }
  }

  @Test
  public void testLocalizedDateOnlyString( ) {
    DateTools lDateTools = DateTools.getDateTools();

    Date lDate = lDateTools.toDate(2020, 12, 23, 13, 17, 05, 199);
    Calendar lCalendar = lDateTools.toCalendar(lDate);

    Locale lCurrentLocale = XFun.getLocaleProvider().getCurrentLocale();

    // Test handling of null values
    assertEquals("", lDateTools.toLocalizedDateOnlyString((Date) null, Locale.GERMANY, DateFormatStyle.SHORT));
    assertEquals("", lDateTools.toLocalizedDateOnlyString((Calendar) null, Locale.GERMANY, DateFormatStyle.SHORT));

    // Test short representation
    String lDefaultExpected = lDateTools.toLocalizedDateOnlyString(lDate, lCurrentLocale, DateFormatStyle.SHORT);
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateOnlyString(lDate, DateFormatStyle.SHORT));
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateOnlyString(lCalendar, DateFormatStyle.SHORT));

    assertEquals("23.12.20", lDateTools.toLocalizedDateOnlyString(lDate, Locale.GERMANY, DateFormatStyle.SHORT));
    assertEquals("23.12.20", lDateTools.toLocalizedDateOnlyString(lCalendar, Locale.GERMANY, DateFormatStyle.SHORT));
    assertEquals("12/23/20", lDateTools.toLocalizedDateOnlyString(lDate, Locale.US, DateFormatStyle.SHORT));
    assertEquals("12/23/20", lDateTools.toLocalizedDateOnlyString(lCalendar, Locale.US, DateFormatStyle.SHORT));

    // Unfortunately date behavior for France changed with Java 11
    if (XFun.getInfoProvider().getJavaRuntimeEnvironment().isEqualOrHigher(JavaRelease.JAVA_11)) {
      assertEquals("23/12/2020", lDateTools.toLocalizedDateOnlyString(lDate, Locale.FRANCE, DateFormatStyle.SHORT));
      assertEquals("23/12/2020", lDateTools.toLocalizedDateOnlyString(lCalendar, Locale.FRANCE, DateFormatStyle.SHORT));
    }
    // Before Java 11 this is expected.
    else {
      assertEquals("23/12/20", lDateTools.toLocalizedDateOnlyString(lDate, Locale.FRANCE, DateFormatStyle.SHORT));
      assertEquals("23/12/20", lDateTools.toLocalizedDateOnlyString(lCalendar, Locale.FRANCE, DateFormatStyle.SHORT));
    }

    // Test medium representation
    lDefaultExpected = lDateTools.toLocalizedDateOnlyString(lDate, lCurrentLocale, DateFormatStyle.MEDIUM);
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateOnlyString(lDate, DateFormatStyle.MEDIUM));
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateOnlyString(lCalendar, DateFormatStyle.MEDIUM));
    assertEquals("23.12.2020", lDateTools.toLocalizedDateOnlyString(lDate, Locale.GERMANY, DateFormatStyle.MEDIUM));
    assertEquals("23.12.2020", lDateTools.toLocalizedDateOnlyString(lCalendar, Locale.GERMANY, DateFormatStyle.MEDIUM));
    assertEquals("Dec 23, 2020", lDateTools.toLocalizedDateOnlyString(lDate, Locale.US, DateFormatStyle.MEDIUM));
    assertEquals("Dec 23, 2020", lDateTools.toLocalizedDateOnlyString(lCalendar, Locale.US, DateFormatStyle.MEDIUM));
    assertEquals("23 déc. 2020", lDateTools.toLocalizedDateOnlyString(lDate, Locale.FRANCE, DateFormatStyle.MEDIUM));
    assertEquals("23 déc. 2020",
        lDateTools.toLocalizedDateOnlyString(lCalendar, Locale.FRANCE, DateFormatStyle.MEDIUM));

    // Test long representation
    lDefaultExpected = lDateTools.toLocalizedDateOnlyString(lDate, lCurrentLocale, DateFormatStyle.LONG);
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateOnlyString(lDate, DateFormatStyle.LONG));
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateOnlyString(lCalendar, DateFormatStyle.LONG));
    assertEquals("23. Dezember 2020",
        lDateTools.toLocalizedDateOnlyString(lDate, Locale.GERMANY, DateFormatStyle.LONG));
    assertEquals("23. Dezember 2020",
        lDateTools.toLocalizedDateOnlyString(lCalendar, Locale.GERMANY, DateFormatStyle.LONG));
    assertEquals("December 23, 2020", lDateTools.toLocalizedDateOnlyString(lDate, Locale.US, DateFormatStyle.LONG));
    assertEquals("December 23, 2020", lDateTools.toLocalizedDateOnlyString(lCalendar, Locale.US, DateFormatStyle.LONG));
    assertEquals("23 décembre 2020", lDateTools.toLocalizedDateOnlyString(lDate, Locale.FRANCE, DateFormatStyle.LONG));
    assertEquals("23 décembre 2020",
        lDateTools.toLocalizedDateOnlyString(lCalendar, Locale.FRANCE, DateFormatStyle.LONG));

    // Test full representation
    lDefaultExpected = lDateTools.toLocalizedDateOnlyString(lDate, lCurrentLocale, DateFormatStyle.FULL);
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateOnlyString(lDate, DateFormatStyle.FULL));
    assertEquals(lDefaultExpected, lDateTools.toLocalizedDateOnlyString(lCalendar, DateFormatStyle.FULL));
    assertEquals("Mittwoch, 23. Dezember 2020",
        lDateTools.toLocalizedDateOnlyString(lDate, Locale.GERMANY, DateFormatStyle.FULL));
    assertEquals("Mittwoch, 23. Dezember 2020",
        lDateTools.toLocalizedDateOnlyString(lCalendar, Locale.GERMANY, DateFormatStyle.FULL));
    assertEquals("Wednesday, December 23, 2020",
        lDateTools.toLocalizedDateOnlyString(lDate, Locale.US, DateFormatStyle.FULL));
    assertEquals("Wednesday, December 23, 2020",
        lDateTools.toLocalizedDateOnlyString(lCalendar, Locale.US, DateFormatStyle.FULL));
    assertEquals("mercredi 23 décembre 2020",
        lDateTools.toLocalizedDateOnlyString(lDate, Locale.FRANCE, DateFormatStyle.FULL));
    assertEquals("mercredi 23 décembre 2020",
        lDateTools.toLocalizedDateOnlyString(lCalendar, Locale.FRANCE, DateFormatStyle.FULL));
  }

  @Test
  public void testLocalizedTimeOnlyString( ) {
    DateTools lDateTools = Tools.getDateTools();

    Date lDate = lDateTools.toDate(2020, 12, 23, 13, 17, 05, 199);
    Calendar lCalendar = lDateTools.toCalendar(lDate);

    Locale lCurrentLocale = XFun.getLocaleProvider().getCurrentLocale();

    // Test handling of null values
    assertEquals("", lDateTools.toLocalizedTimeOnlyString((Date) null, Locale.GERMANY, DateFormatStyle.SHORT));
    assertEquals("", lDateTools.toLocalizedTimeOnlyString((Calendar) null, Locale.GERMANY, DateFormatStyle.SHORT));

    // Test short representation
    String lDefaultExpected = lDateTools.toLocalizedTimeOnlyString(lDate, lCurrentLocale, DateFormatStyle.SHORT);
    assertEquals(lDefaultExpected, lDateTools.toLocalizedTimeOnlyString(lDate, DateFormatStyle.SHORT));
    assertEquals(lDefaultExpected, lDateTools.toLocalizedTimeOnlyString(lCalendar, DateFormatStyle.SHORT));

    assertEquals("13:17", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.GERMANY, DateFormatStyle.SHORT));
    assertEquals("13:17", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.GERMANY, DateFormatStyle.SHORT));

    // Starting with Java 21 some not visible special characters changed ;-)
    // https://stackoverflow.com/questions/77225936/java-21-problem-with-dateformat-getdatetimeinstance-formatnew-date
    if (XFun.getInfoProvider().getJavaRuntimeEnvironment().getJavaRelease().isEqualOrHigher(JavaRelease.JAVA_21)) {
      assertEquals("1:17 PM", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.US, DateFormatStyle.SHORT));
      assertEquals("1:17 PM", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.US, DateFormatStyle.SHORT));
    }
    else {
      assertEquals("1:17 PM", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.US, DateFormatStyle.SHORT));
      assertEquals("1:17 PM", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.US, DateFormatStyle.SHORT));
    }
    assertEquals("13:17", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.FRANCE, DateFormatStyle.SHORT));
    assertEquals("13:17", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.FRANCE, DateFormatStyle.SHORT));

    // Test medium representation
    lDefaultExpected = lDateTools.toLocalizedTimeOnlyString(lDate, lCurrentLocale, DateFormatStyle.MEDIUM);
    assertEquals(lDefaultExpected, lDateTools.toLocalizedTimeOnlyString(lDate, DateFormatStyle.MEDIUM));
    assertEquals(lDefaultExpected, lDateTools.toLocalizedTimeOnlyString(lCalendar, DateFormatStyle.MEDIUM));
    assertEquals("13:17:05", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.GERMANY, DateFormatStyle.MEDIUM));
    assertEquals("13:17:05", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.GERMANY, DateFormatStyle.MEDIUM));

    // Starting with Java 21 some not visible special characters changed ;-)
    if (XFun.getInfoProvider().getJavaRuntimeEnvironment().getJavaRelease().isEqualOrHigher(JavaRelease.JAVA_21)) {
      assertEquals("1:17:05 PM", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.US, DateFormatStyle.MEDIUM));
      assertEquals("1:17:05 PM", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.US, DateFormatStyle.MEDIUM));
    }
    else {
      assertEquals("1:17:05 PM", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.US, DateFormatStyle.MEDIUM));
      assertEquals("1:17:05 PM", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.US, DateFormatStyle.MEDIUM));
    }
    assertEquals("13:17:05", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.FRANCE, DateFormatStyle.MEDIUM));
    assertEquals("13:17:05", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.FRANCE, DateFormatStyle.MEDIUM));

    // Test long representation
    lDefaultExpected = lDateTools.toLocalizedTimeOnlyString(lDate, lCurrentLocale, DateFormatStyle.LONG);
    assertEquals(lDefaultExpected, lDateTools.toLocalizedTimeOnlyString(lDate, DateFormatStyle.LONG));
    assertEquals(lDefaultExpected, lDateTools.toLocalizedTimeOnlyString(lCalendar, DateFormatStyle.LONG));
    assertEquals("13:17:05", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.GERMANY, DateFormatStyle.LONG));
    assertEquals("13:17:05", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.GERMANY, DateFormatStyle.LONG));

    // Starting with Java 21 some not visible special characters changed ;-)
    if (XFun.getInfoProvider().getJavaRuntimeEnvironment().getJavaRelease().isEqualOrHigher(JavaRelease.JAVA_21)) {
      assertEquals("1:17:05 PM", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.US, DateFormatStyle.LONG));
      assertEquals("1:17:05 PM", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.US, DateFormatStyle.LONG));
    }
    else {
      assertEquals("1:17:05 PM", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.US, DateFormatStyle.LONG));
      assertEquals("1:17:05 PM", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.US, DateFormatStyle.LONG));
    }
    assertEquals("13:17:05", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.FRANCE, DateFormatStyle.LONG));
    assertEquals("13:17:05", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.FRANCE, DateFormatStyle.LONG));

    // Test full representation
    lDefaultExpected = lDateTools.toLocalizedTimeOnlyString(lDate, lCurrentLocale, DateFormatStyle.FULL);
    assertEquals(lDefaultExpected, lDateTools.toLocalizedTimeOnlyString(lDate, DateFormatStyle.FULL));
    assertEquals(lDefaultExpected, lDateTools.toLocalizedTimeOnlyString(lCalendar, DateFormatStyle.FULL));
    assertEquals("13:17:05", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.GERMANY, DateFormatStyle.FULL));
    assertEquals("13:17:05", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.GERMANY, DateFormatStyle.FULL));

    // Starting with Java 21 some not visible special characters changed ;-)
    if (XFun.getInfoProvider().getJavaRuntimeEnvironment().getJavaRelease().isEqualOrHigher(JavaRelease.JAVA_21)) {
      assertEquals("1:17:05 PM", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.US, DateFormatStyle.FULL));
      assertEquals("1:17:05 PM", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.US, DateFormatStyle.FULL));
    }
    else {
      assertEquals("1:17:05 PM", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.US, DateFormatStyle.FULL));
      assertEquals("1:17:05 PM", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.US, DateFormatStyle.FULL));
    }
    assertEquals("13:17:05", lDateTools.toLocalizedTimeOnlyString(lDate, Locale.FRANCE, DateFormatStyle.FULL));
    assertEquals("13:17:05", lDateTools.toLocalizedTimeOnlyString(lCalendar, Locale.FRANCE, DateFormatStyle.FULL));
  }

  @Test
  public void testStringToDateConversions( ) {
    DateTools lDateTools = DateTools.getDateTools();

    // Check full ISO date time
    Calendar lCalendar = lDateTools.toCalendar("2021-01-16 17:11:55.062", true);
    assertEquals(2021, lCalendar.get(Calendar.YEAR));
    assertEquals(Calendar.JANUARY, lCalendar.get(Calendar.MONTH));
    assertEquals(16, lCalendar.get(Calendar.DAY_OF_MONTH));
    assertEquals(17, lCalendar.get(Calendar.HOUR_OF_DAY));
    assertEquals(11, lCalendar.get(Calendar.MINUTE));
    assertEquals(55, lCalendar.get(Calendar.SECOND));
    assertEquals(62, lCalendar.get(Calendar.MILLISECOND));

    lCalendar = lDateTools.toCalendar("2021-01-16 17:11:55.062", false);
    assertEquals(2021, lCalendar.get(Calendar.YEAR));
    assertEquals(Calendar.JANUARY, lCalendar.get(Calendar.MONTH));
    assertEquals(16, lCalendar.get(Calendar.DAY_OF_MONTH));
    assertEquals(17, lCalendar.get(Calendar.HOUR_OF_DAY));
    assertEquals(11, lCalendar.get(Calendar.MINUTE));
    assertEquals(55, lCalendar.get(Calendar.SECOND));
    assertEquals(62, lCalendar.get(Calendar.MILLISECOND));

    // Check ISO date time
    lCalendar = lDateTools.toCalendar("2021-01-16 17:11:55", false);
    assertEquals(2021, lCalendar.get(Calendar.YEAR));
    assertEquals(Calendar.JANUARY, lCalendar.get(Calendar.MONTH));
    assertEquals(16, lCalendar.get(Calendar.DAY_OF_MONTH));
    assertEquals(17, lCalendar.get(Calendar.HOUR_OF_DAY));
    assertEquals(11, lCalendar.get(Calendar.MINUTE));
    assertEquals(55, lCalendar.get(Calendar.SECOND));
    assertEquals(0, lCalendar.get(Calendar.MILLISECOND));

    // Check short ISO date time
    lCalendar = lDateTools.toCalendar("2021-01-16 17:11", false);
    assertEquals(2021, lCalendar.get(Calendar.YEAR));
    assertEquals(Calendar.JANUARY, lCalendar.get(Calendar.MONTH));
    assertEquals(16, lCalendar.get(Calendar.DAY_OF_MONTH));
    assertEquals(17, lCalendar.get(Calendar.HOUR_OF_DAY));
    assertEquals(11, lCalendar.get(Calendar.MINUTE));
    assertEquals(0, lCalendar.get(Calendar.SECOND));
    assertEquals(0, lCalendar.get(Calendar.MILLISECOND));

    // Check ISO date only
    lCalendar = lDateTools.toCalendar("2021-01-16", false);
    assertEquals(2021, lCalendar.get(Calendar.YEAR));
    assertEquals(Calendar.JANUARY, lCalendar.get(Calendar.MONTH));
    assertEquals(16, lCalendar.get(Calendar.DAY_OF_MONTH));
    assertEquals(0, lCalendar.get(Calendar.HOUR_OF_DAY));
    assertEquals(0, lCalendar.get(Calendar.MINUTE));
    assertEquals(0, lCalendar.get(Calendar.SECOND));
    assertEquals(0, lCalendar.get(Calendar.MILLISECOND));

    lCalendar = lDateTools.toCalendar("2021-15-32", false);
    assertEquals(2022, lCalendar.get(Calendar.YEAR));
    assertEquals(Calendar.APRIL, lCalendar.get(Calendar.MONTH));
    assertEquals(1, lCalendar.get(Calendar.DAY_OF_MONTH));
    assertEquals(0, lCalendar.get(Calendar.HOUR_OF_DAY));
    assertEquals(0, lCalendar.get(Calendar.MINUTE));
    assertEquals(0, lCalendar.get(Calendar.SECOND));
    assertEquals(0, lCalendar.get(Calendar.MILLISECOND));

    // Test error handling.

    try {
      lDateTools.toCalendar("2021-01-16", true);
      fail("Exception expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.INVALID_TIMESTAMP_FORMAT, e.getErrorCode());
    }

    try {
      lDateTools.toCalendar("2021-13", false);
      fail("Exception expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.INVALID_TIMESTAMP_FORMAT, e.getErrorCode());
    }

    try {
      lDateTools.toCalendar("13. Januar 2021", false);
      fail("Exception expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.INVALID_TIMESTAMP_FORMAT, e.getErrorCode());
    }

    try {
      lDateTools.toCalendar(null, false);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testDateToStringConversions( ) {
    DateTools lDateTools = DateTools.getDateTools();

    // Test conversion of full timestamp
    Calendar lCalendar = lDateTools.toCalendar(lDateTools.toDate(2020, 12, 23, 13, 17, 05, 199));
    assertEquals("2020-12-23 13:17:05.199", lDateTools.toString(lCalendar));
    assertEquals("2020-12-23 13:17:05.199", lDateTools.toString(lDateTools.toDate(lCalendar)));

    // Test conversion of full date time
    lCalendar = lDateTools.toCalendar(lDateTools.toDate(2020, 12, 23, 13, 17, 05, 0));
    assertEquals("2020-12-23 13:17:05", lDateTools.toString(lCalendar));
    assertEquals("2020-12-23 13:17:05", lDateTools.toString(lDateTools.toDate(lCalendar)));

    // Test conversion of short date time
    lCalendar = lDateTools.toCalendar(lDateTools.toDate(2020, 12, 23, 13, 17, 0, 0));
    assertEquals("2020-12-23 13:17", lDateTools.toString(lCalendar));
    assertEquals("2020-12-23 13:17", lDateTools.toString(lDateTools.toDate(lCalendar)));

    // Test conversion of short date only
    lCalendar = lDateTools.toCalendar(lDateTools.toDate(2020, 12, 23, 0, 0, 0, 0));
    assertEquals("2020-12-23", lDateTools.toString(lCalendar));
    assertEquals("2020-12-23", lDateTools.toString(lDateTools.toDate(lCalendar)));

    // Test error handling
    try {
      lDateTools.toString((Calendar) null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lDateTools.toString((Date) null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }
}
