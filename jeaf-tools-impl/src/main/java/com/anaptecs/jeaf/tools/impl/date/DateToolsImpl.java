/*
 * anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * Copyright 2004 - 2013 All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.date;

import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.date.DateFormatStyle;
import com.anaptecs.jeaf.tools.api.date.DateTools;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.checks.Assert;
import com.anaptecs.jeaf.xfun.api.checks.Check;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;

/**
 * This class implements the date tools interface for all full featured java environments such as JSE and JEE.
 * 
 * @author JEAF Development Team
 * @version JEAF Release 1.3
 */
@ToolsImplementation(toolsInterface = DateTools.class)
public class DateToolsImpl implements DateTools {

  /** The Constant MILLISECONDS_TO_MINUTES. */
  private static final int MILLISECONDS_TO_SECONDS = 1000;

  /** The Constant MILLISECONDS_TO_MINUTES. */
  private static final int MILLISECONDS_TO_MINUTES = 1000 * 60;

  /** The Constant MILLISECONDS_TO_HOURS. */
  private static final int MILLISECONDS_TO_HOURS = MILLISECONDS_TO_MINUTES * 60;

  /** The Constant MILLISECONDS_TO_DAYS. */
  private static final int MILLISECONDS_TO_DAYS = MILLISECONDS_TO_HOURS * 24;

  private static final String[] DATE_PATTERN_ORDER =
      { DateTools.TIMESTAMP_PATTERN, DateTools.DATE_TIME_PATTERN, SHORT_DATE_TIME_PATTERN, DATE_PATTERN };

  /**
   * Method creates a new calendar object with the current date, time and timezone.
   * 
   * @return {@link Calendar} New calendar object. The method never returns null.
   */
  public Calendar newCalendar( ) {
    return Calendar.getInstance();
  }

  /**
   * Method creates a new calendar wit the passed time / date and the current timezone.
   * 
   * @param pTimeInMillis Date / time definition in milliseconds.
   * @return {@link Calendar} New calendar object. The method never returns null.
   */
  public Calendar newCalendar( long pTimeInMillis ) {
    Calendar lCalendar = this.newCalendar();
    lCalendar.setTimeInMillis(pTimeInMillis);
    return lCalendar;
  }

  /**
   * Method returns a date object the represents the passed date.
   * 
   * @param pYear Year of the date.
   * @param pMonth Month of the date.
   * @param pDay Day of month of the date.
   * @return {@link Date} Created date object. The method never returns null.
   */
  @Override
  public Date toDate( int pYear, int pMonth, int pDay ) {
    return toDate(pYear, pMonth, pDay, 0, 0, 0, 0);
  }

  /**
   * Method returns a date object the represents the passed date.
   * 
   * @param pYear Year of the date.
   * @param pMonth Month of the date.
   * @param pDay Day of month of the date.
   * @param pHour Hour of the date.
   * @param pMiniute the minute
   * @param pSecond Second of the date.
   * @return {@link Date} Created date object. The method never returns null.
   */
  @Override
  public Date toDate( int pYear, int pMonth, int pDay, int pHour, int pMiniute, int pSecond ) {
    return toDate(pYear, pMonth, pDay, pHour, pMiniute, pSecond, 0);
  }

  /**
   * Method returns a date object the represents the passed date.
   * 
   * @param pYear Year of the date.
   * @param pMonth Month of the date.
   * @param pDay Day of month of the date.
   * @param pHour Hour of the date.
   * @param pMinute Minute of the date.
   * @param pSecond Second of the date.
   * @param pMilliseconds Milliseconds of the date.
   * @return {@link Date} Created date object. The method never returns null.
   */
  @Override
  public Date toDate( int pYear, int pMonth, int pDay, int pHour, int pMinute, int pSecond, int pMilliseconds ) {
    StringBuilder lDate = new StringBuilder();
    lDate.append(pYear);
    lDate.append("-");
    lDate.append(pMonth);
    lDate.append("-");
    lDate.append(pDay);
    lDate.append(" ");
    lDate.append(pHour);
    lDate.append(":");
    lDate.append(pMinute);
    lDate.append(":");
    lDate.append(pSecond);
    lDate.append(".");
    lDate.append(pMilliseconds);
    SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(TIMESTAMP_PATTERN);
    try {
      return lSimpleDateFormat.parse(lDate.toString());
    }
    catch (ParseException e) {
      Assert.internalError("Unable to create date from pattern " + TIMESTAMP_PATTERN + ".");
      // Statement will never be reached as method above will always throw an exception.
      return null;
    }
  }

