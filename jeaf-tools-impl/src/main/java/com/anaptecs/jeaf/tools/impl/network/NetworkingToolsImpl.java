/**
 * Copyright 2004 - 2015 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.network;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.network.NetworkingTools;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.checks.Check;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import com.anaptecs.jeaf.xfun.api.info.OperatingSystem;

/**
 * Class implements JEAFs Networking Tools Helper.
 * 
 * @author JEAF Development Team
 * @version JEAF Release 1.3
 */
@ToolsImplementation(toolsInterface = NetworkingTools.class)
public class NetworkingToolsImpl implements NetworkingTools {
  private int pingTimeout;

  public NetworkingToolsImpl( ) {
    NetworkingToolsConfiguration lConfiguration = new NetworkingToolsConfiguration();
    pingTimeout = lConfiguration.getPingTimeout();
  }

  /**
   * Method returns the host name of this machine.
   * 
   * @return String Host name of this machine. Method never returns null.
   */
  @Override
  public String getHostname( ) {
    try {
      return InetAddress.getLocalHost().getHostName();
    }
    catch (UnknownHostException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_RESOLVE_NETWORK_INFO, e, e.getMessage());
    }
  }

  /**
   * Method returns all IPv4 addresses of the machine. Loop back addresses will not be returned as well as addresses
   * from interfaces that are not up and running. If the JVM has public and private IPv4 addresses then the public will
   * be ordered before the private addresses.
   * 
   * @return {@link List} List with all public IPv4 addresses of this JVM. The method never returns null.
   */
  @Override
  public List<Inet4Address> getIPv4Addresses( ) {
    try {
      // Resolve all "real" network interfaces that are known.
      List<NetworkInterface> lNetworkInterfaces = this.getNetworkInterfaces();
      List<Inet4Address> lMyIPs = new ArrayList<>(lNetworkInterfaces.size());
      for (NetworkInterface lNextInterface : lNetworkInterfaces) {
        Enumeration<InetAddress> lAddresses = lNextInterface.getInetAddresses();
        while (lAddresses.hasMoreElements()) {
          InetAddress lNextIP = lAddresses.nextElement();
          if (lNextIP instanceof Inet4Address) {
            lMyIPs.add((Inet4Address) lNextIP);
          }
        }
      }
      Collections.sort(lMyIPs, new Inet4AddressComparator());
      return lMyIPs;
    }
    // Handle network exceptions.
    catch (SocketException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_RESOLVE_NETWORK_INFO, e, e.getMessage());
    }
  }

  /**
   * Method returns all real hardware addresses of this machine. "real" in this case means that virtual network adapters
   * are ignored as well as loop back adapters and adapters with invalid mac addresses.
   * 
   * @return List List of all hardware addresses. The Strings are formated in the typical MAC address style
   * AA:AA:AA:11:11:11. The method never returns null.
   */
  @Override
  public List<String> getHardwareAddresses( ) {
    try {
      // Resolve all "real" network interfaces that are known.
      List<NetworkInterface> lNetworkInterfaces = this.getNetworkInterfaces();
      List<String> lMACs = new ArrayList<>(lNetworkInterfaces.size());
      for (NetworkInterface lNextInterface : lNetworkInterfaces) {
        byte[] lMAC = lNextInterface.getHardwareAddress();

        if (lMAC != null && lMAC.length <= 6) {
          StringBuilder lStringBuilder = new StringBuilder(18);
          for (byte b : lMAC) {
            if (lStringBuilder.length() > 0)
              lStringBuilder.append(':');
            lStringBuilder.append(String.format("%02x", b));
          }
          lMACs.add(lStringBuilder.toString().toUpperCase());
        }
      }
      return lMACs;
    }
    // Handle network exceptions.
    catch (SocketException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_RESOLVE_NETWORK_INFO, e, e.getMessage());
    }
  }

  /**
   * Method checks if the passed IP or host is reachable.
   * 
   * @param pHostAddress IP address or IP that should be check. The parameter must not be null and a valid IP or host
   * name.
   * @param pTimeout Timeout in milliseconds.
   * @return boolean Method returns true in case that the host is reachable or false if not.
   */
  @Override
  public boolean isReachable( String pHostAddress ) {
    return this.isReachable(pHostAddress, pingTimeout);
  }

  /**
   * Method checks if the passed IP or host is reachable.
   * 
   * @param pHostAddress IP address or IP that should be check. The parameter must not be null and a valid IP or host
   * name.
   * @param pTimeout Timeout in milliseconds.
   * @return boolean Method returns true in case that the host is reachable or false if not.
   */
  @Override
  public boolean isReachable( String pHostAddress, int pTimeout ) {
    try {
      InetAddress lHost = InetAddress.getByName(pHostAddress);
      boolean lReachable = lHost.isReachable(pTimeout);

      // In case of Linux root privileges are required to execute ping, which is what we basically do here.
      if (XFun.getInfoProvider().getOperatingSystem() == OperatingSystem.LINUX && lReachable == false) {
        XFun.getTrace().warn("Host " + pHostAddress
            + " seems to be not available, but please be aware that on Linux root privileges are required to execute ping.");
      }
      return lReachable;
    }
    catch (UnknownHostException e) {
      throw new JEAFSystemException(ToolsMessages.UNKNOWN_HOST, e, e.getMessage());
    }
    catch (IOException e) {
      throw new JEAFSystemException(ToolsMessages.NETWORK_ERROR_ON_PING, e, e.getMessage());
    }
  }

  /**
   * Method creates a new {@link URL} from the passed string.
   * 
   * @param pURLString String that should be used to create a {@link URL} object. The parameter must not be null.
   * @return {@link URL} URL object that was created using the passed string.
   * @throws JEAFSystemException if the passed string is not a valid URL.
   */
  @Override
  public URL toURL( String pURLString ) {
    // Check parameter
    Check.checkInvalidParameterNull(pURLString, "pURLString");

    try {
      return new URL(pURLString);
    }
    catch (MalformedURLException e) {
      throw new JEAFSystemException(ToolsMessages.INVALID_URL, e, pURLString);
    }
  }

  private List<NetworkInterface> getNetworkInterfaces( ) throws SocketException {
    // Resolve all network interfaces that are known.
    Enumeration<NetworkInterface> lInterfaces = NetworkInterface.getNetworkInterfaces();
    List<NetworkInterface> lNetworkInterfaces = new ArrayList<>();
    while (lInterfaces.hasMoreElements()) {
      NetworkInterface lNextInterface = lInterfaces.nextElement();

      // We are only interested in "real" interfaces that are up and running.
      if (lNextInterface.isLoopback() == false && lNextInterface.isPointToPoint() == false
          && lNextInterface.isVirtual() == false && lNextInterface.isUp() == true) {
        lNetworkInterfaces.add(lNextInterface);
      }
    }
    return lNetworkInterfaces;
  }

}