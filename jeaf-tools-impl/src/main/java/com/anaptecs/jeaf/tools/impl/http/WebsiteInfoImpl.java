/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.anaptecs.jeaf.tools.api.http.HTTPStatusCode;
import com.anaptecs.jeaf.tools.api.http.WebsiteInfo;
import com.anaptecs.jeaf.tools.api.http.XFrameOptions;

public class WebsiteInfoImpl implements WebsiteInfo {
  /**
   * URL / location of the website.
   */
  private final String location;

  /**
   * Title of the website or an empty string if none is defined.
   */
  private final String title;

  /**
   * HTTP status code that was returned when the information from the website were gathered.
   */
  private final HTTPStatusCode statusCode;

  /**
   * X-Frame-Options as they are defined by the website.
   */
  private final XFrameOptions xFrameOptions;

  /**
   * HTTP header as they were returned from the web server. Map is immutable.
   */
  private final Map<String, String> headers;

  /**
   * Cookies as they were returned from the web server. Map is immutable.
   */
  private final Map<String, String> cookies;

  WebsiteInfoImpl( String pLocation, String pTitle, HTTPStatusCode pStatusCode, XFrameOptions pXFrameOptions,
      Map<String, String> pHeaders, Map<String, String> pCookies ) {

    location = pLocation;
    title = pTitle;
    statusCode = pStatusCode;
    xFrameOptions = pXFrameOptions;
    headers = Collections.unmodifiableMap(new HashMap<>(pHeaders));
    cookies = Collections.unmodifiableMap(new HashMap<>(pCookies));
  }

  /**
   * Method returns the location of the website. Due to redirects / switching from http to htpps and so on the returned
   * location may not be the same as the requested one
   * 
   * @return Location of the website. The method never returns null.
   */
  @Override
  public String getLocation( ) {
    return location;
  }

  /**
   * Method returns the title of the website. The title is taken from the HTML response of the requested URL.
   * 
   * @return String Title of the website. If the website does not hava a title then en empty string will be returned.
   */
  @Override
  public String getTitle( ) {
    return title;
  }

  /**
   * HTTP status code that was returned by the website.
   * 
   * @return {@link StatusCode} Status code that was returned when the website was accessed. The method never returns
   * null. In almost all cases this will be {@link StatusCode#OK}.
   */
  @Override
  public HTTPStatusCode getStatusCode( ) {
    return statusCode;
  }

  /**
   * X-Frame-Options that are configured for the website.
   * 
   * @return {@link XFrameOptions} X-Frame-Options that are configured for the website. The method never returns null.
   */
  @Override
  public XFrameOptions getXFrameOptions( ) {
    return xFrameOptions;
  }

  /**
   * Method returns a map with all http headers that were part of the response.
   * 
   * @return {@link Map} Map with all http header. The method never returns null. The map is not modifiable.
   */
  @Override
  public Map<String, String> getHeaders( ) {
    return headers;
  }

  /**
   * Method returns a map with all cookies that were part of the response.
   * 
   * @return {@link Map} Map with all cookies. The method never returns null. The map is not modifiable.
   */
  @Override
  public Map<String, String> getCookies( ) {
    return cookies;
  }
}
