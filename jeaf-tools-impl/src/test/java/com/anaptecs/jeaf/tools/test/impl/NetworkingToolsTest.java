/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.anaptecs.jeaf.tools.annotations.NetworkingToolsConfig;
import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.network.NetworkingTools;
import com.anaptecs.jeaf.tools.api.regexp.RegExpTools;
import com.anaptecs.jeaf.tools.impl.DefaultToolsConfiguration;
import com.anaptecs.jeaf.tools.impl.network.Inet4AddressComparator;
import com.anaptecs.jeaf.tools.impl.network.NetworkingToolsConfiguration;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import com.anaptecs.jeaf.xfun.api.info.OperatingSystem;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

@NetworkingToolsConfig(pingTimeout = -1)
public class NetworkingToolsTest {
  /**
   * Test cases test JEAF's networking tools.
   *
   * @throws IOException
   */
  @Test
  public void testIsReachable( ) throws SocketException, IOException {
    // Test ping
    NetworkingTools lNetworkingTools = Tools.getNetworkingTools();
    boolean lReachable = lNetworkingTools.isReachable("127.0.0.1");

    assertTrue("Ping test for 127.0.0.1 failed.", lReachable);

    // Test standard ping.
    lReachable = lNetworkingTools.isReachable("www.heise.de");

    // In case of Linux root privileges are required to execute ping, which is usually will not be the case.
    OperatingSystem lOperatingSystem = XFun.getInfoProvider().getOperatingSystem();
    if (lOperatingSystem == OperatingSystem.LINUX || lOperatingSystem == OperatingSystem.MAC) {
      assertFalse("Due to missing root privileges ping is expected to fail.", lReachable);
    }
    else {
      assertTrue(lReachable);
    }

    // Expecting timeout.
    assertFalse(lNetworkingTools.isReachable("www.heise.de", 1));

    try {
      lNetworkingTools.isReachable("Invalid IP");
    }
    catch (JEAFSystemException e) {
      TestCase.assertEquals("Wrong error code", ToolsMessages.UNKNOWN_HOST, e.getErrorCode());
    }
  }

  @Test
  public void testIPAdressResolution( ) {
    NetworkingTools lNetworkingTools = Tools.getNetworkingTools();
    List<Inet4Address> lIPs = lNetworkingTools.getIPv4Addresses();
    System.out.println(lIPs);
    assertTrue(lIPs.size() >= 1);
    for (Inet4Address lAddress : lIPs) {
      assertFalse(lAddress.isLoopbackAddress());
    }

    List<String> lHardwareAddresses = lNetworkingTools.getHardwareAddresses();
    System.out.println(lHardwareAddresses);
    RegExpTools lRegExpTools = Tools.getRegExpTools();

    for (String lNextMAC : lHardwareAddresses) {
      assertTrue(lRegExpTools.matchesPattern(lNextMAC, "^([0-9A-F]{2}[\\\\.:-]){5}([0-9A-F]{2})$"));
    }
  }

  @Test
  public void testGetHostname( ) {
    String lHostname = Tools.getNetworkingTools().getHostname();
    assertNotNull(lHostname);
  }

