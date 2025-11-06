/**
 * Copyright 2004 - 2019 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.ssl.SSLTools;

@ToolsImplementation(toolsInterface = SSLTools.class)
public class SSLToolsImpl implements SSLTools {

  @Override
  public SSLSocketFactory getAcceptAllCertsSSLSocketFactory( ) {
    return new AcceptAllCertsSSLSocketFactory();
  }

  @Override
  public Class<? extends SSLSocketFactory> getAcceptAllCertsSSLSocketFactoryClass( ) {
    return AcceptAllCertsSSLSocketFactory.class;
  }

  @Override
  public X509TrustManager getAcceptAllCertsTrustManager( ) {
    return new AcceptAllCertsTrustManager();
  }

  @Override
  public Class<? extends X509TrustManager> getAcceptAllCertsTrustManagerClass( ) {
    return AcceptAllCertsTrustManager.class;
  }

  @Override
  public HostnameVerifier getAcceptAllHostnamesVerifier( ) {
    return new AcceptAllHostnamesVerifier();
  }

  @Override
  public Class<? extends HostnameVerifier> getAcceptAllHostnamesVerifierClass( ) {
    return AcceptAllHostnamesVerifier.class;
  }
}
