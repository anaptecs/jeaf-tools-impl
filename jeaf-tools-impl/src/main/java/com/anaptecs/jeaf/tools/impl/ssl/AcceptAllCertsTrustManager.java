/**
 * Copyright 2004 - 2015 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Class implements a trust manager that accepts all certificates no matter if they are valid or not.
 * 
 * @author JEAF Development Team
 * @version JEAF Release 1.3
 */
public class AcceptAllCertsTrustManager implements X509TrustManager {

  @Override
  public void checkClientTrusted( X509Certificate[] xcs, String string ) throws CertificateException {
    // Nothing to do.
  }

  @Override
  public void checkServerTrusted( X509Certificate[] xcs, String string ) throws CertificateException {
    // Nothing to do.
  }

  @Override
  public X509Certificate[] getAcceptedIssuers( ) {
    // Return empty certificate array. This causes that all certificates will be accepted.
    return new java.security.cert.X509Certificate[0];
  }
}
