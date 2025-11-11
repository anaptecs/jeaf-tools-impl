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
import java.net.BindException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.ssl.SSLTools;
import com.anaptecs.jeaf.tools.impl.ssl.AcceptAllCertsSSLSocketFactory;
import com.anaptecs.jeaf.tools.impl.ssl.AcceptAllCertsTrustManager;
import com.anaptecs.jeaf.tools.impl.ssl.AcceptAllHostnamesVerifier;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import com.anaptecs.jeaf.xfun.api.info.OperatingSystem;
import org.junit.jupiter.api.Test;

public class SSLToolsTest {

  @Test
  public void testAcceptAllCertsTrustManager( ) throws GeneralSecurityException {
    AcceptAllCertsTrustManager lTrustManager = new AcceptAllCertsTrustManager();

    // Trust manager accepts anything. At least this is our expectation.
    lTrustManager.checkClientTrusted(null, null);
    lTrustManager.checkServerTrusted(null, null);

    X509Certificate[] lIssuers = lTrustManager.getAcceptedIssuers();
    assertNotNull(lIssuers);
    assertEquals(0, lIssuers.length);
  }

  @Test
  public void testAcceptAllHostnamesVerifier( ) {
    AcceptAllHostnamesVerifier lVerifier = new AcceptAllHostnamesVerifier();
    assertTrue(lVerifier.verify(null, null));
    assertTrue(lVerifier.verify("www.jeaf.de", null));
  }

  @Test
  public void testAcceptAllCertsSSLSocketFactory( ) throws IOException {
    AcceptAllCertsSSLSocketFactory lSocketFactory = new AcceptAllCertsSSLSocketFactory();

    Socket lSocket = lSocketFactory.createSocket();
    assertNotNull(lSocket);
    assertFalse(lSocket.isConnected());
    lSocket.close();
    assertTrue(lSocket.isClosed());

    lSocket = lSocketFactory.createSocket("www.anaptecs.de", 443);
    assertNotNull(lSocket);
    assertTrue(lSocket.isConnected());
    lSocket.close();
    assertTrue(lSocket.isClosed());

    lSocket = lSocketFactory.createSocket(InetAddress.getByName("www.anaptecs.de"), 443);
    assertNotNull(lSocket);
    assertTrue(lSocket.isConnected());
    lSocket.close();
    assertTrue(lSocket.isClosed());

    if (XFun.getInfoProvider().getOperatingSystem() == OperatingSystem.WINDOWS) {
      try {
        lSocket = lSocketFactory.createSocket("www.anaptecs.de", 443, InetAddress.getLocalHost(), 19988);
        assertNotNull(lSocket);
        assertTrue(lSocket.isConnected());
        lSocket.close();
        assertTrue(lSocket.isClosed());

        lSocket = lSocketFactory.createSocket(InetAddress.getByName("www.anaptecs.de"), 443, InetAddress.getLocalHost(),
            9988);
        assertNotNull(lSocket);
        assertTrue(lSocket.isConnected());
        lSocket.close();
        assertTrue(lSocket.isClosed());
      }
      catch (BindException e) {
        System.out.println("Ignoring BindException: " + e.getMessage());
      }
    }

    try {
      lSocketFactory.createSocket(InetAddress.getByName("www.anaptecs.de"), 443, InetAddress.getLocalHost(), -1);
      fail("Expecting exception when using invalid port number.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    Socket lPlainSocket = new Socket("www.anaptecs.de", 443);
    lSocket = lSocketFactory.createSocket(lPlainSocket, null, true);
    assertNotNull(lSocket);
    assertTrue(lSocket.isConnected());
    lSocket.close();
    lPlainSocket.close();

    lPlainSocket = new Socket("www.anaptecs.de", 443);
    lSocket = lSocketFactory.createSocket(lPlainSocket, "www.anaptecs.de", 443, true);
    assertNotNull(lSocket);
    assertTrue(lSocket.isConnected());
    lSocket.close();

    SocketFactory lDefault = AcceptAllCertsSSLSocketFactory.getDefault();
    assertNotNull(lDefault);
    assertTrue(lDefault instanceof AcceptAllCertsSSLSocketFactory);

    String[] lDefaultCipherSuites = lSocketFactory.getDefaultCipherSuites();
    assertTrue(lDefaultCipherSuites.length > 0);

    String[] lSupportedCipherSuites = lSocketFactory.getSupportedCipherSuites();
    assertTrue(lSupportedCipherSuites.length > 0);

    // Test error handling in case of an invalid protocol.
    try {
      lSocketFactory = new AcceptAllCertsSSLSocketFactory("InvalidSSLProtocol");
      fail();
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_CREATE_SSL_SOCKET_FACTORY, e.getErrorCode());
    }
  }

  @Test
  public void testSSLToolsImpl( ) {
    SSLTools lSSLTools = Tools.getSSLTools();

    SSLSocketFactory lSSLSocketFactory = lSSLTools.getAcceptAllCertsSSLSocketFactory();
    assertNotNull(lSSLSocketFactory);
    assertEquals(AcceptAllCertsSSLSocketFactory.class, lSSLSocketFactory.getClass());

    assertEquals(AcceptAllCertsSSLSocketFactory.class, lSSLTools.getAcceptAllCertsSSLSocketFactoryClass());

    X509TrustManager lTrustManager = lSSLTools.getAcceptAllCertsTrustManager();
    assertNotNull(lTrustManager);
    assertEquals(AcceptAllCertsTrustManager.class, lTrustManager.getClass());
    assertEquals(AcceptAllCertsTrustManager.class, lSSLTools.getAcceptAllCertsTrustManagerClass());

    HostnameVerifier lVerifier = lSSLTools.getAcceptAllHostnamesVerifier();
    assertNotNull(lVerifier);
    assertEquals(AcceptAllHostnamesVerifier.class, lVerifier.getClass());
    assertEquals(AcceptAllHostnamesVerifier.class, lSSLTools.getAcceptAllHostnamesVerifierClass());
  }
}