  /**
   * Method returns the Calendar converted to a Date Object.
   * 
   * @param pCalendar Calendar Object which will be converted
   * @return Date the Object the Calendar Object was converted to.
   */
  @Override
  public Date toDate( Calendar pCalendar ) {
    // Check parameter
    Check.checkInvalidParameterNull(pCalendar, "pCalendar");

    return new Date(pCalendar.getTimeInMillis());
  }

  /**
   * Method returns the FileTime converted to a Date object.
   * 
   * @param pFileTime FileTime object which will be converted. The parameter must not be null.
   * @return {@link Date} Object the file time was converted to. The method never returns null.
   */
  @Override
  public Date toDate( FileTime pFileTime ) {
    // Check parameter
    Check.checkInvalidParameterNull(pFileTime, "pFileTime");

    // Convert file time to date using millis and return it.
    return new Date(pFileTime.toMillis());
  }

  /**
   * Converts the given Date Strings resembling the {@link TIMESTAMP_PATTERN}. If it matches this pattern the date will
   * be returned otherwise a Exception will be thrown.
   * 
   * @param pDate the String which will be converted.
   * @return the Date.
   */
  @Override
  public Date toDate( String pDate ) {
    return this.toDate(pDate, true);
  }

  /**
   * Converts the given string into a {@link Date}. If it matches this pattern the date will be returned otherwise a
   * Exception will be thrown.
   * 
   * @param pDate the String which will be converted. The parameter must not be null.
   * @param pStrict If parameter is set to <code>true</code> then the passed string strictly has to match with pattern
   * {@link #TIMESTAMP_PATTERN}. If parameter is set to <code>false</code> the closest fitting pattern out of
   * {@link #TIMESTAMP_PATTERN}, {@link #DATE_TIME_PATTERN}, {@link #SHORT_DATE_TIME_PATTERN}, or {@link #DATE_PATTERN}
   * will be chosen.
   * @return Date object that was created from the string. The method never returns null.
   */
  @Override
  public Date toDate( String pDate, boolean pStrict ) {
    // Check parameter
    Check.checkInvalidParameterNull(pDate, "pDate");

    // Depending on the used mode the amount of tries will be different.
    int lMaxLoops;
    if (pStrict == true) {
      lMaxLoops = 1;
    }
    else {
      lMaxLoops = DATE_PATTERN_ORDER.length;
    }

    // Try to process all date patterns that should be tried.
    Date lDate = null;
    String lLastPattern = null;
    ParseException lLastException = null;
    for (int i = 0; i < lMaxLoops; i++) {
      lLastPattern = DATE_PATTERN_ORDER[i];
      SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(lLastPattern);
      try {
        lDate = lSimpleDateFormat.parse(pDate);
        break;
      }
      catch (ParseException e) {
        lLastException = e;
      }
    }

    // Analyze result. Conversion to date was successful.
    if (lDate != null) {
      return lDate;
    }
    // Unable to convert date into pattern.
    else {
      throw new JEAFSystemException(ToolsMessages.INVALID_TIMESTAMP_FORMAT, lLastException, pDate, lLastPattern);
    }
  }

  /**
   * Method creates a string out of the passed date and thereby respects the current precision of the object. This means
   * e.g. if milliseconds are 0 or seconds are not set (meaning 0) then they are not present in the string
   * representation.
   * 
   * @param pDate Date that should be converted to its string representation. The parameter must not be null.
   * @return {@link String} String representation of the passed date. The method never returns null.
   */
  @Override
  public String toString( Date pDate ) {
    // Check parameter
    Check.checkInvalidParameterNull(pDate, "pDate");

    return this.toString(this.toCalendar(pDate));
  }