  @Test
  public void testInet4AddressComparator( ) throws IOException {
    Inet4Address lLoopback = (Inet4Address) InetAddress.getByName("127.0.0.1");
    Inet4Address lPrivate1 = (Inet4Address) InetAddress.getByName("192.168.178.1");
    Inet4Address lPrivate2 = (Inet4Address) InetAddress.getByName("192.168.178.2");
    Inet4Address lPrivate3 = (Inet4Address) InetAddress.getByName("10.0.0.1");
    Inet4Address lPrivate4 = (Inet4Address) InetAddress.getByName("172.16.1.100");
    Inet4Address lPublic1 = (Inet4Address) InetAddress.getByName("159.69.102.178");
    Inet4Address lPublic2 = (Inet4Address) InetAddress.getByName("23.236.62.147");

    List<Inet4Address> lIPs = new ArrayList<>();
    lIPs.add(lPrivate4);
    lIPs.add(lPrivate1);
    lIPs.add(lPrivate2);
    lIPs.add(lPublic2);
    lIPs.add(lPublic1);
    lIPs.add(lPrivate3);
    lIPs.add(lLoopback);

    Inet4AddressComparator lComparator = new Inet4AddressComparator();
    lIPs.sort(lComparator);
    System.out.println(lIPs);
    assertEquals(lPrivate3, lIPs.get(0));
    assertEquals(lPrivate4, lIPs.get(1));
    assertEquals(lPrivate1, lIPs.get(2));
    assertEquals(lPrivate2, lIPs.get(3));
    assertEquals(lPublic2, lIPs.get(4));
    assertEquals(lPublic1, lIPs.get(5));
    assertEquals(lLoopback, lIPs.get(6));

    assertEquals(0, lComparator.compare(lLoopback, lLoopback));
    assertEquals(0, lComparator.compare(null, null));
    assertEquals(-1, lComparator.compare(lPublic2, lLoopback));
    assertEquals(1, lComparator.compare(lLoopback, lPublic2));
    assertEquals(-1, lComparator.compare(lPrivate2, lPublic2));
    assertEquals(-1, lComparator.compare(null, lPublic2));
    assertEquals(1, lComparator.compare(lPrivate2, null));

    assertTrue(lLoopback.isLoopbackAddress());

    lIPs.clear();
    lIPs.add(lPrivate1);
    lIPs.add(lPublic1);
    lIPs.sort(lComparator);
    assertEquals(lPrivate1, lIPs.get(0));
    assertEquals(lPublic1, lIPs.get(1));

    lIPs.clear();
    lIPs.add(lPrivate1);
    lIPs.add(lPrivate2);
    lIPs.sort(lComparator);
    assertEquals(lPrivate1, lIPs.get(0));
    assertEquals(lPrivate2, lIPs.get(1));

    lIPs.clear();
    lIPs.add(lPublic1);
    lIPs.add(lPrivate1);
    lIPs.sort(lComparator);
    assertEquals(lPrivate1, lIPs.get(0));
    assertEquals(lPublic1, lIPs.get(1));

    lIPs.clear();
    lIPs.add(lPublic1);
    lIPs.add(lPublic2);
    lIPs.sort(lComparator);
    assertEquals(lPublic2, lIPs.get(0));
    assertEquals(lPublic1, lIPs.get(1));
  }

  @Test
  public void testToURL( ) {
    NetworkingTools lNetworkingTools = Tools.getNetworkingTools();
    URL lURL = lNetworkingTools.toURL("http://www.anaptecs.de");
    assertEquals("www.anaptecs.de", lURL.getHost());
    assertEquals(-1, lURL.getPort());
    assertEquals(80, lURL.getDefaultPort());
    assertEquals("http", lURL.getProtocol());
    assertEquals("", lURL.getPath());
    assertEquals(null, lURL.getQuery());
    assertEquals(null, lURL.getRef());

    lURL = lNetworkingTools.toURL("https://my.weeasy.de/weeasy/1.3/desktop/login#!login");
    assertEquals("my.weeasy.de", lURL.getHost());
    assertEquals(-1, lURL.getPort());
    assertEquals(443, lURL.getDefaultPort());
    assertEquals("https", lURL.getProtocol());
    assertEquals("/weeasy/1.3/desktop/login", lURL.getPath());
    assertEquals(null, lURL.getQuery());
    assertEquals("!login", lURL.getRef());

    // Test exception handling
    try {
      lNetworkingTools.toURL("invalid URL");
      fail("Exception expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.INVALID_URL, e.getErrorCode());
    }

    try {
      lNetworkingTools.toURL("unkown.url");
      fail("Exception expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.INVALID_URL, e.getErrorCode());
    }
    try {
      lNetworkingTools.toURL(null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testNetworkingToolsConfiguration( ) {
    // Test default configuration
    NetworkingToolsConfiguration lConfiguration = new NetworkingToolsConfiguration();
    NetworkingToolsConfig lAnnotation = DefaultToolsConfiguration.class.getAnnotation(NetworkingToolsConfig.class);
    List<String> lErrors = lConfiguration.checkCustomConfiguration(lAnnotation);
    assertEquals(0, lErrors.size());

    // Test custom configuration
    lAnnotation = NetworkingToolsTest.class.getAnnotation(NetworkingToolsConfig.class);
    lErrors = lConfiguration.checkCustomConfiguration(lAnnotation);
    assertEquals(1, lErrors.size());
    assertEquals("'pingTimeout' must be zero or greater. Current value is -1", lErrors.get(0));

    // Test empty configuration.
    NetworkingToolsConfig lEmptyConfiguration = lConfiguration.getEmptyConfiguration();
    assertEquals(NetworkingToolsConfig.class, lEmptyConfiguration.annotationType());
    assertEquals(NetworkingToolsConfig.DEFAULT_PING_TIMEOUT, lEmptyConfiguration.pingTimeout());
  }
}
