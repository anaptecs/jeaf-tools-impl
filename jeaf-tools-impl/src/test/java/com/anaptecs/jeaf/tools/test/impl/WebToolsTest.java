/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Map;

import javax.net.ssl.SSLException;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.http.HTTPStatusCode;
import com.anaptecs.jeaf.tools.api.http.URLDetails;
import com.anaptecs.jeaf.tools.api.http.WebTools;
import com.anaptecs.jeaf.tools.api.http.WebsiteInfo;
import com.anaptecs.jeaf.tools.api.http.XFrameOptions;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import com.anaptecs.jeaf.xfun.api.info.JavaRelease;
import org.jsoup.HttpStatusException;
import org.junit.jupiter.api.Test;

public class WebToolsTest {
  @Test
  public void testGetWebsiteInfo( ) throws IOException {
    WebTools lWebTools = Tools.getWebTools();

    // Test standard request
    int lTimeout = 5000;
    WebsiteInfo lWebsiteInfo = lWebTools.lookupWebsiteInfo("https://www.heise.de", lTimeout, true, false);

    assertEquals("heise online - IT-News, Nachrichten und Hintergründe | heise online", lWebsiteInfo.getTitle());
    assertEquals("https://www.heise.de", lWebsiteInfo.getLocation());
    assertEquals(HTTPStatusCode.OK, lWebsiteInfo.getStatusCode());
    assertEquals(XFrameOptions.DENY, lWebsiteInfo.getXFrameOptions());

    Map<String, String> lHeaders = lWebsiteInfo.getHeaders();
    assertEquals("text/html; charset=utf-8", lHeaders.get("Content-Type"));
    Map<String, String> lCookies = lWebsiteInfo.getCookies();
    assertEquals(0, lCookies.size());

    // Execute request without checking SSL certificates.
    lWebsiteInfo = lWebTools.lookupWebsiteInfo("http://www.heise.de", lTimeout, true, true);
    assertEquals("heise online - IT-News, Nachrichten und Hintergründe | heise online", lWebsiteInfo.getTitle());
    assertEquals("https://www.heise.de/", lWebsiteInfo.getLocation());
    assertEquals(HTTPStatusCode.OK, lWebsiteInfo.getStatusCode());
    assertEquals(XFrameOptions.DENY, lWebsiteInfo.getXFrameOptions());

    // Try to access site that is password protected.
    try {
      lWebTools.lookupWebsiteInfo("https://subversion.anaptecs.de/svn/JEAF/jeaf-tools/api/trunk", 30000, true, false);
      fail("Exception expected when trying to connect to website with access restriction.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_LOOKUP_WEBSITE_INFORMATION, e.getErrorCode());
      JavaRelease lJavaRelease = XFun.getInfoProvider().getJavaRuntimeEnvironment().getJavaRelease();

      // If Java version is lower than Java 11 then SSL protocol version is not supported. That's why in this case
      // another exception occurs.
      if (lJavaRelease.isLower(JavaRelease.JAVA_11)) {
        assertEquals(SSLException.class, e.getCause().getClass());
      }
      else {
        assertEquals(HttpStatusException.class, e.getCause().getClass());
      }
    }
  }

  @Test
  public void testCreateBasicAuthorizationHeader( ) {
    WebTools lWebTools = Tools.getWebTools();
    String lHeader = lWebTools.createBasicAuthorizationHeader("user", "password");
    assertEquals("Basic dXNlcjpwYXNzd29yZA==", lHeader);
    lHeader = lWebTools.createBasicAuthorizationHeader("user", "");
    assertEquals("Basic dXNlcjo=", lHeader);
    lHeader = lWebTools.createBasicAuthorizationHeader("user", null);
    assertEquals("Basic dXNlcjo=", lHeader);
    lHeader = lWebTools.createBasicAuthorizationHeader("", "HelloWorld!");
    assertEquals("Basic OkhlbGxvV29ybGQh", lHeader);
    lHeader = lWebTools.createBasicAuthorizationHeader(null, "HelloWorld!");
    assertEquals("Basic OkhlbGxvV29ybGQh", lHeader);
    lHeader = lWebTools.createBasicAuthorizationHeader("", "");
    assertEquals("Basic Og==", lHeader);
    lHeader = lWebTools.createBasicAuthorizationHeader(null, null);
    assertEquals("Basic Og==", lHeader);
  }

  @Test
  public void testURLDetails( ) throws IOException {
    String lURLString =
        "https://FFC-powerpoint.officeapps.live.com/p/PowerPointFrame.aspx?WOPISrc=http%3A%2F%2Flocalhost%3A8080%2Fweeasy%2F1.3%2Fdesktop%2Fwopi%2Ffiles%2F1-----B-----N5-&New=1&rs=de-DE&ui=de-DE&IsLicensedUser=0&PowerPointView=EditView";
    URLDetails lURLDetails = Tools.getWebTools().getURLDetails(lURLString);

    // Check standard info
    assertEquals("https", lURLDetails.getProtocol(), "Wrong protocol");
    assertEquals("FFC-powerpoint.officeapps.live.com", lURLDetails.getHost(), "Wrong host");
    assertEquals(443, lURLDetails.getPort(), "Wrong port");
    assertEquals("/p/PowerPointFrame.aspx", lURLDetails.getPath(), "Wrong path");
    assertEquals(
        "WOPISrc=http%3A%2F%2Flocalhost%3A8080%2Fweeasy%2F1.3%2Fdesktop%2Fwopi%2Ffiles%2F1-----B-----N5-&New=1&rs=de-DE&ui=de-DE&IsLicensedUser=0&PowerPointView=EditView",
        lURLDetails.getQuery(), "Wrong query");
    assertEquals(lURLString, lURLDetails.getURLAsString(), "Wrong URL");
    assertNotNull(lURLDetails.getURL(), "URL object not returned");
  }

  @Test
  public void testIsValidURL( ) {
    WebTools lWebTools = Tools.getWebTools();
    assertTrue(lWebTools.isValidURL("https://www.heise.de"));
    assertTrue(lWebTools.isValidURL("https://www.heise.de/abc"));
    assertTrue(lWebTools.isValidURL("https://www.heise.de/abc fdsfd"));
    assertFalse(lWebTools.isValidURL("htts:?/www.heise.de"));
    assertFalse(lWebTools.isValidURL(" hello "));
    assertFalse(lWebTools.isValidURL(null));
  }

  @Test
  public void testUnescapeHTML( ) {
    WebTools lWebTools = Tools.getWebTools();
    assertEquals("JEAF © by anaptecs GmbH", lWebTools.unescapeHtml("JEAF &copy; by anaptecs GmbH"));
    assertEquals("Price 30.00€", lWebTools.unescapeHtml("Price 30.00&euro;"));
  }
}