  /**
   * Method creates a string out of the passed date and thereby respects the current precision of the object. This means
   * e.g. if milliseconds are 0 or seconds are not set (meaning 0) then they are not present in the string
   * representation.
   * 
   * @param pCalendar Date that should be converted to its string representation. The parameter must not be null.
   * @return {@link String} String representation of the passed date. The method never returns null.
   */
  @Override
  public String toString( Calendar pCalendar ) {
    // Check parameter
    Check.checkInvalidParameterNull(pCalendar, "pCalendar");

    String lFormatPattern = this.getStringFormatPattern(pCalendar);
    return this.toString(pCalendar, lFormatPattern);
  }

  /**
   * Method returns the best matching pattern for a string representation of the passed date. The pattern is chosen
   * based on the precision of the passed object.
   * 
   * @param pCalendar Date for match the best matching string representation patterns should be determined. The
   * parameter must not be null.
   * @return String Best matching string pattern. The method never returns null.
   */
  private String getStringFormatPattern( Calendar pCalendar ) {
    // Check parameter
    Assert.assertNotNull(pCalendar, "pCalendar");

    String lPattern;
    // Check if there are milliseconds
    if (pCalendar.get(Calendar.MILLISECOND) == 0) {
      // Check if there are seconds
      if (pCalendar.get(Calendar.SECOND) == 0) {
        // Check if there are minutes and hours.
        if (pCalendar.get(Calendar.HOUR_OF_DAY) == 0 && pCalendar.get(Calendar.MINUTE) == 0) {
          // It's just a date
          lPattern = DateTools.DATE_PATTERN;
        }
        // Date has short date precision.
        else {
          lPattern = DateTools.SHORT_DATE_TIME_PATTERN;
        }
      }
      // Date has date time precision
      else {
        lPattern = DateTools.DATE_TIME_PATTERN;
      }
    }
    // Date has timestamp precision
    else {
      lPattern = DateTools.TIMESTAMP_PATTERN;
    }
    return lPattern;
  }

  /**
   * Method returns a string representation of the date information of the passed calendar.
   * 
   * @param pDate Date object whose date should be returned as string. The parameter must not be null.
   * @return String String representing the date of the calendar. The returned string is formated as described by the
   * pattern @see {@link #DATE_PATTERN}.
   */
  @Override
  public String toDateString( Date pDate ) {
    return toString(pDate, DATE_PATTERN);
  }

  /**
   * Method returns a string representation of the date information of the passed calendar.
   * 
   * @param pDate Calendar object whose date should be returned as string. The parameter must not be null.
   * @return String String representing the date of the calendar. The returned string is formated as described by the
   * pattern @see {@link #DATE_PATTERN}.
   */
  @Override
  public String toDateString( Calendar pDate ) {
    return toString(pDate, DATE_PATTERN);
  }

  /**
   * Method returns a string representation of the date and time information of the passed calendar.
   * 
   * @param pDate Date object whose date should be returned as string. The parameter must not be null.
   * @return String String representing the date of the calendar. The returned string is formated as described by the
   * pattern @see {@link #DATE_TIME_PATTERN}.
   */
  @Override
  public String toDateTimeString( Date pDate ) {
    return toString(pDate, DATE_TIME_PATTERN);
  }

  /**
   * Method returns a string representation of the date and time information of the passed calendar.
   * 
   * @param pDate Calendar object whose date should be returned as string. The parameter must not be null.
   * @return String String representing the date of the calendar. The returned string is formated as described by the
   * pattern @see {@link #DATE_TIME_PATTERN}.
   */
  @Override
  public String toDateTimeString( Calendar pDate ) {
    return toString(pDate, DATE_TIME_PATTERN);
  }

  /**
   * Method returns a string representation of the date and time information of the passed date.
   * 
   * @param pDate Date object whose date should be returned as string. The parameter must not be null.
   * @return String String representation of the date. The returned string is formated as described by the pattern
   * 
   * @see {@link #SHORT_DATE_TIME_PATTERN}.
   */
  @Override
  public String toShortDateTimeString( Date pDate ) {
    return toString(pDate, SHORT_DATE_TIME_PATTERN);
  }

