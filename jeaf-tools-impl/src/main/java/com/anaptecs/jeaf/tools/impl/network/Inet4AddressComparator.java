/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.network;

import java.net.Inet4Address;
import java.util.Comparator;

import com.anaptecs.jeaf.xfun.api.checks.Assert;

/**
 * Class compares two ip addresses with each other.
 */
public class Inet4AddressComparator implements Comparator<Inet4Address> {

  @Override
  public int compare( Inet4Address pFirstAddress, Inet4Address pSecondAddress ) {
    int lResult;
    // Both addresses are not null
    if (pFirstAddress != null && pSecondAddress != null) {
      lResult = this.internalCompareRealAddresses(pFirstAddress, pSecondAddress);
    }
    // Both addresses are null.
    else if (pFirstAddress == null && pSecondAddress == null) {
      lResult = 0;
    }
    // First address is not null.
    else if (pFirstAddress != null) {
      lResult = 1;
    }
    // Second address is not null.
    else {
      lResult = -1;
    }
    // Return result.
    return lResult;
  }

  /**
   * Method compares the passed ip addresses.
   * 
   * @param pFirstAddress First address. The parameter must not be null.
   * @param pSecondAddress Second address. The parameter must not be null.
   * @return int Result of comparison.
   */
  private int internalCompareRealAddresses( Inet4Address pFirstAddress, Inet4Address pSecondAddress ) {
    // Check parameters
    Assert.assertNotNull(pFirstAddress, "pFirstAddress");
    Assert.assertNotNull(pSecondAddress, "pSecondAddress");

    // Compare both addresses
    boolean lFirstIsPrivate = pFirstAddress.isSiteLocalAddress();
    boolean lSecondIsPrivate = pSecondAddress.isSiteLocalAddress();

    // Both addresses are private
    int lResult;
    if (lFirstIsPrivate == true && lSecondIsPrivate == true) {
      lResult = this.compareIPs(pFirstAddress, pSecondAddress);
    }
    // First address is public second is private.
    else if (lFirstIsPrivate == false && lSecondIsPrivate == true) {
      lResult = 1;
    }
    // First address is private second is public.
    else if (lFirstIsPrivate == true && lSecondIsPrivate == false) {
      lResult = -1;
    }
    // Both addresses are not private
    else {
      if (pFirstAddress.isLoopbackAddress() && pSecondAddress.isLoopbackAddress()) {
        lResult = this.compareIPs(pFirstAddress, pSecondAddress);
      }
      else if (pFirstAddress.isLoopbackAddress()) {
        lResult = 1;
      }
      else if (pSecondAddress.isLoopbackAddress()) {
        lResult = -1;
      }
      else {
        lResult = this.compareIPs(pFirstAddress, pSecondAddress);
      }
    }
    return lResult;
  }

  private int compareIPs( Inet4Address pFirstAddress, Inet4Address pSecondAddress ) {
    // Check parameters
    Assert.assertNotNull(pFirstAddress, "pFirstAddress");
    Assert.assertNotNull(pSecondAddress, "pSecondAddress");

    String[] lFirst = pFirstAddress.getHostAddress().split("\\.");
    String[] lSecond = pSecondAddress.getHostAddress().split("\\.");
    int lResult = 0;
    for (int i = 0; i < 4; i++) {
      // Compare each byte
      lResult = Integer.compare(Integer.valueOf(lFirst[i]), Integer.valueOf(lSecond[i]));

      // We have a result.
      if (lResult != 0) {
        break;
      }
    }
    return lResult;
  }

}