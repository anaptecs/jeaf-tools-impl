/**
 * Copyright 2004 - 2015 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;

/**
 * Class implements a SSL Socket Factory that accepts all kinds of SSL certificates. This especially includes
 * self-signed and not valid certificates.
 * 
 * The only difference of this SSL socket factory to the default implementation is that a special {@link TrustManager}
 * is used which accepts all certificates.
 * 
 * @author JEAF Development Team
 * @version JEAF Release 1.3
 */
public class AcceptAllCertsSSLSocketFactory extends SSLSocketFactory {

  /**
   * Socket factory to which all requests will be delegated.
   */
  private SSLSocketFactory socketFactory;

  /**
   * Initialize object. Thereby a new SSL socket factory will be created.
   */
  public AcceptAllCertsSSLSocketFactory( ) {
    this("TLS");
  }

  /**
   * Initialize object. Thereby a new SSL socket factory will be created.
   * 
   * @param pProtocol Secure socket protocol that should be used. The parameter must not be null and must be a supported
   * protocol according to class {@link SSLContext#getInstance(String)}
   */
  public AcceptAllCertsSSLSocketFactory( String pProtocol ) {
    try {
      SSLContext lContext = SSLContext.getInstance(pProtocol);
      lContext.init(null, new TrustManager[] { new AcceptAllCertsTrustManager() }, new SecureRandom());
      socketFactory = lContext.getSocketFactory();
    }
    catch (GeneralSecurityException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_CREATE_SSL_SOCKET_FACTORY, e);
    }
  }

  /**
   * Method always returns a new instance of this class as default socket factory.
   * 
   * @return {@link SocketFactory} Default socket factory. The method never returns null.
   */
  public static SocketFactory getDefault( ) {
    return new AcceptAllCertsSSLSocketFactory();
  }

  @Override
  public String[] getDefaultCipherSuites( ) {
    return socketFactory.getDefaultCipherSuites();
  }

  @Override
  public String[] getSupportedCipherSuites( ) {
    return socketFactory.getSupportedCipherSuites();
  }

  @Override
  public Socket createSocket( ) throws IOException {
    return socketFactory.createSocket();
  }

  @Override
  public Socket createSocket( Socket pSocket, InputStream pConsumed, boolean pAutoClose ) throws IOException {
    return socketFactory.createSocket(pSocket, pConsumed, pAutoClose);
  }

  @Override
  public Socket createSocket( Socket pSocket, String pHost, int pPort, boolean pAutoClose ) throws IOException {
    return socketFactory.createSocket(pSocket, pHost, pPort, pAutoClose);
  }

  @Override
  public Socket createSocket( String pHost, int pPort ) throws IOException, UnknownHostException {
    return socketFactory.createSocket(pHost, pPort);
  }

  @Override
  public Socket createSocket( InetAddress pHost, int pPort ) throws IOException {
    return socketFactory.createSocket(pHost, pPort);
  }

  @Override
  public Socket createSocket( String pHost, int pPort, InetAddress pLocalHost, int pLocalPort )
    throws IOException, UnknownHostException {
    return socketFactory.createSocket(pHost, pPort, pLocalHost, pLocalPort);
  }

  @Override
  public Socket createSocket( InetAddress pAddress, int pPort, InetAddress pLocalAddress, int pLocalPort )
    throws IOException {
    return socketFactory.createSocket(pAddress, pPort, pLocalAddress, pLocalPort);
  }
}