  /**
   * Method returns a string representation of the date and time information of the passed date.
   * 
   * @param pDate Calendar object whose date should be returned as string. The parameter must not be null.
   * @return String String representation of the date. The returned string is formated as described by the pattern
   * 
   * @see {@link #SHORT_DATE_TIME_PATTERN}.
   */
  @Override
  public String toShortDateTimeString( Calendar pDate ) {
    return toString(pDate, SHORT_DATE_TIME_PATTERN);
  }

  /**
   * Method returns a string representation of the timestamp information of the passed calendar.
   * 
   * @param pDate Date object whose date should be returned as string. The parameter must not be null.
   * @return String String representing the date of the calendar. The returned string is formated as described by the
   * pattern @see {@link #TIMESTAMP_PATTERN}.
   */
  @Override
  public String toTimestampString( Date pDate ) {
    return toString(pDate, TIMESTAMP_PATTERN);
  }

  /**
   * Method returns a string representation of the timestamp information of the passed calendar.
   * 
   * @param pDate Calendar object whose date should be returned as string. The parameter must not be null.
   * @return String String representing the date of the calendar. The returned string is formated as described by the
   * pattern @see {@link #TIMESTAMP_PATTERN}.
   */
  @Override
  public String toTimestampString( Calendar pDate ) {
    return toString(pDate, TIMESTAMP_PATTERN);
  }

  /**
   * Method returns a date-time string representation of the passed date using the provided locale.
   * 
   * @param pDate Date which will be formatted as string. The parameter may be null. In this case an empty string will
   * be returned.
   * @param pLocale Locale that should be used to format the date. The parameter must not be null.
   * @param pStyle Style that should be used to format the date. The parameter must not be null.
   * @return {@link String} String representing the passed date. The method never returns null.
   */
  @Override
  public String toLocalizedDateTimeString( Date pDate, Locale pLocale, DateFormatStyle pStyle ) {
    // Check parameters
    Check.checkInvalidParameterNull(pLocale, "pLocale");
    Check.checkInvalidParameterNull(pStyle, "pStyle");

    String lLocalizedDateTime;
    if (pDate != null) {
      DateFormat lDateFormat =
          DateFormat.getDateTimeInstance(pStyle.getDateFormatStyle(), pStyle.getTimeFormatStyle(), pLocale);
      lLocalizedDateTime = lDateFormat.format(pDate);
    }
    else {
      lLocalizedDateTime = "";
    }
    return lLocalizedDateTime;
  }

  /**
   * Method returns a date-time string representation of the passed date using the provided locale.
   * 
   * @param pDate Date which will be formatted as string. The parameter may be null. In this case an empty string will
   * be returned.
   * @param pLocale Locale that should be used to format the date. The parameter must not be null.
   * @param pStyle Style that should be used to format the date. The parameter must not be null.
   * @return {@link String} String representing the passed date. The method never returns null.
   */
  @Override
  public String toLocalizedDateTimeString( Calendar pDate, Locale pLocale, DateFormatStyle pStyle ) {
    String lLocalizedDateTime;
    if (pDate != null) {
      lLocalizedDateTime = this.toLocalizedDateTimeString(this.toDate(pDate), pLocale, pStyle);
    }
    else {
      lLocalizedDateTime = "";
    }
    return lLocalizedDateTime;
  }

  /**
   * Method returns a date-time string representation of the passed date using the current locale.
   * 
   * @param pDate Date which will be formatted as string. The parameter may be null. In this case an empty string will
   * be returned.
   * @param pStyle Style that should be used to format the date. The parameter must not be null.
   * @return {@link String} String representing the passed date. The method never returns null.
   */
  @Override
  public String toLocalizedDateTimeString( Date pDate, DateFormatStyle pStyle ) {
    return this.toLocalizedDateTimeString(pDate, XFun.getLocaleProvider().getCurrentLocale(), pStyle);
  }

