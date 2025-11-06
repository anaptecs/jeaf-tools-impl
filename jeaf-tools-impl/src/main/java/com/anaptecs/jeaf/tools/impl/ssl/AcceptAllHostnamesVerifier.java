/**
 * Copyright 2004 - 2016 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Class implements a host name verifier that accepts all hostnames.
 * 
 * @author JEAF DEvelopment Team
 * @version JEAF Release 1.3
 */
public class AcceptAllHostnamesVerifier implements HostnameVerifier {
  /**
   * Method accepts all host names.
   */
  @Override
  public boolean verify( String hostname, SSLSession session ) {
    return true;
  }
}