  /**
   * Method returns a date-time string representation of the passed date using the current locale.
   * 
   * @param pDate Date which will be formatted as string. The parameter may be null. In this case an empty string will
   * be returned.
   * @param pStyle Style that should be used to format the date. The parameter must not be null.
   * @return {@link String} String representing the passed date. The method never returns null.
   */
  @Override
  public String toLocalizedDateTimeString( Calendar pDate, DateFormatStyle pStyle ) {
    return this.toLocalizedDateTimeString(pDate, XFun.getLocaleProvider().getCurrentLocale(), pStyle);
  }

  /**
   * Method returns a date-only string representation of the passed date using the provided locale.
   * 
   * @param pDate Date which will be formatted as string. The parameter may be null. In this case an empty string will
   * be returned.
   * @param pLocale Locale that should be used to format the date. The parameter must not be null.
   * @param pStyle Style that should be used to format the date. The parameter must not be null.
   * @return {@link String} String representing the passed date. The method never returns null.
   */
  @Override
  public String toLocalizedDateOnlyString( Date pDate, Locale pLocale, DateFormatStyle pStyle ) {
    // Check parameters
    Check.checkInvalidParameterNull(pLocale, "pLocale");
    Check.checkInvalidParameterNull(pStyle, "pStyle");

    String lLocalizedDateOnly;
    if (pDate != null) {
      DateFormat lDateFormat = DateFormat.getDateInstance(pStyle.getDateFormatStyle(), pLocale);
      lLocalizedDateOnly = lDateFormat.format(pDate);
    }
    else {
      lLocalizedDateOnly = "";
    }
    return lLocalizedDateOnly;
  }

  /**
   * Method returns a date-only string representation of the passed date using the provided locale.
   * 
   * @param pDate Date which will be formatted as string. The parameter may be null. In this case an empty string will
   * be returned.
   * @param pLocale Locale that should be used to format the date. The parameter must not be null.
   * @param pStyle Style that should be used to format the date. The parameter must not be null.
   * @return {@link String} String representing the passed date. The method never returns null.
   */
  @Override
  public String toLocalizedDateOnlyString( Calendar pDate, Locale pLocale, DateFormatStyle pStyle ) {
    String lLocalizedDateOnly;
    if (pDate != null) {
      lLocalizedDateOnly = this.toLocalizedDateOnlyString(this.toDate(pDate), pLocale, pStyle);
    }
    else {
      lLocalizedDateOnly = "";
    }
    return lLocalizedDateOnly;
  }

  /**
   * Method returns a date-only string representation of the passed date using the current locale.
   * 
   * @param pDate Date which will be formatted as string. The parameter may be null. In this case an empty string will
   * be returned.
   * @param pStyle Style that should be used to format the date. The parameter must not be null.
   * @return {@link String} String representing the passed date. The method never returns null.
   */
  @Override
  public String toLocalizedDateOnlyString( Date pDate, DateFormatStyle pStyle ) {
    return this.toLocalizedDateOnlyString(pDate, XFun.getLocaleProvider().getCurrentLocale(), pStyle);
  }

  /**
   * Method returns a date-only string representation of the passed date using the current locale.
   * 
   * @param pDate Date which will be formatted as string. The parameter may be null. In this case an empty string will
   * be returned.
   * @param pStyle Style that should be used to format the date. The parameter must not be null.
   * @return {@link String} String representing the passed date. The method never returns null.
   */
  @Override
  public String toLocalizedDateOnlyString( Calendar pDate, DateFormatStyle pStyle ) {
    return this.toLocalizedDateOnlyString(pDate, XFun.getLocaleProvider().getCurrentLocale(), pStyle);
  }

  /**
   * Method returns a time-only string representation of the passed date using the provided locale.
   * 
   * @param pDate Date which will be formatted as string. The parameter may be null. In this case an empty string will
   * be returned.
   * @param pLocale Locale that should be used to format the date. The parameter must not be null.
   * @param pStyle Style that should be used to format the date. The parameter must not be null.
   * @return {@link String} String representing the passed date. The method never returns null.
   */
  @Override
  public String toLocalizedTimeOnlyString( Date pDate, Locale pLocale, DateFormatStyle pStyle ) {
    // Check parameters
    Check.checkInvalidParameterNull(pLocale, "pLocale");
    Check.checkInvalidParameterNull(pStyle, "pStyle");

    String lLocalizedTimeOnly;
    if (pDate != null) {
      DateFormat lDateFormat = DateFormat.getTimeInstance(pStyle.getTimeFormatStyle(), pLocale);
      lLocalizedTimeOnly = lDateFormat.format(pDate);
    }
    else {
      lLocalizedTimeOnly = "";
    }
    return lLocalizedTimeOnly;
  }

  /**
   * Method returns a time-only string representation of the passed date using the provided locale.
   * 
   * @param pDate Date which will be formatted as string. The parameter may be null. In this case an empty string will
   * be returned.
   * @param pLocale Locale that should be used to format the date. The parameter must not be null.
   * @param pStyle Style that should be used to format the date. The parameter must not be null.
   * @return {@link String} String representing the passed date. The method never returns null.
   */
  @Override
  public String toLocalizedTimeOnlyString( Calendar pDate, Locale pLocale, DateFormatStyle pStyle ) {
    String lLocalizedTimeOnly;
    if (pDate != null) {
      lLocalizedTimeOnly = this.toLocalizedTimeOnlyString(this.toDate(pDate), pLocale, pStyle);
    }
    else {
      lLocalizedTimeOnly = "";
    }
    return lLocalizedTimeOnly;
  }

  /**
   * Method returns a time-only string representation of the passed date using the current locale.
   * 
   * @param pDate Date which will be formatted as string. The parameter may be null. In this case an empty string will
   * be returned.
   * @param pStyle Style that should be used to format the date. The parameter must not be null.
   * @return {@link String} String representing the passed date. The method never returns null.
   */
  @Override
  public String toLocalizedTimeOnlyString( Date pDate, DateFormatStyle pStyle ) {
    return this.toLocalizedTimeOnlyString(pDate, XFun.getLocaleProvider().getCurrentLocale(), pStyle);
  }

  /**
   * Method returns a time-only string representation of the passed date using the current locale.
   * 
   * @param pDate Date which will be formatted as string. The parameter may be null. In this case an empty string will
   * be returned.
   * @param pStyle Style that should be used to format the date. The parameter must not be null.
   * @return {@link String} String representing the passed date. The method never returns null.
   */
  @Override
  public String toLocalizedTimeOnlyString( Calendar pDate, DateFormatStyle pStyle ) {
    return this.toLocalizedTimeOnlyString(pDate, XFun.getLocaleProvider().getCurrentLocale(), pStyle);
  }

  /**
   * Method returns a string representation of the passed calendar using the passed pattern.
   * 
   * @param pDate Calendar object whose date should be returned as string. The parameter must not be null.
   * @param pPattern Pattern that should be used to format the passed calendar. The parameter must not be null.
   * @return String String representing the date of the calendar. The returned string is formated as described by the
   * pattern @see {@link #TIMESTAMP_PATTERN}.
   */
  @Override
  public String toString( Date pDate, String pPattern ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pDate, "pDate");
    Check.checkInvalidParameterNull(pPattern, "pPattern");

    SimpleDateFormat lDateFormat = new SimpleDateFormat(pPattern);
    return lDateFormat.format(pDate);
  }

  /**
   * Method returns a string representation of the passed calendar using the passed pattern.
   * 
   * @param pDate Calendar object whose date should be returned as string. The parameter must not be null.
   * @param pPattern Pattern that should be used to format the passed calendar. The parameter must not be null.
   * @return String String representing the date of the calendar. The returned string is formated as described by the
   * pattern @see {@link #TIMESTAMP_PATTERN}.
   */
  @Override
  public String toString( Calendar pDate, String pPattern ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pDate, "pDate");
    Check.checkInvalidParameterNull(pPattern, "pPattern");

    return toString(this.toDate(pDate), pPattern);
  }

  /**
   * Method returns a date Object that represents the date with given days from now.
   * 
   * @param pDays Days which will be added to the actual date.
   * @return {@link Date} Created date object. The method never returns null.
   */
  @Override
  public Date addDaysToActualDate( int pDays ) {
    Calendar cal = this.addDaysToActualCalendar(pDays);
    return toDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
  }

  /**
   * Method returns a Calendar object that represents the date with given days from now.
   * 
   * @param pDays Days which will be added to the actual date.
   * @return {@link Calendar} Created calendar object. The method never returns null.
   */
  @Override
  public Calendar addDaysToActualCalendar( int pDays ) {
    Calendar lCalendar = Calendar.getInstance();
    lCalendar.add(Calendar.DAY_OF_MONTH, pDays);
    return lCalendar;
  }

  /**
   * Method returns the difference between the given date and the current date in days.
   * 
   * @param pDate Date object whose time period to the current date should be calculated. The parameter must not be
   * null.
   * @return int Number of days between the current date and the given date. If the passed date is in the future then
   * the method will return a positive value. If the passed date is in the past then a negative value will be returned.
   */
  @Override
  public int calculateTimeDifferenceInDays( Date pDate ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pDate, "pDate");

    // Get each date in milliseconds and calculate the difference.
    long lDiff = pDate.getTime() - System.currentTimeMillis();
    // Convert milliseconds to days.
    return (int) (lDiff / MILLISECONDS_TO_DAYS);
  }

  /**
   * Method returns the difference between the given date and the current date in days.
   * 
   * @param pDate Calendar object whose time period to the current date should be calculated. The parameter must not be
   * null.
   * @return int Number of days between the current date and the given date. If the passed date is in the future then
   * the method will return a positive value. If the passed date is in the past then a negative value will be returned.
   */
  @Override
  public int calculateTimeDifferenceInDays( Calendar pDate ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pDate, "pDate");

    // Calculate time difference in days.
    return this.calculateTimeDifferenceInDays(this.toDate(pDate));
  }

  /**
   * Method returns the difference between the given date and the current date in hours.
   * 
   * @param pDate Date object whose time period to the current date should be calculated. The parameter must not be
   * null.
   * @return int Number of hours between the current date and the given date. If the passed date is in the future then
   * the method will return a positive value. If the passed date is in the past then a negative value will be returned.
   */
  @Override
  public int calculateTimeDifferenceInHours( Date pDate ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pDate, "pDate");

    // Get each date in milliseconds and calculate the difference.
    long lDiff = pDate.getTime() - System.currentTimeMillis();

    // Convert milliseconds to hours.
    return (int) (lDiff / MILLISECONDS_TO_HOURS);
  }

  /**
   * Method returns the difference between the given date and the current date in hours.
   * 
   * @param pDate Calendar object whose time period to the current date should be calculated. The parameter must not be
   * null.
   * @return int Number of hours between the current date and the given date. If the passed date is in the future then
   * the method will return a positive value. If the passed date is in the past then a negative value will be returned.
   */
  @Override
  public int calculateTimeDifferenceInHours( Calendar pDate ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pDate, "pDate");

    // Calculate time difference in hours.
    return this.calculateTimeDifferenceInHours(this.toDate(pDate));
  }

  /**
   * Method returns the difference between the given date and the current date in minutes.
   * 
   * @param pDate Date object whose time period to the current date should be calculated. The parameter must not be
   * null.
   * @return int Number of minutes between the current date and the given date. If the passed date is in the future then
   * the method will return a positive value. If the passed date is in the past then a negative value will be returned.
   */
  @Override
  public int calculateTimeDifferenceInMinutes( Date pDate ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pDate, "pDate");

    // Get each date in milliseconds and calculate the difference.
    long lDiff = pDate.getTime() - System.currentTimeMillis();

    // Convert milliseconds to minutes.
    return (int) (lDiff / MILLISECONDS_TO_MINUTES);
  }

  /**
   * Method returns the difference between the given date and the current date in minutes.
   * 
   * @param pDate Calendar object whose time period to the current date should be calculated. The parameter must not be
   * null.
   * @return int Number of minutes between the current date and the given date. If the passed date is in the future then
   * the method will return a positive value. If the passed date is in the past then a negative value will be returned.
   */
  @Override
  public int calculateTimeDifferenceInMinutes( Calendar pDate ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pDate, "pDate");

    // Calculate time difference in hours.
    return this.calculateTimeDifferenceInMinutes(this.toDate(pDate));
  }

  /**
   * Method returns the difference between the given date and the current date in seconds.
   * 
   * @param pDate Date object whose time period to the current date should be calculated. The parameter must not be
   * null.
   * @return int Number of seconds between the current date and the given date. If the passed date is in the future then
   * the method will return a positive value. If the passed date is in the past then a negative value will be returned.
   */
  @Override
  public int calculateTimeDifferenceInSeconds( Date pDate ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pDate, "pDate");

    // Get each date in milliseconds and calculate the difference.
    long lDiff = pDate.getTime() - System.currentTimeMillis();

    // Convert milliseconds to minutes.
    return (int) (lDiff / MILLISECONDS_TO_SECONDS);
  }

  /**
   * Method returns the difference between the given date and the current date in seconds.
   * 
   * @param pDate Calendar object whose time period to the current date should be calculated. The parameter must not be
   * null.
   * @return int Number of seconds between the current date and the given date. If the passed date is in the future then
   * the method will return a positive value. If the passed date is in the past then a negative value will be returned.
   */
  @Override
  public int calculateTimeDifferenceInSeconds( Calendar pDate ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pDate, "pDate");

    // Calculate time difference in hours.
    return this.calculateTimeDifferenceInSeconds(this.toDate(pDate));
  }

  /**
   * Method returns the date converted to a calendar object.
   * 
   * @param pDate date object which will be converted. The parameter must not be null.
   * @return calendar the object the date was converted to. The method never returns null.
   */
  @Override
  public Calendar toCalendar( Date pDate ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pDate, "pDate");

    Calendar lCalendar = Calendar.getInstance();
    lCalendar.setTime(pDate);
    return lCalendar;
  }

  /**
   * Method returns the FileTime converted to a Calendar object.
   * 
   * @param pFileTime FileTime object which will be converted. The parameter must not be null.
   * @return {@link Calendar} Object the file time was converted to. The method never returns null.
   */
  @Override
  public Calendar toCalendar( FileTime pFileTime ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pFileTime, "pFileTime");

    Calendar lCalendar = Calendar.getInstance();
    lCalendar.setTimeInMillis(pFileTime.toMillis());
    return lCalendar;
  }

  /**
   * Method returns the string converted to a Calendar object.
   * 
   * @param pDateString String object which will be converted. The parameter must not be null.
   * @return {@link Calendar} Object the date string was converted to. The method never returns null.
   */
  @Override
  public Calendar toCalendar( String pDateString ) {
    Date lDate = this.toDate(pDateString);
    return this.toCalendar(lDate);
  }

  /**
   * Converts the given string into a {@link Date}. If it matches this pattern the date will be returned otherwise a
   * Exception will be thrown.
   * 
   * @param pDate the String which will be converted. The parameter must not be null.
   * @param pStrict If parameter is set to <code>true</code> then the passed string strictly has to match with pattern
   * {@link #TIMESTAMP_PATTERN}. If parameter is set to <code>false</code> the closest fitting pattern out of
   * {@link #TIMESTAMP_PATTERN}, {@link #DATE_TIME_PATTERN}, {@link #SHORT_DATE_TIME_PATTERN}, or {@link #DATE_PATTERN}
   * will be chosen.
   * @return Date object that was created from the string. The method never returns null.
   */
  @Override
  public Calendar toCalendar( String pDate, boolean pStrict ) {
    Date lDate = this.toDate(pDate, pStrict);
    return this.toCalendar(lDate);
  }

  /**
   * Checks if the given Date Strings resembles the {@link TIMESTAMP_PATTERN}. If it matches this pattern true will be
   * returned otherwise false.
   * 
   * @param pDate the String which will be tested.
   * @return true if the given String can be converted into a date.
   */
  @Override
  public boolean isDateValid( String pDate ) {
    boolean lIsValid;
    if (pDate != null) {
      try {
        DateFormat lDateFormat = new SimpleDateFormat(TIMESTAMP_PATTERN);
        lDateFormat.setLenient(false);
        lDateFormat.parse(pDate);
        lIsValid = true;
      }
      catch (ParseException e) {
        lIsValid = false;
      }
    }
    else {
      lIsValid = false;
    }
    return lIsValid;
  }
}
